/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package multisocketexample;

/**
 *
 * @author alienware
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

public class Client {

    @SuppressWarnings("null")
    public static void main(String args[]) throws IOException {

        InetAddress direccionLocal = InetAddress.getLocalHost();
        Socket socket = null;
        String line;
        BufferedReader teclado = null;
        BufferedReader entrada = null;
        PrintWriter salida = null;

        try {
            socket = new Socket(direccionLocal, 4445); 
            teclado = new BufferedReader(new InputStreamReader(System.in));
            entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            salida= new PrintWriter(socket.getOutputStream());
        } catch (IOException e) {
            System.err.print("Error de entrada/salida");
        }

        System.out.println("Cliente  : " + direccionLocal);
        System.out.println("mensaje a enviar (  QUIT para terminar):");

        String respuesta = null;
        try {
            line = teclado.readLine();
            while (line.compareTo("QUIT") != 0) {
                salida.println(line);
                salida.flush();
                respuesta = entrada.readLine();
                System.out.println("Respuesta del server : " + respuesta);
                line = teclado.readLine();
            }

        } catch (IOException e) {
            System.out.println("Error canal de lectura");
        } finally {
            entrada.close();
            salida.close();
            teclado.close();
            socket.close();
            System.out.println("Conexion cerrada");
        }
    }
}
