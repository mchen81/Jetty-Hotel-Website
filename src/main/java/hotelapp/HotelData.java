package hotelapp;

import hotelapp.bean.Hotel;
import hotelapp.bean.Review;
import hotelapp.exceptions.InvalidRatingException;
import hotelapp.exceptions.NoHotelFoundException;
import hotelapp.exceptions.ParseException;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.*;

public class HotelData {

    protected Map<String, Hotel> hotelsBook;

    protected Map<String, TreeSet<Review>> reviewsBook;

    protected Set<String> hotels;

    protected Map<String, String> aboutThisArea;


    /**
     * Default Constructor
     */
    public HotelData() {
        hotelsBook = new HashMap<>();
        reviewsBook = new HashMap<>();
        hotels = new TreeSet<>((o1, o2) -> o1.compareTo(o2));
        aboutThisArea = new HashMap<>();
    }


    /**
     * @param hotelId hotel id
     * @return true if exists hotel id
     */
    public boolean hasHotel(String hotelId) {
        return hotelsBook.containsKey(hotelId);
    }


    /**
     * Create a Hotel given the hotel object, and add it to the appropriate data
     * structure(s).
     *
     * @param hotel - the hotel object containing hotel information
     */
    public void addHotel(Hotel hotel) {
        addHotel(hotel.getHotelId(),
                hotel.getName(),
                hotel.getCity(),
                hotel.getState(),
                hotel.getStreetAddress(),
                hotel.getLatitude(),
                hotel.getLongitude());
    }

    /**
     * Create a Hotel given the parameters, and add it to the appropriate data
     * structure(s).
     *
     * @param hotelId       - the id of the hotel
     * @param hotelName     - the name of the hotel
     * @param city          - the city where the hotel is located
     * @param state         - the state where the hotel is located.
     * @param streetAddress - the building number and the street
     * @param lat           - the latitude of hotel is located
     * @param lon           - the longitude of hotel is located
     */
    public void addHotel(String hotelId, String hotelName, String city, String state, String streetAddress, double lat,
                         double lon) {
        Hotel hotel = new Hotel();
        hotel.setHotelId(hotelId);
        hotel.setName(hotelName);
        hotel.setCity(city);
        hotel.setState(state);
        hotel.setStreetAddress(streetAddress);
        hotel.setLatitude(lat);
        hotel.setLongitude(lon);
        hotelsBook.put(hotelId, hotel);
        hotels.add(hotelId);
    }

    /**
     * Add review information to review book
     *
     * @param hotelId     - the id of the hotel reviewed
     * @param reviewId    - the id of the review
     * @param rating      - integer rating 1-5.
     * @param reviewTitle - the title of the review
     * @param review      - text of the review
     * @param isRecom     - whether the user recommends it or not
     * @param date        - date of the review
     * @param username    - the nickname of the user writing the review.
     * @return true if successful, false if unsuccessful because of invalid date
     * or rating. Needs to catch and handle the following exceptions:
     * ParseException if the date is invalid InvalidRatingException if
     * the rating is out of range
     */
    public boolean addReview(String hotelId, String reviewId, int rating, String reviewTitle, String review,
                             boolean isRecom, String date, String username) {
        try {
            Review reviewInfo = new Review();
            reviewInfo.setHotelId(hotelId);
            reviewInfo.setReviewId(reviewId);
            reviewInfo.setRatingOverall(rating);
            reviewInfo.setTitle(reviewTitle);
            reviewInfo.setReviewText(review);
            reviewInfo.setSubmissionTime(date);
            reviewInfo.setUserNickname(username);
            if (!reviewsBook.containsKey(hotelId)) {
                reviewsBook.put(hotelId, new TreeSet<>(Comparator.naturalOrder()));
            }
            reviewsBook.get(hotelId).add(reviewInfo);
        } catch (InvalidRatingException e) {
            return false;
        } catch (ParseException e) {
            return false;
        }
        return true;
    }

