package hotelapp;

import jettyServer.JettyHotelServer;
import rawHttpServer.RawSocketHotelServer;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class HotelDataDriver {

    public static ThreadSafeHotelData hotelData;

    public static void main(String[] args) throws Exception {
        // prepare hotel data
        Map<String, String> commands = parseArgs(args);
        if (!commands.containsKey("-hotels") || !commands.containsKey("-reviews") || !commands.containsKey("-config")) {
            System.out.println("Wrong Arguments!");
            System.out.println("Correct Arguments should include: -hotels, -reviews, -config");
            System.out.println("e.g. -hotels input/hotels.json -reviews input/reviews -config input/config.json");
            throw new IllegalArgumentException();
        }
        HotelDataDriver.prepareHotelData(commands);
        TouristAttractionFinder.parseConfigFile(commands.get("-config"));

        System.out.println("====================WELCOME=====================");
        System.out.println("Files Loaded Successfully");
        System.out.println("JettyHotelServer's Port is on " + JettyHotelServer.PORT);
        System.out.println("RawSocketHotelServer's Port is on" + RawSocketHotelServer.PORT);
        System.out.println("Provide 3 query request: ");
        System.out.println("EXAMPLE: ");
        System.out.println("/hotelInfo?hotelId=12345");
        System.out.println("/reviews?hotelId=12345&num=2");
        System.out.println("/attractions?hotelId=12345&radius=2");
        System.out.println("=====================ENJOY======================");

        new RawSocketHotelServer().startServer(hotelData);
        new JettyHotelServer();

    }

    private static void prepareHotelData(Map<String, String> commands) {
        hotelData = new ThreadSafeHotelData();
        HotelDataBuilder hotelDataBuilder = new HotelDataBuilder(hotelData);
        try {
            hotelDataBuilder.loadHotelInfo(commands.get("-hotels"));
            hotelDataBuilder.loadReviews(Paths.get(commands.get("-reviews")));
        } catch (IOException e) {
            throw new IllegalArgumentException("Cannot read file: " + e);
        }
    }

    private static Map<String, String> parseArgs(String[] args) {
        Map<String, String> result = new HashMap<>();
        if (args.length % 2 != 0) {
            throw new IllegalArgumentException("Wrong Arguments, please see Readme");
        }
        for (int i = 0; i < args.length; i += 2) {
            if (!args[i].startsWith("-")) {
                throw new IllegalArgumentException("Wrong Arguments, please see Readme");
            }
            result.put(args[i], args[i + 1]);
        }
        return result;
    }

    public ThreadSafeHotelData getHotelData() {
        return new ThreadSafeHotelData();
    }


}
