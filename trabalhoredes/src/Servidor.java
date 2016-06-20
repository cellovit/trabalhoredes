
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
    int nSeqEsperado;
    int ACK;
    
    public void recebeArquivo(String caminhoArquivo){
        
        if(!new File(caminhoArquivo).exists())  new File(caminhoArquivo).mkdirs();
    }
}
