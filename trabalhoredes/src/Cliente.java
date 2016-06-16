import java.io.*;
import java.net.*;
import java.util.Scanner;
        

public class Cliente {
    
    static final int portaServidor = 8002;
    static final int tamanhoPacote = 1000;
    
    public Cliente(){}
    
    public static byte[] preparaArquivo(String caminhoArquivo) throws IOException{
        
        File f = new File(caminhoArquivo);
        FileInputStream fis = new FileInputStream(f);
        int tamanhoArquivo = fis.available();
        
        System.out.println("Nome do arquivo : " + f.getName());
        System.out.println("Tamanho arquivo : " + f.length());
        
        byte[] sendData = new byte[tamanhoPacote];
        for (int i = 0; i < f.length(); ){
            sendData[i] = (byte)fis.read();
        }
        return sendData;
    }
    
    public void enviaPacote (byte[] pacote, InetAddress IPAddress, int nSeq) throws SocketException, IOException{
        
        DatagramPacket p = new DatagramPacket(pacote, pacote.length, IPAddress, portaServidor);
        DatagramSocket s = new DatagramSocket();
        s.send(p);
    }
}
