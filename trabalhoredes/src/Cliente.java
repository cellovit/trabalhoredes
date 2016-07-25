
import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

public class Cliente {

    static final int PORTASERVIDOR = 8002;
    static final int TIMEOUTTIME = 1000;
    static final int TAMANHOPACOTE = 1000;
    int nSeq;
    int ACKesperado;

    public Cliente(int nSeq, int ACKesperado) {
        this.nSeq = nSeq;
        this.ACKesperado = ACKesperado;
    }

    public static boolean recebeACK2(DatagramSocket ClienteSocket, DatagramSocket sk1, byte[] bufferArquivo, byte[] data, int bytesLidos, int bytesRestantes, int nSeq, int ACKesperado) throws SocketException, IOException {

        Cliente c = new Cliente(nSeq, ACKesperado);
        InetAddress IPAddress = InetAddress.getByName("127.0.0.1");
        boolean ACKrecebido = false;

        byte[] ackBytes = ByteBuffer.allocate(4).putInt(nSeq).array();

        DatagramPacket ACK = new DatagramPacket(ackBytes, ackBytes.length, IPAddress, 8001);
        System.out.println("esperando ACK");

        try {
            sk1.setSoTimeout(TIMEOUTTIME);
            sk1.receive(ACK);
        } catch (SocketTimeoutException e) { //retransmite se acusar exceção de timeout
            System.out.println("RETRANSMITINDO PACOTE POR TIMEOUT");
            return false;

        }

        int numeroACK = ByteBuffer.wrap(ackBytes, 0, 4).getInt();
        System.out.println("ACK " + numeroACK + " recebido \n");
        if (numeroACK != ACKesperado) {
            System.out.println("ACK contém numero de sequencia não esperado, RETRANSMITIR pacote");

            if (bytesLidos == 1000) {
                ClienteSocket.send(c.preparaPacote(IPAddress, nSeq, data));
                System.out.println("retransmitindo primeiro pacote " + nSeq + " enviado");
                ACKrecebido = recebeACK2(ClienteSocket, sk1, bufferArquivo, data, bytesLidos, bytesRestantes, nSeq, ACKesperado);

            } else if (bytesLidos > 1000) {

                ClienteSocket.send(c.preparaPacote(IPAddress, nSeq, data));
                System.out.println("retransmitindo pacote " + nSeq + " enviado");
                ACKrecebido = recebeACK2(ClienteSocket, sk1, bufferArquivo, data, bytesLidos, bytesRestantes, nSeq, ACKesperado);
            } else if (bytesLidos > 1000 && bytesRestantes <= 1000) {
                byte[] ultimoPacote = new byte[1];

                ClienteSocket.send(c.preparaPacote(IPAddress, nSeq, ultimoPacote));
                System.out.println("retransmitindo ultimo pacote " + nSeq + " enviado");
                ACKrecebido = recebeACK2(ClienteSocket, sk1, bufferArquivo, data, bytesLidos, bytesRestantes, nSeq, ACKesperado);
            }
        } else {
            ACKrecebido = true;
        }
        return ACKrecebido;
    }

    public static void recebeAck() throws SocketException, UnknownHostException, IOException {

        byte[] data = new byte[TAMANHOPACOTE];

        DatagramSocket s = new DatagramSocket();
        DatagramPacket p = new DatagramPacket(data, data.length, s.getLocalAddress(), PORTASERVIDOR);
        s.setSoTimeout(TIMEOUTTIME);
        s.receive(p);

        data = p.getData();
        int ACK = ByteBuffer.wrap(data, 0, 4).getInt();

        System.out.println("ACK " + ACK + " recebido");

    }

    //public FileOutputStream preparaArquivo2(String caminhoArquivo){
    /*public byte[] preparaArquivo(String caminhoArquivo) throws IOException {

        File f = new File(caminhoArquivo);
        FileInputStream fis = new FileInputStream(f);
        int bytesArquivo = fis.available();

        System.out.println("Nome do arquivo : " + f.getName());
        System.out.println("Tamanho arquivo : " + bytesArquivo);

        byte[] bufferArquivo = new byte[bytesArquivo];

        byte[] ArquivoB = Arrays.copyOfRange(bufferArquivo, 0, bytesArquivo);
        ByteBuffer bb = ByteBuffer.allocate(ArquivoB.length);

        bb.put(ArquivoB);

        return bb.array();

    }*/
    public byte[] preparaArquivo2(String caminhoArquivo) throws IOException {

        File f = new File(caminhoArquivo);
        System.out.println("Nome do arquivo : " + f.getName());
        System.out.println("Tamanho arquivo : " + f.length());

        byte[] bufferArquivo = new byte[(int) f.length()];

        FileInputStream fis = new FileInputStream(f);
        fis.read(bufferArquivo);
        fis.close();

        return bufferArquivo;
    }

    

