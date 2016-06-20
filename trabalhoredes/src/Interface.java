
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Timer;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author marcelo
 */
public class Interface {

    static final int portaServidor = 8002;
    static final int timeoutTime = 200;
    static final int tamanhoPacote = 1000;
    static final int tamanhoJanela = 5;

    boolean ultimoNseq = false;
    Timer timer;

    public static void main(String args[]) throws Exception {

        ArrayList<DatagramPacket> listaPacotes = new ArrayList<DatagramPacket>();
        int nSeqAtual = 0;
        boolean transferenciaCompleta = false;
        //int ssthresh = 16;
        int tamanhoJanela = 10;
        ServerSocket servSckt = null;
        int bytesLidos = 0;
        FileInputStream fis = null;
        BufferedInputStream bis = null;
        OutputStream os = null;

        Cliente cliente = new Cliente();
        int nSeqAtualCliente = cliente.getnSeqAtual();

        Scanner input = new Scanner(System.in);
        // Resolução de nome de host.
        InetAddress IPAddress = InetAddress.getLocalHost();

        System.out.println("Insira o caminho do arquivo que deseja enviar");
        String caminhoArquivo = input.nextLine();

        byte[] bufferArquivo = cliente.preparaArquivo(caminhoArquivo);
        System.out.println("tamanho do Arquivo + informações : " + bufferArquivo.length);
        int bytesRestantes = bufferArquivo.length;
        while (bytesLidos < bufferArquivo.length) {
            
            
                byte[] data = new byte[tamanhoPacote];
                for (int i = 0; i <  tamanhoPacote; i++) {
                    data[i] = bufferArquivo[i];
                    bytesLidos++;
                    bytesRestantes--;   
                    if(bytesRestantes == 0) break;
                }
                
                System.out.println("bytes lidos : " + bytesLidos);
                System.out.println("bytes restantes : " + bytesRestantes);
                //listaPacotes.add(cliente.preparaPacote(IPAddress, bytesLidos, data));  //????? o numero de sequencia atual deve ser o numero de bytes lidos ?
                
                //nSeqAtual++;
            
            
            //System.out.println("total de pacotes : " + listaPacotes.size());
            

                DatagramSocket s = new DatagramSocket();
                
                s.setSoTimeout(timeoutTime);
                s.send(cliente.preparaPacote(IPAddress, bytesLidos, data));
                System.out.println("pacote " + bytesLidos + " enviado");
        }

        

    }
}
