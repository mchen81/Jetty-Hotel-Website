package rawHttpServer.handlers;

import com.google.gson.stream.JsonWriter;
import hotelapp.HotelDataDriver;
import hotelapp.ThreadSafeHotelData;
import hotelapp.bean.Review;
import rawHttpServer.HttpHandler;
import rawHttpServer.HttpRequest;
import rawHttpServer.RawSocketHotelServer;

import java.io.IOException;
import java.io.PrintWriter;

import java.util.Date;
import java.util.List;

public class ReviewsHandler implements HttpHandler {
    @Override
    public void processRequest(HttpRequest request, PrintWriter writer) {
        ThreadSafeHotelData hotelData = HotelDataDriver.hotelData;

        String hotelId = request.getValue("hotelId");
        int numbersOfReview = Integer.parseInt(request.getValue("num"));
        List<Review> reviews = hotelData.getReviews(hotelId);

        JsonWriter jsonWriter = new JsonWriter(writer);


        try {
            jsonWriter.beginObject();
            if (reviews == null) {
                jsonWriter.name("success").value(false);
                jsonWriter.name("hotelId").value("Invalid");
            } else {
                jsonWriter.name("success").value(true);
                jsonWriter.name("hotelId").value(hotelId);
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
            writer.println();

        } catch (IOException e) {
            throw new IllegalArgumentException("Cannot process Review Info");
        }


    }
}
