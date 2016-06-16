import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.Scanner;
        

public class Cliente {
    
    static final int portaServidor = 8002;
    static final int tamanhoPacote = 1000;
    int nSeqAtual;
    
    public Cliente(){}
    
    public static byte[] preparaArquivo(String caminhoArquivo) throws IOException{
        
        File f = new File(caminhoArquivo);
        FileInputStream fis = new FileInputStream(f);
        int tamanhoArquivo = fis.available();
        
        //if (tamanhoArquivo != 0){
        System.out.println("Nome do arquivo : " + f.getName());
        System.out.println("Tamanho arquivo : " + f.length());
        
        byte[] sendData = new byte[tamanhoPacote];
        for (int i = 0; i < tamanhoArquivo; i++ ){
            sendData[i] = (byte)fis.read();
        }
        return sendData;
    }
    
    //método que constroi um unico pacote de determinado numero de sequencia com o cabeçalho + dados
    public byte[] preparaPacote(int nSeq, byte[] sendData){
        
        ByteBuffer bufferPacote = ByteBuffer.allocate(4 + tamanhoPacote); //4 bytes pro numero de sequencia + tamanho do pacote
        bufferPacote.putInt(nSeq);  //insere o numero de sequencia no buffer
        bufferPacote.put(sendData[nSeq]); //insere dados no buffer
        return bufferPacote.array();
    }

    public int getnSeqAtual() {
        return nSeqAtual;
    }

    public void setnSeqAtual(int nSeqAtual) {
        this.nSeqAtual = nSeqAtual;
    }
    
    public void enviaPacote (byte[] pacote, InetAddress IPAddress, int nSeq) throws SocketException, IOException{
        
        
        DatagramPacket p = new DatagramPacket(pacote, pacote.length, IPAddress, portaServidor);
        DatagramSocket s = new DatagramSocket();
        //s.setSoTimeout(300);
        s.send(p);
    }
}
