
import java.net.*;
import java.io.*;


/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author marcelo
 */
public class Servidor {
    
    static final int porta_servidor = 8002;
    static final int tamanho_pacote = 1000;
    
    public void recebeArquivo(String caminhoArquivo){
        
        if(!new File(caminhoArquivo).exists())  new File(caminhoArquivo).mkdirs();
    }
    
    public static void main(String args[]) throws Exception{
        
        DatagramSocket serverSocket = new DatagramSocket(porta_servidor);
        byte[] receiveData = new byte[tamanho_pacote];
        byte[] sendData = new byte[tamanho_pacote];
        
        while(true){
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            serverSocket.receive(receivePacket);
            
            InetAddress IPAddress = receivePacket.getAddress();
            int port = receivePacket.getPort();
            
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
            // Envio em si do datagrama.
            serverSocket.send(sendPacket);
        }
    }
    
}
