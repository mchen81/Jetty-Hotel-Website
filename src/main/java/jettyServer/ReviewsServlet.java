package jettyServer;

import com.google.gson.stream.JsonWriter;
import hotelapp.HotelDataDriver;
import hotelapp.ThreadSafeHotelData;
import hotelapp.bean.Review;
import org.apache.commons.text.StringEscapeUtils;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Servlet for reviews
 */
public class ReviewsServlet extends HttpServlet {

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        ThreadSafeHotelData hotelData = HotelDataDriver.hotelData;
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_OK);
        PrintWriter out = response.getWriter();
        String hotelID = request.getParameter("hotelId");
        hotelID = hotelID == null ? "-1" : StringEscapeUtils.escapeHtml4(hotelID);

        int numbersOfReview;
        try { // if the parameter of num cannot be transformed to int, set it -1
            numbersOfReview = Integer.parseInt(request.getParameter("num"));
        } catch (Exception e) {
            numbersOfReview = -1;
        }

        List<Review> reviews = hotelData.getReviews(hotelID);
        JsonWriter jsonWriter = new JsonWriter(out);

        try {
            jsonWriter.beginObject();
            if (reviews == null || numbersOfReview < 0) {
                jsonWriter.name("success").value(false);
                jsonWriter.name("hotelId").value("invalid");
            } else {
                jsonWriter.name("success").value(true);
                jsonWriter.name("hotelId").value(hotelID);
                jsonWriter.name("reviews").beginArray();
                for (int i = 0; i < numbersOfReview && i < reviews.size(); i++) {
                    Review review = reviews.get(i);
                    LocalDateTime reviewDate = review.getSubmissionTime();
                    String month = String.valueOf(reviewDate.getMonth().getValue());
                    month = month.length() == 1 ? "0" + month : month; // ensure output 2 digits
                    String day = String.valueOf(reviewDate.getDayOfMonth());
                    day = day.length() == 1 ? "0" + day : day; // ensure output 2 digits
                    String year = String.valueOf(reviewDate.getYear()).substring(2, 4);

                    jsonWriter.beginObject();
                    jsonWriter.name("reviewId").value(review.getReviewId());
                    jsonWriter.name("title").value(review.getTitle());
                    jsonWriter.name("user").value(review.getUserNickname());
                    jsonWriter.name("reviewText").value(review.getReviewText());
                    jsonWriter.name("date").value(String.format("%s:%s:%s", month, day, year));
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
