
import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
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
        //int tamanhoArquivo = fis.read(bufferArquivo, 0, tamanhoPacote - 4 - nomeArquivoB.length);
        byte[] ArquivoB = Arrays.copyOfRange(bufferArquivo, 0, bytesArquivo);
        ByteBuffer bb = ByteBuffer.allocate(4 + nomeArquivoB.length + ArquivoB.length);
        bb.put(tamanhoNomeArquivoB);
        bb.put(nomeArquivoB);
        bb.put(ArquivoB);
        
        return bb.array();

    }

    //prepara um pacote do tamanho da janela com o numero de sequencia atual
    //public DatagramPacket preparaPacote(int nSeq, byte[] sendData, InetAddress IPAddress) {


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
        for (int i = 0; i < sendDataArray.length; i++){
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
}
