

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

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
    static final int tamanhoPacote = 1000;
    
    
    public static void main(String args[]) throws Exception {
        
        boolean transferenciaCompleta = false;
        int ssthresh = 16;
        int tamanhoJanela = 10;
        
        Cliente cliente = new Cliente();
        int nSeqAtualCliente = cliente.getnSeqAtual();
        
        Scanner input = new Scanner (System.in);
        // Resolução de nome de host.
        InetAddress IPAddress = InetAddress.getLocalHost();
        
        System.out.println("Insira o caminho do arquivo que deseja enviar");
        String caminhoArquivo = input.nextLine();
        
        byte[] bufferArquivo = cliente.preparaArquivo(caminhoArquivo);
        
        for (int i = 0; i < bufferArquivo.length; i++){
            System.out.println(bufferArquivo[i]);
        }
        
    }
    
}
