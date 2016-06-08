
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
    
    static final int tamanho_pacote = 1000;
    static final int porta_servidor = 8002;
    static final int janela_congestionamento = 10;
    static final int timeout_time = 500;
    
    public Servidor (int porta_s1, int porta_s2, String dir){
        
        DatagramSocket s1; //socket receptor de solicitações
        DatagramSocket s2; //socket que responde solicitações
        System.out.println("Porta :" + porta_s1);
        
        int proxNumSeq = 1; //proximo numero de sequencia a ser recebido
        boolean transferFlag = false;  //variavel para verificação de transferencia completa
        
        
    }
    
    public byte[] geraPacote(int nSeqatual, byte[] data){
        
    }
    
    
    public void pktin(){
        
    }
    
}
