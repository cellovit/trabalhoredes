

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

public class Cliente {

    static final int portaServidor = 8002;
    static final int timeoutTime = 200;
    static final int tamanhoPacote = 1000;
    static final int tamanhoJanela = 5;
    int nSeqAtual = 0;
    boolean ultimoNseq = false;
    Timer timer;

    public Cliente() {
    }
    
    public void recebeAck (){
        
    }
    
    //public FileOutputStream preparaArquivo2(String caminhoArquivo){
    public byte[] preparaArquivo(String caminhoArquivo) throws IOException {

        File f = new File(caminhoArquivo);
        FileInputStream fis = new FileInputStream(f);
        int bytesArquivo = fis.available();
        String nomeArquivo = f.getName();

        System.out.println("Nome do arquivo : " + f.getName());
        System.out.println("Tamanho arquivo : " + bytesArquivo);

        byte[] nomeArquivoB = nomeArquivo.getBytes();
        byte[] tamanhoNomeArquivoB = ByteBuffer.allocate(4).putInt(nomeArquivo.length()).array();
        byte[] bufferArquivo = new byte[bytesArquivo];
        
        byte[] ArquivoB = Arrays.copyOfRange(bufferArquivo, 0, bytesArquivo);
        ByteBuffer bb = ByteBuffer.allocate(4 + nomeArquivoB.length + ArquivoB.length);
        bb.put(tamanhoNomeArquivoB);
        bb.put(nomeArquivoB);
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
        
        //ByteBuffer bufferPacote; //4 bytes pro numero de sequencia + tamanho do pacote
        //bufferPacote = ByteBuffer.allocateDirect(4 + sendData.length);
        //os 4 primeiros bytes do pacote definirão o numero de sequencia
        //bufferPacote.put(sendData);  //insere os bytes do numero de sequencia no buffer
        //bufferPacote.put(nSeqBytes); //insere dados no buffer
        
        DatagramPacket p = new DatagramPacket(data, data.length, IPAddress, portaServidor);
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

            byte[] data = new byte[tamanhoPacote];
            for (int i = 0; i < tamanhoPacote; i++) {
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
        }
        Cliente.close();
    }
}