    public DatagramPacket preparaPacote(InetAddress IPAddress, int nSeq, byte[] sendData) throws SocketException, IOException {

        byte[] nSeqBytes = ByteBuffer.allocate(4).putInt(nSeq).array();

        byte[] sendDataArray = ByteBuffer.allocate(sendData.length).put(sendData).array();

        byte[] data = new byte[nSeqBytes.length + sendDataArray.length];

        //insere as informações do numero de sequencia
        for (int i = 0; i < (nSeqBytes.length); i++) {
            data[i] = nSeqBytes[i];
        }

        //insere os dados
        for (int i = nSeqBytes.length; i < (sendDataArray.length); i++) {
            data[i] = sendData[i];
        }

        DatagramPacket p = new DatagramPacket(data, data.length, IPAddress, PORTASERVIDOR);
        return p;
    }

    public static void main(String args[]) throws Exception {

        int nSeq = 0;
        int bytesLidos = 0;
        int ACKesperado = 1000;
        boolean ACKrecebido = false;
        boolean primeiroPacoteEnviado = false;
        boolean transferenciaCompleta = false;
        boolean ultimo = false;
        Cliente cliente = new Cliente(nSeq, ACKesperado);
        Scanner input = new Scanner(System.in);
        System.out.println("Insira o caminho do arquivo que deseja enviar");
        String caminhoArquivoUpload = input.nextLine();
        byte[] bufferArquivo = cliente.preparaArquivo2(caminhoArquivoUpload);

        DatagramSocket ClienteSocket = new DatagramSocket();
        InetAddress IPAddress = InetAddress.getByName("127.0.0.1");
        DatagramSocket sk1 = new DatagramSocket(8001);

        int bytesRestantes = bufferArquivo.length;
        while (bytesLidos < bufferArquivo.length && !transferenciaCompleta) {
            ACKrecebido = false;
            int tPacote = 0;

            if (bytesRestantes >= 1000) {
                tPacote = TAMANHOPACOTE;
            } else {
                tPacote = bytesRestantes;
            }
            byte[] data = new byte[tPacote];

            for (int i = 0; i < tPacote; i++) {
                data[i] = bufferArquivo[i];
                bytesLidos++;
                bytesRestantes--;
            }

            System.out.println("bytes lidos : " + bytesLidos);
            System.out.println("bytes restantes : " + bytesRestantes);
            while (!ACKrecebido && !transferenciaCompleta) {
                if (bytesRestantes < 1000){ultimo = true;}
                if (bytesLidos > 1000 && nSeq >= 1000 && primeiroPacoteEnviado) {

                    ClienteSocket.send(cliente.preparaPacote(IPAddress, nSeq, data));
                    System.out.println("pacote " + nSeq + " enviado");
                    ACKrecebido = recebeACK2(ClienteSocket, sk1, bufferArquivo, data, bytesLidos, bytesRestantes, nSeq, ACKesperado);
                    if (ACKrecebido) {
                        nSeq = bytesLidos;
                    }
                } 
                else if (bytesLidos <= 1000) {

                    ClienteSocket.send(cliente.preparaPacote(IPAddress, nSeq, data));
                    System.out.println("PRIMEIRO pacote " + nSeq + " enviado");
                    primeiroPacoteEnviado = true;
                    //System.out.println("TRANSFERENCIA INICIADA \n");
                    ACKrecebido = recebeACK2(ClienteSocket, sk1, bufferArquivo, data, bytesLidos, bytesRestantes, nSeq, ACKesperado);
                    if (ACKrecebido) {
                        nSeq = bytesLidos;
                    }
                    
                }else if (ultimo) {
                    byte[] ultimoPacote = new byte[tPacote];

                    ClienteSocket.send(cliente.preparaPacote(IPAddress, nSeq, ultimoPacote));
                    System.out.println("ULTIMO pacote " + nSeq + " enviado");
                    ACKrecebido = recebeACK2(ClienteSocket, sk1, bufferArquivo, data, bytesLidos, bytesRestantes, nSeq, ACKesperado);
                    if (ACKrecebido) {
                        nSeq = bytesLidos;
                        System.out.println("TRANSFERENCIA COMPLETA \n");
                        transferenciaCompleta = true;
                        sk1.close();
                        ClienteSocket.close();
                        
                        
                        break;
                    }
                } 
            }
            ACKesperado += tPacote;
            //if (transferenciaCompleta) break;
        }
        
    }
}
