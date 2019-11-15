package rawHttpServer.handlers;

import com.google.gson.stream.JsonWriter;
import hotelapp.HotelDataDriver;
import hotelapp.TouristAttractionFinder;
import rawHttpServer.HttpHandler;
import rawHttpServer.HttpRequest;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * Handler for /attractions
 */
public class AttractionsHandler implements HttpHandler {
    @Override
    public void processRequest(HttpRequest request, PrintWriter writer) {

        TouristAttractionFinder touristAttractionFinder = new TouristAttractionFinder(HotelDataDriver.hotelData);
        String hotelId = request.getValue("hotelId");
        int radius = 0;
        JsonWriter jsonWriter = new JsonWriter(writer);
        try {
            radius = Integer.parseInt(request.getValue("radius"));
        } catch (NumberFormatException e) {
            radius = 0;
        }

        try {
            if (hotelId == null || !HotelDataDriver.hotelData.hasHotel(hotelId)) {
                jsonWriter.beginObject();
                jsonWriter.name("success").value(false);
                jsonWriter.name("hotelId").value("invalid");
                jsonWriter.endObject();
            } else if (radius <= 0) {
                jsonWriter.beginObject();
                jsonWriter.name("success").value(false);
                jsonWriter.name("radius").value("invalid");
                jsonWriter.endObject();
            } else {
                writer.print(touristAttractionFinder.fetchAttractions(hotelId, radius));
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("Cannot process Hotel Info");
        }


    }
}
