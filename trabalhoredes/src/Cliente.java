
import java.util.Timer;
import java.util.Vector;
import java.net.*;
import java.io.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
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
    
    
    int nSeqAtual; //numero de sequencia atual
    int proxNseq; //prox numero de sequencia da janela
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
    public byte[] geraPacote(int nSeq, byte[] data){
	byte[] seqNumBytes = ByteBuffer.allocate(4).putInt(nSeq).array(); //numero de sequencia com 4 bytes
	ByteBuffer pktBuf = ByteBuffer.allocate( 4 + data.length);
        //insere as informações no buffer
        pktBuf.put(seqNumBytes);
	pktBuf.put(data);
	return pktBuf.array();
    }
    
    public void pktout(){
        try{
            InetAddress IpAddress = InetAddress.getLocalHost();
            FileInputStream fileInputStream = new FileInputStream(new File(caminhoArquivo));
            
            //enquanto tiverem pacotes para serem recebidos pelo servidor
            while(!transferenciaCompleta){
                //manda pacotes se a janela ainda não estiver cheia
                if(proxNseq < nSeqAtual + janela_congestionamento){
                   
                    if (nSeqAtual == proxNseq) setTimer(true); //primeiro pacote inicia o timer
                    
                    byte[] sendData = new byte[10];
                    boolean ultimoNseq = false;
                    
                    //se o pacote estiver na lista de pacotes pega ele na lista de pacotes
                    if(proxNseq < listaDePacotes.size()){
                        sendData = listaDePacotes.get(proxNseq);
                    }else{
                        //se for o primeiro pacote do conjunto, acrescenta as informações do arquivo
                        if(proxNseq == 0){
                            byte[] nomeArquivoB = nomeArquivo.getBytes();
                            byte[] tamanhoNomeArquivoB = ByteBuffer.allocate(4).putInt(nomeArquivo.length()).array();
                            byte[] bufferArquivo = new byte [tamanho_pacote];
                            int tamanhoArquivo = fileInputStream.read(bufferArquivo, 0, tamanho_pacote - 4 - nomeArquivoB.length);
                            byte[] ArquivoB = Arrays.copyOfRange(bufferArquivo, 0, tamanhoArquivo);
                            ByteBuffer bb = ByteBuffer.allocate(4 + nomeArquivoB.length + ArquivoB.length);
                            bb.put(tamanhoNomeArquivoB); bb.put(nomeArquivoB); bb.put(ArquivoB);
                            sendData = geraPacote(proxNseq, bb.array());
                        }else{
                            byte[] bufferArquivo = new byte[tamanho_pacote];
                            int tamanhoArquivo = fileInputStream.read(bufferArquivo, 0, tamanho_pacote);
                            if (tamanhoArquivo == -1){
                                ultimoNseq = true;
                                sendData = geraPacote(proxNseq, new byte[0]);
                            }else{
                                byte[] ArquivoB = Arrays.copyOfRange(bufferArquivo, 0, tamanhoArquivo);
                                sendData = geraPacote(proxNseq, ArquivoB);
                            }
                        }
                        listaDePacotes.add(sendData);
                    }
                    //mandar pacote
                    
                    //atualizar variavel 'proxNseq'
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
        
    }
    
    // retorna -1 se pacote recebido está corrompido, senão retorna numero do ACK
    public int decodificaPacote(){
        return -1;
    }
    
    public void pktin(){
        
    }
    
    // to start or stop the timer
    public void setTimer(boolean isNewTimer){
	if (timer != null) timer.cancel();
	if (isNewTimer){
            timer = new Timer();
            timer.schedule(new Timeout(), timeout_time);
	}
    }
    
    public class Timeout extends TimerTask{
		
    }// END CLASS Timeout
    
}
