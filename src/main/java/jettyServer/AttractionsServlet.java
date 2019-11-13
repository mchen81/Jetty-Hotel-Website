package jettyServer;

import com.google.gson.stream.JsonWriter;
import hotelapp.HotelDataDriver;
import hotelapp.ThreadSafeHotelData;
import hotelapp.TouristAttractionFinder;
import hotelapp.bean.Review;

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
        int radius = Integer.parseInt(request.getParameter("radius"));

        TouristAttractionFinder touristAttractionFinder = new TouristAttractionFinder(HotelDataDriver.hotelData);
        out.print(touristAttractionFinder.fetchAttractions(hotelId, radius));
        out.flush();

    }
}