    /**
     * Add review information to review book
     *
     * @param review - the review object contains all review's information
     * @return true if successful, false if unsuccessful because of invalid date
     * or rating. Needs to catch and handle the following exceptions:
     * ParseException if the date is invalid InvalidRatingException if
     * the rating is out of range
     */
    public boolean addReview(Review review) {
        try {
            Review reviewInfo = new Review();
            reviewInfo.setHotelId(review.getHotelId());
            reviewInfo.setReviewId(review.getReviewId());
            reviewInfo.setRatingOverall(review.getRatingOverall());
            reviewInfo.setTitle(review.getTitle());
            reviewInfo.setReviewText(review.getReviewText());
            reviewInfo.setSubmissionTime(review.getSubmissionTime());
            reviewInfo.setUserNickname(review.getUserNickname());
            if (!reviewsBook.containsKey(review.getHotelId())) {
                reviewsBook.put(review.getHotelId(), new TreeSet<>(Comparator.naturalOrder()));
            }
            reviewsBook.get(review.getHotelId()).add(reviewInfo);
        } catch (InvalidRatingException e) {
            return false;
        }
        return true;
    }

    /**
     * Give a list of reviews, add to reviews book
     *
     * @param hotelId Single Hotel Id
     * @param reviews the list of reviews
     * @return true if add success
     */
    public boolean addAllReviews(String hotelId, List<Review> reviews) {
        if (!reviewsBook.containsKey(hotelId)) {
            reviewsBook.put(hotelId, new TreeSet<>(Comparator.naturalOrder()));
        }
        reviewsBook.get(hotelId).addAll(reviews);
        return true;
    }

    /**
     * Save the string representation of the hotel data to the file specified by
     * filename in the following format: an empty line A line of 20 asterisks
     * ******************** on the next line information for each hotel, printed
     * in the format described in the toString method of this class.
     * <p>
     * The hotels should be sorted by hotel ids
     *
     * @param filename - Path specifying where to save the output.
     */
    public void printToFile(Path filename) {
        try (PrintWriter pw = new PrintWriter(filename.toFile())) {
            pw.println();
            for (String hotelId : hotels) {
                pw.println("********************");
                pw.println(toString(hotelId));
            }
            pw.close();
        } catch (IOException e) {
            throw new IllegalArgumentException(e.getCause());
        }
    }

    /**
     * Returns a string representing information about the hotel with the given
     * id, including all the reviews for this hotel separated by
     * --------------------
     * Format of the string: HoteName: hotelId
     * streetAddress city, state
     * --------------------
     * Review by username: rating
     * ReviewTitle ReviewText
     * --------------------
     * Review by username: rating
     * ReviewTitle ReviewText ...
     *
     * @param hotelId hotel's id
     * @return - output string.
     */
    public String toString(String hotelId) {
        StringBuilder result = new StringBuilder();
        Hotel hotel = hotelsBook.get(hotelId);
        if (hotel == null) {
            return "";
        }
        result.append(String.format("%s: %s", hotel.getName(), hotel.getHotelId()));
        result.append("\n");
        result.append(hotel.getStreetAddress());
        result.append("\n");
        result.append(String.format("%s, %s\n", hotel.getCity(), hotel.getState()));
        TreeSet<Review> reviews = reviewsBook.get(hotelId);
        if (reviews == null || reviews.size() == 0) {
            return result.toString();
        } else {
            for (Review r : reviews) {
                result.append("--------------------\n");
                result.append(String.format("Review by %s on %s\n", r.getUserNickname(), r.getSubmissionTime()));
                result.append(String.format("Rating: %d\n", r.getRatingOverall()));
                result.append(r.getTitle());
                result.append("\n");
                result.append(r.getReviewText());
                result.append("\n");
            }
        }
        return result.toString();
    }

    /**
     * @return an alphabetized list of the ids of all hotels
     */
    public List<String> getHotels() {
        return new ArrayList<>(hotels);
    }

    /**
     * @param hotelId Hotel Id
     * @return an Hotel Instance
     */
    public Hotel getHotelInstance(String hotelId) {
        return hotelsBook.get(hotelId);
    }

    /**
     * @param hotelId
     * @return
     */
    public List<Review> getReviews(String hotelId) {
        if (reviewsBook.containsKey(hotelId)) {
            return new ArrayList<>(reviewsBook.get(hotelId));
        } else {
            return null;
        }
    }
}
