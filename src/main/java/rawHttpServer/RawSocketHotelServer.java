package rawHttpServer;

import exceptions.HttpRequestParingException;
import hotelapp.ThreadSafeHotelData;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Implements an http server using raw sockets
 */
public class RawSocketHotelServer {

    public static final int PORT = 5556;
    public static final String EOT = "EOT";
    public static final String EXIT = "SHUTDOWN";

    private Map<String, String> handlers; // maps each url path to the appropriate handler

    private volatile boolean isShutdown = false;

    public RawSocketHotelServer() {

        handlers = new HashMap<>();
        handlers.put("attractions", "rawHttpServer.handlers.AttractionsHandler");
        handlers.put("hotelInfo", "rawHttpServer.handlers.HotelHandler");
        handlers.put("reviews", "rawHttpServer.handlers.ReviewsHandler");

    }

    public void startServer(ThreadSafeHotelData hotelData) {
        final ExecutorService threads = Executors.newFixedThreadPool(4);
        Runnable serverTask = new Runnable() {
            @Override
            public void run() {
                try {
                    ServerSocket welcomingSocket = new ServerSocket(PORT);
                    System.out.println("Waiting for clients to connect...");
                    while (!isShutdown) {
                        Socket clientSocket = welcomingSocket.accept();
                        System.out.println("Connecting Successful");
                        threads.submit(new RequestWorker(clientSocket));
                    }
                    if (isShutdown) {
                        welcomingSocket.close();
                    }
                } catch (IOException e) {
                    System.err.println("Unable to process client request");
                    e.printStackTrace();
                } catch (Exception e) {
                    System.out.println(e);
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
                PrintWriter outPutPrintWriter = new PrintWriter(clientSocket.getOutputStream());
                while (!clientSocket.isClosed()) {
                    String httpRequestString = reader.readLine();
                    HttpRequest httpRequest = null;
                    try {
                        httpRequest = new HttpRequest(httpRequestString);
                    } catch (HttpRequestParingException e) {
                        outPutPrintWriter.println("HTTP/1.1 400 Bad Request");
                        System.out.println("HTTP/1.1 400 Bad Request: " + e);
                        outPutPrintWriter.flush();
                        return;
                    }

                    if (!"GET".equals(httpRequest.getHttpCRUD())) {
                        // return 405 Method Not Allowed
                        outPutPrintWriter.println("HTTP/1.1 405 Method Not Allowed");
                        System.out.println("405 Method Not Allowed: " + httpRequest.getAction());
                        outPutPrintWriter.flush();
                        return;
                    }

                    if (!handlers.containsKey(httpRequest.getAction().replace("/", ""))) {
                        outPutPrintWriter.println("HTTP/1.1 404 Page Not Found");
                        System.out.println("404 Page Not Found");
                        System.out.println(httpRequest.getAction());
                        outPutPrintWriter.flush();
                        return;
                    }
                    try {
                        System.out.println("HI");
                        Class c = Class.forName(handlers.get(httpRequest.getAction()));
                        HttpHandler httpHandler = (HttpHandler) c.newInstance();
                        outPutPrintWriter.print("HTTP/1.1 200 \r\n"); // Version & status code
                        outPutPrintWriter.print("Content-Type: application/json\r\n"); // The type of data
                        outPutPrintWriter.print("Connection: close\r\n"); // Will close stream
                        outPutPrintWriter.print("\r\n"); // End of headers
                        httpHandler.processRequest(httpRequest, outPutPrintWriter);
                        outPutPrintWriter.println();
                        outPutPrintWriter.flush();
                        System.out.println("Success");
                    } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
                        System.out.println(e);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
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
