package servidorchat;

import java.io.*;
import java.net.*;
import java.util.*;

public class ServidorChat {

    // Definir variables y constantes
    private static final int MAX_CLIENTS = 10;
    private static int connectedClients = 0;
    private static final List<ControladorClientes> clients = new ArrayList<>();
    private static ServerSocket serverSocket;

    public static void main(String[] args) {
        try {
            // Solicitar el puerto por el que se establecerá la conexión
            Scanner scanner = new Scanner(System.in);
            System.out.print("Introduce el puerto para establecer la conexión: ");
            int port = scanner.nextInt();
            scanner.close();

            // Crear un servidor en el puerto introducido
            serverSocket = new ServerSocket(port);
            System.out.println("Servidor iniciado en el puerto " + port);

            while (true) {
                // Esperar a que lleguen clientes y aceptar sus conexiones
                Socket clientSocket = serverSocket.accept();
                if (connectedClients < MAX_CLIENTS) {
                    connectedClients++;
                    // Si el número de clientes no supera el máximo, aceptar la conexión
                    System.out.println("Nuevo cliente conectado. Actualmente hay " + connectedClients + " usuarios conectados.");
                    // Crear un hilo para manejar al nuevo cliente
                    ControladorClientes controladorClientes = new ControladorClientes(clientSocket);
                    clients.add(controladorClientes);
                    new Thread(controladorClientes).start();
                } else {
                    // Si ya se alcanzó el máximo de clientes, rechazar nuevas conexiones
                    System.out.println("Demasiados usuarios conectados. Rechazando la conexión de un nuevo cliente.");
                    clientSocket.close();
                }
            }
        } catch (IOException e) {
            // Manejar los errores de entrada/salida
            System.err.println("Error en el servidor: " + e.getMessage());
        }
    }

    // Método para enviar un mensaje a todos los clientes conectados
    public static synchronized void broadcastMessage(String message) {
        for (ControladorClientes client : clients) {
            client.sendMessage(message);
        }
    }

    // Método para eliminar un cliente desconectado de la lista de conectados
    public static synchronized void removeClient(ControladorClientes client) {
        clients.remove(client);
        connectedClients--;
        if (connectedClients == 0) {
            // Si no hay ningún cliente conectado, mostrar un mensaje
            System.out.println("Ningún cliente conectado.");
        }
    }

    // Método para cerrar adecuadamente las conexiones de los clientes al cerrar el servidor
    public static void closeServer() {
        try {
            // Cerrar el socket del servidor
            serverSocket.close();
            // Enviar un mensaje de desconexión a todos los clientes
            broadcastMessage("El servidor se desconectó.");
            // Cerrar las conexiones de todos los clientes
            for (ControladorClientes client : clients) {
                client.closeConnection();
            }
        } catch (IOException e) {
            System.err.println("Error al cerrar el servidor: " + e.getMessage());
        }
    }
}
