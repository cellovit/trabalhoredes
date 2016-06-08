
import java.util.Timer;
import java.util.Vector;
import java.net.*;
import java.io.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.CRC32;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author marcelo
 */
public class Cliente {
    
    static final int tamanho_pacote = 1000;
    static final int porta_servidor = 8002;
    static final int janela_congestionamento = 10;
    static final int timeout_time = 500;
    
    int base;	//numero base da janela
    int nSeqAtual; //numero de sequencia atual
    int proxnseq; //prox numero de sequencia da janela
    String caminhoArquivo; //caminho do arquivo a ser enviado
    String nomeArquivo; //nome do arquivo a ser salvo no servidor
    ArrayList<byte[]> listaDePacotes;	// lista dos pacotes gerados
    Timer timer;	
    boolean transferenciaCompleta;
    
    //BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
    
    public Cliente(String caminhoArquivo, String nomeArquivo, int portaOrigem, int portaDestino){
        
        int base = 0;
        int proxnseq = 0;
        this.caminhoArquivo = caminhoArquivo;
        this.nomeArquivo = nomeArquivo;
        listaDePacotes = new ArrayList<byte[]>(janela_congestionamento);
        
        
    }
    
    //constroi o pacote com as informações do cabeçalho
    public byte[] geraPacote(int nSeqatual, byte[] data){
	byte[] seqNumBytes = ByteBuffer.allocate(4).putInt(nSeqAtual).array(); //numero de sequencia com 4 bytes
	ByteBuffer pktBuf = ByteBuffer.allocate( 4 + data.length);
        //insere as informações no buffer
        pktBuf.put(seqNumBytes);
	pktBuf.put(data);
	return pktBuf.array();
    }
    
    public void pktout(){
        
    }
    
    // retorna -1 se pacote recebido está corrompido, senão retorna numero do ACK
    public int decodificaPacote(){
        return -1;
    }
    
    public void pktin(){
        
    }
    
    public class Timeout extends TimerTask{
		
	}// END CLASS Timeout
    
}
