
import java.net.*;
import java.io.*;
import java.nio.ByteBuffer;
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
public class Servidor {

    static final int portaServidor = 8002;
    static final int tamanhoPacote = 1000;

    int ultimoNSeq = -1;
    int nSeqEsperado = 0;
    FileOutputStream fos = null;
    byte[] data = new byte[tamanhoPacote];
    

    public Servidor() {

    }

    public void recebePacote(InetAddress IPAddress) throws IOException {
        ServerSocket s = new ServerSocket (portaServidor);
        DatagramPacket p = new DatagramPacket(data, data.length, IPAddress, portaServidor);
        while (true) {
            //DatagramSocket s = new DatagramSocket(portaServidor);
            System.out.println("accept");
            //s.accept();
            //s.bind(s.getLocalSocketAddress());
            data = p.getData();
            int nSeq = ByteBuffer.wrap(data, 0, 4).getInt();

            if (nSeqEsperado == nSeq) {
                try {
                    // primeiro pacote
                    if ((ultimoNSeq == -1) && (nSeqEsperado == 0)) {

                        Scanner input = new Scanner(System.in);
                        System.out.println("Insira onde deseja salvar o arquivo");
                        String caminhoArquivo = input.nextLine();

                        if (!new File(caminhoArquivo).exists()) {
                            new File(caminhoArquivo).mkdirs();
                        }

                        int tamanhoNomeArquivo = ByteBuffer.wrap(data, 0, 4).getInt();
                        String nomeArquivo = ByteBuffer.wrap(data, 4, 4 + tamanhoNomeArquivo).toString();
                        System.out.println("Servidor -> Nome do arquivo : " + nomeArquivo);

                        File f = new File(caminhoArquivo + nomeArquivo);
                        if (!f.exists()) {
                            f.createNewFile();
                        }

                        //dados iniciais
                        fos = new FileOutputStream(f);
                        fos.write(data, 4 + tamanhoNomeArquivo, p.getLength() - 4 - tamanhoNomeArquivo);

                        ultimoNSeq = nSeq;
                        nSeqEsperado += p.getLength();
                        enviaAck(nSeq, IPAddress);
                    } else {
                        fos.write(data);
                        enviaAck(nSeq, IPAddress);
                        ultimoNSeq = nSeq;
                        nSeqEsperado += p.getLength();
                    }

                } catch (SocketException e1) {
                    e1.printStackTrace();
                }
            } else {//gera ACK duplicado
                enviaAck(ultimoNSeq, IPAddress);

            }
        }
    }

    public void enviaAck(int ack, InetAddress IPAddress) throws IOException {
        byte[] ackBytes = ByteBuffer.allocate(4).putInt(ack).array();

        DatagramPacket p = new DatagramPacket(ackBytes, ackBytes.length, IPAddress, portaServidor);
        try {
            DatagramSocket s = new DatagramSocket(portaServidor);
            s.send(p);
        } catch (SocketException e1) {
            e1.printStackTrace();
        }

        System.out.println("Servidor -> ACK " + ack + "enviado");
    }
    
    public static void main(String args[]) throws Exception {
        
    }
}
