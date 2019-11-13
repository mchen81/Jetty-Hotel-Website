package hotelapp;

import jettyServer.JettyHotelServer;
import rawHttpServer.RawSocketHotelServer;

import java.nio.file.Paths;

public class HotelDataDriver {

    public static ThreadSafeHotelData hotelData;

    public static void main(String[] args) throws Exception {
        // prepare hotel data
        HotelDataDriver.prepareHotelData();
        new RawSocketHotelServer().startServer(hotelData);
        new JettyHotelServer();
    }

    private static void prepareHotelData() {
        hotelData = new ThreadSafeHotelData();
        HotelDataBuilder hotelDataBuilder = new HotelDataBuilder(hotelData);
        hotelDataBuilder.loadHotelInfo("input/hotels.json");
        hotelDataBuilder.loadReviews(Paths.get("input/reviews"));
    }

}
