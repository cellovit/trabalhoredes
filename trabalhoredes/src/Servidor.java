
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

    static final int PORTASERVIDOR = 8002;
    static final int TAMANHOPACOTE = 1000;

    public Servidor() {

    }

    public static void enviaAck(int ack, InetAddress IPAddress) throws IOException {
        byte[] ackBytes = ByteBuffer.allocate(4).putInt(ack).array();

        DatagramPacket p = new DatagramPacket(ackBytes, ackBytes.length, IPAddress, PORTASERVIDOR);
        try {
            DatagramSocket s = new DatagramSocket();
            s.send(p);
        } catch (SocketException e1) {
            e1.printStackTrace();
        }

        System.out.println("Servidor -> ACK " + ack + " enviado");
    }

    public static void main(String args[]) throws UnknownHostException, SocketException, IOException {

        InetAddress IPAddress = InetAddress.getLocalHost();
        int ultimoNSeq = -1;
        int nSeqEsperado = 0;
        FileOutputStream fos = null;
        byte[] data = new byte[TAMANHOPACOTE];

        while (true) {
            DatagramSocket s = new DatagramSocket(PORTASERVIDOR);
            DatagramPacket p = new DatagramPacket(data, data.length, IPAddress, PORTASERVIDOR);

            s.receive(p);

            data = p.getData();
            int nSeq = ByteBuffer.wrap(data, 0, 4).getInt();
            System.out.println("pacote " + nSeq + " recebido");

            if (nSeqEsperado == nSeq) {

                // primeiro pacote
                if ((ultimoNSeq == -1) && (nSeqEsperado == 0)) {

                    Scanner input = new Scanner(System.in);
                    System.out.println("Insira onde deseja salvar o arquivo");
                    String caminhoArquivo = input.nextLine();

                    if (!new File(caminhoArquivo).exists()) {
                        new File(caminhoArquivo).mkdirs();
                    }
                    
                    System.out.println("Insira o nome do arquivo a ser criado ..");
                    String nomeArquivo = input.nextLine();
                    //System.out.println("Servidor -> Nome do arquivo : " + nomeArquivo);

                    File f = new File(caminhoArquivo + nomeArquivo);
                    if (!f.exists()) {
                        f.createNewFile();
                    }

                    //dados iniciais
                    fos = new FileOutputStream(f);
                    
                    fos.write(data, 4, p.getLength() - 4);

                    ultimoNSeq = nSeq;
                    nSeqEsperado += p.getLength();
                    enviaAck(nSeq + nSeqEsperado, IPAddress);
                } else {
                    fos.write(data);
                    ultimoNSeq = nSeq;
                    nSeqEsperado += p.getLength();
                    enviaAck(nSeq + nSeqEsperado, IPAddress);
                }
            }
            s.close();
        }
        
    }
}
