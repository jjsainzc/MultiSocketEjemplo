package multisocketexample;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Servidor multiconexion usando hilos y control de conexiones
 * @author alienware
 */
public class ServerMultiCliente {

    // Contador de conexiones.
    public static int cantidad = 1;
    // Maxima cantidad de conexiones a atender
    private static final int MAX = 2;
    private static final int PUERTO = 4445;
    private static ServerMultiCliente serverXClient;

    public static ServerMultiCliente getInstance() {
       if (serverXClient == null) serverXClient = new ServerMultiCliente( PUERTO );
       return serverXClient;
   }
    
    public static ServerMultiCliente getInstance(Integer puerto) {
       if (serverXClient == null) serverXClient = new ServerMultiCliente( (puerto == null) ? PUERTO : puerto );
       return serverXClient;
   }
    
    private ServerMultiCliente(int puerto) {
        @SuppressWarnings("UnusedAssignment")
        Socket s = null;
        ServerSocket ss2 = null;
        System.out.println("Servidor en escucha......");
        try {
            ss2 = new ServerSocket(puerto); // puerto
        } catch (IOException e) {
            System.out.println("No puedo crear el socket");
        }

        while (true) {
            try {
                if (cantidad <= MAX) {
                    s = ss2.accept();
                    System.out.println("Conexion establecida " + s.getInetAddress());
                    ServerThread st = new ServerThread(s);
                    st.start();
                    cantidad++;
                }
            } catch (NullPointerException|IOException e) {
                System.out.println("Error en la conexion ");
                System.exit(-1);
            }
        }
    }

    @SuppressWarnings("null")
    public static void main(String args[]) {
        ServerMultiCliente.getInstance();
    }
}

/*
 * Clase hilo para atender a los clientes
*/
class ServerThread extends Thread {
    String line = null;
    BufferedReader is = null;
    PrintWriter os = null;
    Socket s = null;

    public ServerThread(Socket s) {
        this.s = s;
    }

    @Override
    public void run() {
        try {
            is = new BufferedReader(new InputStreamReader(s.getInputStream()));
            os = new PrintWriter(s.getOutputStream());
        } catch (NullPointerException|IOException e) {
            System.out.println("Error de entrada/salida en hilo");
            return;
        }

        try {
            line = is.readLine();
            while (line.compareTo("QUIT") != 0) {
                os.println(line);
                os.flush();
                System.out.println("Respuesta al cliente:  " + line);
                line = is.readLine();
            }
        } catch (IOException e) {
            line = this.getName(); //reusar la linea para el nombre del hilo
            System.out.println("IO Error/ Cliente " + line + " ha terminado abruptamente");
        } catch (NullPointerException e) {
            line = this.getName();
            System.out.println("Client " + s.getInetAddress() + " cerrado");
        } finally {
            try {
                System.out.println("Conexion cerrandose..");
                if (is != null) {
                    is.close();
                    System.out.println("canal de entrada cerrado");
                }
                if (os != null) {
                    os.close();
                    System.out.println("Canal de salida cerrado");
                }
                if (s != null) {
                    s.close();
                    System.out.println("Socket Cerrado");
                    ServerMultiCliente.cantidad--;
                }
            } catch (IOException ie) {
                System.out.println("Error cerrado del socket");
            }
        }
    }
}
