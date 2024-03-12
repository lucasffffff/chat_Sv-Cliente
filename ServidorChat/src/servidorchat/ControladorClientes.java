package servidorchat;

import java.io.*;
import java.net.*;
import java.util.*;

public class ControladorClientes implements Runnable {
    // Definir variables
    private final Socket clientSocket;
    private BufferedReader in;
    private PrintWriter out;
    private String nickname;

    public ControladorClientes(Socket socket) {
        // Inicializar el socket del cliente
        this.clientSocket = socket;
        try {
            // Inicializar los flujos de entrada y salida
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream(), true);
        } catch (IOException e) {
            System.err.println("Error al inicializar los flujos de entrada/salida: " + e.getMessage());
        }
    }

    @Override
    public void run() {
        try {
            // Solicitar el nickname al cliente
            out.println("Introduce tu nickname:");
            nickname = in.readLine();
            // Comunicar la conexión exitosa al cliente
            out.println("Conexión establecida. Bienvenido, " + nickname + "!");

            String inputLine;
            // Leer los mensajes del cliente y reenviarlos a todos los clientes conectados
            while ((inputLine = in.readLine()) != null) {
                // Mostrar el mensaje en la consola del servidor
                System.out.println(nickname + ": " + inputLine);
                // Reenviar el mensaje a todos los clientes
                ServidorChat.broadcastMessage(nickname + ": " + inputLine);
            }
        } catch (IOException e) {
            System.err.println("Error al comunicarse con el cliente: " + e.getMessage());
        } finally {
            // Cuando el cliente se desconecta, eliminarlo de la lista de clientes conectados y cerrar la conexión
            ServidorChat.removeClient(this);
            closeConnection();
        }
    }

    // Método para enviar un mensaje al cliente
    public void sendMessage(String message) {
        out.println(message);
    }

    // Método para cerrar la conexión con el cliente
    public void closeConnection() {
        try {
            in.close();
            out.close();
            clientSocket.close();
        } catch (IOException e) {
            System.err.println("Error al cerrar la conexión con el cliente: " + e.getMessage());
        }
    }
}
