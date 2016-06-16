
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
    
    public static void main(String args[]) throws Exception {
        
        boolean transferenciaCompleta = false;
        
        Cliente cliente = new Cliente();
        
        Scanner input = new Scanner (System.in);
        String caminhoArquivo = input.nextLine();
        
        // Resolução de nome de host.
        InetAddress IPAddress = InetAddress.getByName("hostname");
        
        System.out.println("Insira o caminho do arquivo que deseja enviar");
        String caminho = input.nextLine();
        
        cliente.preparaArquivo(caminhoArquivo);
        
        while(!transferenciaCompleta){
            
        }
        
        
    }
    
}
