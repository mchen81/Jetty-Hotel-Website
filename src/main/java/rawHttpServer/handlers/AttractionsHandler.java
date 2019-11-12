package rawHttpServer.handlers;

import hotelapp.TouristAttractionFinder;
import rawHttpServer.HttpHandler;
import rawHttpServer.HttpRequest;
import rawHttpServer.RawSocketHotelServer;

import java.io.PrintWriter;

public class AttractionsHandler implements HttpHandler {
    @Override
    public void processRequest(HttpRequest request, PrintWriter writer) {

        TouristAttractionFinder touristAttractionFinder = new TouristAttractionFinder(RawSocketHotelServer.hotelData);
        String hotelId = request.getValue("hotelId");
        int radius = Integer.parseInt(request.getValue("radius"));
        System.out.println(touristAttractionFinder.fetchAttractions(hotelId, radius));
        writer.print(touristAttractionFinder.fetchAttractions(hotelId, radius));

    }
}
