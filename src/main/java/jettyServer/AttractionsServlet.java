package jettyServer;

import com.google.gson.stream.JsonWriter;
import hotelapp.HotelDataDriver;
import hotelapp.ThreadSafeHotelData;
import hotelapp.TouristAttractionFinder;
import org.apache.commons.text.StringEscapeUtils;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Servlet for /attractions
 */
public class AttractionsServlet extends HttpServlet {

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        // set basic response
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_OK);

        PrintWriter out = response.getWriter();
        ThreadSafeHotelData hotelData = HotelDataDriver.hotelData;


        JsonWriter jsonWriter = new JsonWriter(out);
        String hotelId = request.getParameter("hotelId");
        String radius = request.getParameter("radius");

        // if hotel id is null, return invalid hotel id json
        if (hotelId == null) {
            invalidHotelId(jsonWriter);
            out.flush();
            return;
        }

        // if radius is null, return invalid radius json
        if (radius == null) {
            invalidRadius(jsonWriter);
            out.flush();
            return;
        }

        hotelId = StringEscapeUtils.escapeHtml4(hotelId);
        radius = StringEscapeUtils.escapeHtml4(radius);

        int radiusInt;

        // if radius is not a number, return invalid radius json
        try {
            radiusInt = Integer.parseInt(radius);
        } catch (NumberFormatException e) {
            invalidRadius(jsonWriter);
            out.flush();
            return;
        }

        // if there is not hotel id in hotel data, return invalid hotel json
        if (!hotelData.hasHotel(hotelId)) {
            invalidHotelId(jsonWriter);
            out.flush();
            return;
        }

        // if radius is negative, return invalid radius json
        if (radiusInt <= 0) {
            invalidRadius(jsonWriter);
            out.flush();
            return;
        }

        TouristAttractionFinder touristAttractionFinder = new TouristAttractionFinder(hotelData);
        out.print(touristAttractionFinder.fetchAttractions(hotelId, radiusInt));
        out.flush();

    }


    /**
     * write invalid hotel json
     *
     * @param jsonWriter http response writer
     * @throws IOException
     */
    private void invalidHotelId(JsonWriter jsonWriter) throws IOException {
        jsonWriter.beginObject();
        jsonWriter.name("success").value(false);
        jsonWriter.name("hotelId").value("invalid");
        jsonWriter.endObject();
    }

    /**
     * write invalid radius json
     *
     * @param jsonWriter http response writer
     * @throws IOException
     */
    private void invalidRadius(JsonWriter jsonWriter) throws IOException {
        jsonWriter.beginObject();
        jsonWriter.name("success").value(false);
        jsonWriter.name("radius").value("invalid");
        jsonWriter.endObject();
    }


}
