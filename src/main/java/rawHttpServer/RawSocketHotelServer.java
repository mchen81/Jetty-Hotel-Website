package rawHttpServer;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Implements an http server using raw sockets
 */
public class RawSocketHotelServer {

    public static final int PORT = 5556;
    public static final String EOT = "EOT";
    public static final String EXIT = "SHUTDOWN";

    private volatile boolean isShutdown = false;


    public static void main(String[] args) {
        new RawSocketHotelServer().startServer();
    }


    public void startServer() {
        final ExecutorService threads = Executors.newFixedThreadPool(4);

        Runnable serverTask = new Runnable() {
            @Override
            public void run() {
                try {
                    ServerSocket welcomingSocket = new ServerSocket(PORT);
                    System.out.println("Waiting for clients to connect...");
                    while (!isShutdown) {
                        Socket clientSocket = welcomingSocket.accept();
                        threads.submit(new RequestWorker(clientSocket));
                    }
                    if (isShutdown) {
                        welcomingSocket.close();
                    }
                } catch (IOException e) {
                    System.err.println("Unable to process client request");
                    e.printStackTrace();
                }
            }
        };
        Thread serverThread = new Thread(serverTask);
        serverThread.start();
    }


    private class RequestWorker implements Runnable {

        private final Socket clientSocket;

        RequestWorker(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                while (!clientSocket.isClosed()) {
                    String httpRequestString = reader.readLine();
                    HttpRequest httpRequest = new HttpRequest(httpRequestString);
                }
            } catch (IOException e) {
                System.out.println(e);
            } finally {
                try {
                    if (clientSocket != null)
                        clientSocket.close();
                } catch (IOException e) {
                    System.out.println("Can't close the socket : " + e);
                }
            }
        }
    }

}
