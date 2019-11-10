package rawHttpServer;

import hotelapp.HotelData;
import hotelapp.HotelDataDriver;
import hotelapp.ThreadSafeHotelData;
import rawHttpServer.handlers.AttractionsHandler;

import java.io.*;
import java.lang.reflect.Constructor;
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

    private Map<String, String> handlers; // maps each url path to the appropriate handler

    private volatile boolean isShutdown = false;

    public static ThreadSafeHotelData hotelData;


    public RawSocketHotelServer() {

        handlers = new HashMap<>();
        handlers.put("attractions", "AttractionsHandler");
        handlers.put("hotelInfo", "HotelHandler");
        handlers.put("reviews", "ReviewsHandler");

    }

    public static void main(String[] args) {
        // prepare hotel data
        HotelDataDriver hotelDataDriver = new HotelDataDriver();
        hotelData = hotelDataDriver.getHotelData();
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

                PrintWriter outPutPrintWriter = new PrintWriter(clientSocket.getOutputStream());

                while (!clientSocket.isClosed()) {
                    String httpRequestString = reader.readLine();
                    HttpRequest httpRequest = new HttpRequest(httpRequestString);

                    if (!"GET".equals(httpRequest.getHttpCRUD())) {
                        // TODO return 405 Method Not Allowed
                    }

                    // deal with http query path

                    // call handler
                    if (!handlers.containsKey(httpRequest.getAction())) {
                        // TODO return 405 method not allowed
                    }
                    try {
                        Class c = Class.forName(handlers.get(httpRequest.getAction()));
                        HttpHandler httpHandler = (HttpHandler) c.newInstance();
                        httpHandler.processRequest(httpRequest, outPutPrintWriter);

                    } catch (ClassNotFoundException e) {
                        // MUST FIND CLASS
                    } catch (InstantiationException e) {
                        //
                    } catch (IllegalAccessException e) {
                        //
                    }


                    // TODO return client socket(out put printer)


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
