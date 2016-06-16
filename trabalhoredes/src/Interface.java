

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
        int tamanhoJanela = 1;
        
        Cliente cliente = new Cliente();
        int nSeqAtualCliente = cliente.getnSeqAtual();
        
        Scanner input = new Scanner (System.in);
        // Resolução de nome de host.
        InetAddress IPAddress = InetAddress.getByName("hostname");
        
        System.out.println("Insira o caminho do arquivo que deseja enviar");
        String caminhoArquivo = input.nextLine();
        
        byte[] bufferArquivo = cliente.preparaArquivo(caminhoArquivo);
        
        while(!transferenciaCompleta){
            for (int i = cliente.getnSeqAtual(); i < tamanhoJanela; i++){
                System.out.println("Enviando pacote com numero de sequencia : " + nSeqAtualCliente);
                cliente.enviaPacote(cliente.preparaPacote(nSeqAtualCliente, bufferArquivo), IPAddress, i);
            }
            if (tamanhoJanela < ssthresh){
                tamanhoJanela *= 2;
            }
            nSeqAtualCliente++;
        }
        
        
    }
    
}
