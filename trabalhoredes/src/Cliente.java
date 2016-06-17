
import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Scanner;

public class Cliente {

    static final int portaServidor = 8002;
    static final int tamanhoPacote = 1000;
    static final int tamanhoJanela = 5;
    int nSeqAtual = 0; boolean ultimoNseq = false;

    public Cliente() {
    }

    public byte[] preparaArquivo(String caminhoArquivo) throws IOException {

        File f = new File(caminhoArquivo);
        FileInputStream fis = new FileInputStream(f);
        int bytesArquivo = fis.available();
        String nomeArquivo = f.getName();

        //if (tamanhoArquivo != 0){
        System.out.println("Nome do arquivo : " + f.getName());
        System.out.println("Tamanho arquivo : " + bytesArquivo);

        byte[] sendData = new byte[tamanhoPacote];
        //se for o primeiro pacote do conjunto, acrescenta as informações do arquivo
        if (nSeqAtual == 0) {
            byte[] nomeArquivoB = nomeArquivo.getBytes();
            byte[] tamanhoNomeArquivoB = ByteBuffer.allocate(4).putInt(nomeArquivo.length()).array();
            byte[] bufferArquivo = new byte[tamanhoPacote];
            int tamanhoArquivo = fis.read(bufferArquivo, 0, tamanhoPacote - 4 - nomeArquivoB.length);
            byte[] ArquivoB = Arrays.copyOfRange(bufferArquivo, 0, tamanhoArquivo);
            ByteBuffer bb = ByteBuffer.allocate(4 + nomeArquivoB.length + ArquivoB.length);
            bb.put(tamanhoNomeArquivoB);
            bb.put(nomeArquivoB);
            bb.put(ArquivoB);
            return preparaPacote(nSeqAtual, bb.array());
        } else {
            byte[] bufferArquivo = new byte[tamanhoPacote];
            int tamanhoArquivo = fis.read(bufferArquivo, 0, tamanhoPacote);
            //sem dados para ser lidos
            if (tamanhoArquivo == -1) {
                ultimoNseq = true;
                return sendData = preparaPacote(nSeqAtual, new byte[0]);
            }else {
                byte[] ArquivoB = Arrays.copyOfRange(bufferArquivo, 0, tamanhoArquivo);
                return sendData = preparaPacote(nSeqAtual, ArquivoB);
            }
        }
    }
    
    public byte[] preparaPacote(int nSeq, byte[] sendData) {

        byte[] nSeqBytes = ByteBuffer.allocate(4).putInt(nSeq).array();

        ByteBuffer bufferPacote = ByteBuffer.allocate(4 + sendData.length); //4 bytes pro numero de sequencia + tamanho do pacote
        bufferPacote.put(nSeqBytes);  //insere os bytes do numero de sequencia no buffer
        bufferPacote.put(sendData); //insere dados no buffer
        return bufferPacote.array();
    }

    public int getnSeqAtual() {
        return nSeqAtual;
    }

    public void setnSeqAtual(int nSeqAtual) {
        this.nSeqAtual = nSeqAtual;
    }

    public void enviaPacote(byte[] pacote, InetAddress IPAddress) throws SocketException, IOException {
        DatagramPacket p = new DatagramPacket(pacote, pacote.length, IPAddress, portaServidor);
        DatagramSocket s = new DatagramSocket();
        //s.setSoTimeout(300);
        s.send(p);
    }
}
