

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
    static final int timeoutTime = 200;
    static final int TAMANHOPACOTE = 1000;
    static final int tamanhoJanela = 5;
    int nSeqAtual = 0;
    boolean ultimoNseq = false;
    Timer timer;
    
    

    public Cliente() {
    }
    
    public static void recebeAck () throws SocketException, UnknownHostException, IOException{
        InetAddress IPAddress = InetAddress.getLocalHost();
        byte[] data = new byte[TAMANHOPACOTE];
        
        DatagramSocket s = new DatagramSocket();
        DatagramPacket p = new DatagramPacket(data, data.length, IPAddress, PORTASERVIDOR);

        s.receive(p);

        data = p.getData();
        int ACK = ByteBuffer.wrap(data, 0, 4).getInt();
        
        System.out.println("ACK " + ACK + " recebido");
        
    }
    
    //public FileOutputStream preparaArquivo2(String caminhoArquivo){
    public byte[] preparaArquivo(String caminhoArquivo) throws IOException {

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

    }
    
    public int getnSeqAtual() {
        return nSeqAtual;
    }

    public void setnSeqAtual(int nSeqAtual) {
        this.nSeqAtual = nSeqAtual;
    }

    public DatagramPacket preparaPacote( InetAddress IPAddress, int nSeq, byte[] sendData) throws SocketException, IOException {
        
        byte[] nSeqBytes = ByteBuffer.allocate(4).putInt(nSeq).array();
        byte[] sendDataArray = ByteBuffer.allocate(sendData.length).put(sendData).array();
        byte[] data = new byte[nSeqBytes.length + sendDataArray.length];
        
        //insere as informações do numero de sequencia
        for (int i = 0; i < nSeqBytes.length; i++){
            data[i] = nSeqBytes[i];
        }
        
        //insere os dados
        for (int i = nSeqBytes.length; i < sendDataArray.length; i++){
            data[i] = sendData[i];
        }
        
        DatagramPacket p = new DatagramPacket(data, data.length, IPAddress, PORTASERVIDOR);
        return p;
    }
    
    public static void main(String args[]) throws Exception {

        ArrayList<DatagramPacket> listaPacotes = new ArrayList<DatagramPacket>();
        int nSeqAtual = 0;
        boolean transferenciaCompleta = false;
        //int ssthresh = 16;
        int tamanhoJanela = 10;
        int nSeq = 0;
        int bytesLidos = 0;
        
        Cliente cliente = new Cliente();
        Scanner input = new Scanner(System.in);
        InetAddress IPAddress = InetAddress.getLocalHost();

        System.out.println("Insira o caminho do arquivo que deseja enviar");
        String caminhoArquivoUpload = input.nextLine();
        byte[] bufferArquivo = cliente.preparaArquivo(caminhoArquivoUpload);
        System.out.println("tamanho do Arquivo + informações : " + bufferArquivo.length);
        
        DatagramSocket Cliente = new DatagramSocket();
        
        int bytesRestantes = bufferArquivo.length;
        while (bytesLidos < bufferArquivo.length) {

            byte[] data = new byte[TAMANHOPACOTE];
            for (int i = 0; i < TAMANHOPACOTE; i++) {
                data[i] = bufferArquivo[i];
                bytesLidos++;
                bytesRestantes--;
                if (bytesRestantes == 0) {
                    break;
                }
            }

            System.out.println("bytes lidos : " + bytesLidos);
            System.out.println("bytes restantes : " + bytesRestantes);
            //????? o numero de sequencia atual deve ser o numero de bytes lidos ?

            Cliente.setSoTimeout(timeoutTime);
            Cliente.send(cliente.preparaPacote(IPAddress, nSeq, data));
            System.out.println("pacote " + nSeq + " enviado");
            nSeq = bytesLidos;
            recebeAck();
        }
        Cliente.close();
    }
}
