package rawHttpServer.handlers;

import com.google.gson.stream.JsonWriter;
import hotelapp.HotelDataDriver;
import hotelapp.ThreadSafeHotelData;
import hotelapp.bean.Review;
import rawHttpServer.HttpHandler;
import rawHttpServer.HttpRequest;

import java.io.IOException;
import java.io.PrintWriter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Handler for /reviews
 */
public class ReviewsHandler implements HttpHandler {
    @Override
    public void processRequest(HttpRequest request, PrintWriter writer) {
        ThreadSafeHotelData hotelData = HotelDataDriver.hotelData;

        String hotelId = request.getValue("hotelId");
        hotelId = hotelId == null ? "-1" : hotelId;
        int numbersOfReview = 0;
        try {
            numbersOfReview = Integer.parseInt(request.getValue("num"));
        } catch (NumberFormatException e) {
            numbersOfReview = 0;
        }
        List<Review> reviews = hotelData.getReviews(hotelId);

        JsonWriter jsonWriter = new JsonWriter(writer);

        try {
            jsonWriter.beginObject();
            if (reviews == null || numbersOfReview == 0 || hotelId.equals("-1")) {
                jsonWriter.name("success").value(false);
                jsonWriter.name("hotelId").value("invalid");
            } else {
                jsonWriter.name("success").value(true);
                jsonWriter.name("hotelId").value(hotelId);
                jsonWriter.name("reviews").beginArray();
                for (int i = 0; i < numbersOfReview; i++) {
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
            writer.println();

        } catch (IOException e) {
            throw new IllegalArgumentException("Cannot process Review Info");
        }


    }
}
