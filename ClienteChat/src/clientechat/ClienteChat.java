package clientechat;

import java.io.*;
import java.net.*;

public class ClienteChat {
    public static void main(String[] args) {
        try {
            BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));

            // Establecer la conexión con el servidor
            System.out.print("Ingrese la dirección IP del servidor: ");
            String serverIP = userInput.readLine();

            System.out.print("Ingrese el puerto del servidor: ");
            int serverPort = Integer.parseInt(userInput.readLine());

            Socket socket = new Socket(serverIP, serverPort);
            System.out.println("Conectado al servidor en " + serverIP + ":" + serverPort);

            // Hilo para leer mensajes del servidor
            Thread serverListener = new Thread(() -> {
                try {
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    String message;
                    while ((message = in.readLine()) != null) {
                        System.out.println(message);
                    }
                    System.out.println("El servidor se ha desconectado.");
                } catch (IOException e) {
                    System.err.println("Error al leer del servidor: " + e.getMessage());
                }
            });
            serverListener.start();

            // Envío del nickname al servidor
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            System.out.print("Ingrese su nickname: ");
            String nickname = userInput.readLine();
            out.println(nickname);

            // Hilo para enviar mensajes al servidor
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            String userInputMessage;
            while ((userInputMessage = userInput.readLine()) != null) {
                writer.write(userInputMessage);
                writer.newLine();
                writer.flush();
            }

            // Cierre de recursos
            userInput.close();
            socket.close();
        } catch (IOException e) {
            System.err.println("Error en el cliente: " + e.getMessage());
        }
    }
}
