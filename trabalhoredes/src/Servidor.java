
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
    static final int TIMEOUTTIME = 200;
    

    public static void main(String args[]) throws UnknownHostException, SocketException, IOException {

        
        boolean ultimo = false;
        InetAddress IPAddress = InetAddress.getByName("127.0.0.1");
        int ultimoNSeq = -1;
        boolean ACKenviado = false;
        FileOutputStream fos = null;
        byte[] data = new byte[TAMANHOPACOTE];
        DatagramSocket s = new DatagramSocket(PORTASERVIDOR);
        DatagramSocket sk1 = new DatagramSocket();
        DatagramPacket p = new DatagramPacket(data, data.length, IPAddress, PORTASERVIDOR);

        while (true) {
            ACKenviado = false;
            s.receive(p);
            data = p.getData();
            int nSeq = ByteBuffer.wrap(data, 0, 4).getInt();
            

            //verifica se é o ULTIMO pacote
            if (p.getLength() < 1000) {
                System.out.println("ULTIMO pacote recebido");
                fos.write(data, 4, data.length - 4);
                //fos.write(data);
                ultimoNSeq = nSeq;

                data = null;
                byte[] ackBytes = ByteBuffer.allocate(4).putInt(nSeq).array();

                DatagramPacket ACK = new DatagramPacket(ackBytes, ackBytes.length, IPAddress, 8001);
                sk1.send(ACK);
                ACKenviado = true;
                System.out.println("ACK " + (nSeq) + " enviado");
                ackBytes = null;
                fos.flush();
                fos.close();
                s.close();
                System.out.println("\nTRANSFERENCIA COMPLETA\n");
                break;
            }else if (ultimoNSeq >= 0 && (ultimoNSeq + p.getLength() == nSeq) ) {

                System.out.println("pacote " + nSeq + " recebido");
                fos.write(data, 4, data.length - 4);
                //fos.write(data);
                ultimoNSeq = nSeq;

                data = null;
                byte[] ackBytes = ByteBuffer.allocate(4).putInt(nSeq).array();

                DatagramPacket ACK = new DatagramPacket(ackBytes, ackBytes.length, IPAddress, 8001);
                sk1.send(ACK);
                ACKenviado = true;
                System.out.println("ACK " + (nSeq) + " enviado");
                ackBytes = null;

                fos.flush();
                
            }
            // verifica se é o PRIMEIRO pacote
            else if ((ultimoNSeq == -1) && p.getLength() == TAMANHOPACOTE) {

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

                System.out.println("pacote " + nSeq + " recebido");

                fos = new FileOutputStream(f);

                fos.write(data, 4, data.length - 4);
                data = null;
                ultimoNSeq = nSeq;

                //Envia ACK
                byte[] ackBytes = ByteBuffer.allocate(4).putInt(nSeq).array();
                DatagramPacket ACK = new DatagramPacket(ackBytes, ackBytes.length, IPAddress, 8001);
                sk1.send(ACK);
                ACKenviado = true;
                System.out.println("ACK " + (nSeq) + " enviado");
                ackBytes = null;
            } 
        }

    }
}
