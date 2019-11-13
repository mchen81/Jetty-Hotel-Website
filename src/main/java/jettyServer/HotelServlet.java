package jettyServer;

import com.google.gson.stream.JsonWriter;
import hotelapp.HotelDataDriver;
import hotelapp.ThreadSafeHotelData;
import hotelapp.bean.Hotel;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class HotelServlet extends HttpServlet {

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        ThreadSafeHotelData hotelData = HotelDataDriver.hotelData;
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_OK);
        PrintWriter out = response.getWriter();
        String hotelID = request.getParameter("hotelId");
        Hotel hotel = hotelData.getHotelInstance(hotelID);

        JsonWriter jsonWriter = new JsonWriter(out);
        try {
            jsonWriter.beginObject();
            if (hotel == null) {
                jsonWriter.name("success").value(false);
                jsonWriter.name("hotelId").value("Invalid");
            } else {
                jsonWriter.name("success").value(true);
                jsonWriter.name("hotelId").value(hotel.getHotelId());
                jsonWriter.name("name").value(hotel.getName());
                jsonWriter.name("addr").value(hotel.getStreetAddress());
                jsonWriter.name("city").value(hotel.getCity());
                jsonWriter.name("state").value(hotel.getState());
                jsonWriter.name("lat").value(hotel.getLatitude().toString());
                jsonWriter.name("lng").value(hotel.getLongitude().toString());
            }
            jsonWriter.endObject();
            out.flush();
        } catch (IOException e) {
            throw new IllegalArgumentException("Cannot process Hotel Info");
        }


    }


}
