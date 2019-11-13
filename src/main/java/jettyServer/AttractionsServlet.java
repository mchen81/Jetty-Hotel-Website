package jettyServer;

import com.google.gson.stream.JsonWriter;
import hotelapp.HotelDataDriver;
import hotelapp.ThreadSafeHotelData;
import hotelapp.TouristAttractionFinder;
import hotelapp.bean.Review;
import org.apache.commons.text.StringEscapeUtils;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.List;

public class AttractionsServlet extends HttpServlet {

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_OK);
        PrintWriter out = response.getWriter();

        String hotelId = request.getParameter("hotelId");
        hotelId = hotelId == null ? "-1" : StringEscapeUtils.escapeHtml4(hotelId);

        String radius = request.getParameter("radius");
        radius = radius == null ? "0" : StringEscapeUtils.escapeHtml4(radius);
        try {
            Integer.parseInt(radius);
        } catch (NumberFormatException e) {
            radius = "0";
        }
        TouristAttractionFinder touristAttractionFinder = new TouristAttractionFinder(HotelDataDriver.hotelData);
        out.print(touristAttractionFinder.fetchAttractions(hotelId, Integer.parseInt(radius)));
        out.flush();

    }


}
