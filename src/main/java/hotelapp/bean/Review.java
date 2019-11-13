package hotelapp.bean;

import hotelapp.exceptions.InvalidRatingException;
import hotelapp.exceptions.ParseException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class Review implements Comparable<Review> {

    private String hotelId;

    private String reviewId;

    private int ratingOverall;

    private String title;

    private String reviewText;

    private String userNickname = "Anonymous";

    private LocalDateTime submissionTime;

    private boolean isRecommended;

    public String getHotelId() {
        return hotelId;
    }

    public void setHotelId(String hotelId) {
        this.hotelId = hotelId;
    }

    public String getReviewId() {
        return reviewId;
    }

    public void setReviewId(String reviewId) {
        this.reviewId = reviewId;
    }

    public int getRatingOverall() {
        return ratingOverall;
    }

    public void setRatingOverall(int ratingOverall) throws InvalidRatingException {
        if (ratingOverall < 0 || ratingOverall > 5) {
            throw new InvalidRatingException();
        }
        this.ratingOverall = ratingOverall;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getReviewText() {
        return reviewText;
    }

    public void setReviewText(String reviewText) {
        this.reviewText = reviewText;
    }

    public String getUserNickname() {
        return userNickname;
    }

    public void setUserNickname(String userNickname) {
        if (userNickname.isEmpty()) {
            this.userNickname = "Anonymous";
        } else {
            this.userNickname = userNickname;
        }
    }

    public LocalDateTime getSubmissionTime() {
        return submissionTime;
    }

    public void setSubmissionTime(String submissionTime) throws ParseException {
        try {
            this.submissionTime = LocalDateTime.parse(submissionTime, DateTimeFormatter.ISO_DATE_TIME);
        } catch (Exception e) {
            System.out.println(submissionTime);
            throw new ParseException();
        }
    }

    public void setSubmissionTime(LocalDateTime submissionTime) {
        this.submissionTime = submissionTime;
    }

    public boolean isRecommended() {
        return isRecommended;
    }

    public void setRecommended(boolean recommended) {
        isRecommended = recommended;
    }

    @Override
    public int compareTo(Review anotherReview) {
        int timeComparison = this.submissionTime.compareTo(anotherReview.getSubmissionTime());
        if (timeComparison == 0) {
            int nameComparison = userNickname.compareTo(anotherReview.getUserNickname());
            if (nameComparison == 0) {
                return reviewId.compareTo(anotherReview.getReviewId());
            }
            return nameComparison;
        }
        return timeComparison * -1;
    }

    @Override
    public String toString() {
        return null != reviewId
                ? String.format(
                "Hotel Id: %s\nReview Id: %s\nRating Overall: %d\nTitle: %s\nReview Text: %s\nUser's Nick Name: %s\nPosted Date: %s\n\n",
                hotelId,
                reviewId,
                ratingOverall,
                title,
                reviewText,
                userNickname,
                submissionTime.toString())
                : "This hotel has not been reviewed yet.";
    }
}
