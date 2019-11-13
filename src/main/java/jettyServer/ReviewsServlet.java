package jettyServer;

import com.google.gson.stream.JsonWriter;
import hotelapp.HotelDataDriver;
import hotelapp.ThreadSafeHotelData;
import hotelapp.bean.Hotel;
import hotelapp.bean.Review;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.List;

public class ReviewsServlet extends HttpServlet {

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        ThreadSafeHotelData hotelData = HotelDataDriver.hotelData;
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_OK);
        PrintWriter out = response.getWriter();
        String hotelID = request.getParameter("hotelId");
        int numbersOfReview = Integer.parseInt(request.getParameter("num"));
        List<Review> reviews = hotelData.getReviews(hotelID);
        JsonWriter jsonWriter = new JsonWriter(out);

        try {
            jsonWriter.beginObject();
            if (reviews == null) {
                jsonWriter.name("success").value(false);
                jsonWriter.name("hotelId").value("Invalid");
            } else {
                jsonWriter.name("success").value(true);
                jsonWriter.name("hotelId").value(hotelID);
                jsonWriter.name("reviews").beginArray();
                for (int i = 0; i < numbersOfReview; i++) {
                    Review review = reviews.get(i);
                    Date reviewDate = review.getSubmissionTime();

                    jsonWriter.beginObject();

                    jsonWriter.name("reviewId").value(review.getReviewId());
                    jsonWriter.name("title").value(review.getTitle());
                    jsonWriter.name("user").value(review.getUserNickname());
                    jsonWriter.name("reviewText").value(review.getReviewText());
                    jsonWriter.name("date").value(reviewDate.toString());
                    jsonWriter.endObject();

                }
                jsonWriter.endArray();
            }
            jsonWriter.endObject();
            out.flush();

        } catch (IOException e) {
            throw new IllegalArgumentException("Cannot process Review Info");
        }


    }
}
