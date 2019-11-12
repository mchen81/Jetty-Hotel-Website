package hotelapp;


import customLock.ReentrantReadWriteLock;
import hotelapp.bean.Hotel;
import hotelapp.bean.Review;
import hotelapp.bean.TouristAttraction;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Class ThreadSafeHotelData - extends class HotelData (rename your class from project 1 as needed).
 * Thread-safe, uses ReentrantReadWriteLock to synchronize access to all data structures.
 */
public class ThreadSafeHotelData extends HotelData {

    private ReentrantReadWriteLock lock;

    /**
     * Default constructor.
     */
    public ThreadSafeHotelData() {
        super();
        lock = new ReentrantReadWriteLock();
    }

    /**
     * Overrides addHotel method from HotelData class to make it thread-safe; uses the lock.
     * Create a Hotel given the parameters, and add it to the appropriate data
     * structure(s).
     *
     * @param hotelId       - the id of the hotel
     * @param hotelName     - the name of the hotel
     * @param city          - the city where the hotel is located
     * @param state         - the state where the hotel is located.
     * @param streetAddress - the building number and the street
     * @param lat
     * @param lon
     */
    @Override
    public void addHotel(String hotelId, String hotelName, String city, String state, String streetAddress, double lat,
                         double lon) {
        try {
            lock.lockWrite();
            super.addHotel(hotelId, hotelName, city, state, streetAddress, lat, lon);
        } finally {
            lock.unlockWrite();
        }
    }

    /**
     * Overrides addHotel method from HotelData class to make it thread-safe; uses the lock.
     * Create a Hotel given the parameters, and add it to the appropriate data
     * structure(s).
     *
     * @param hotel - the hotel object contains hotel's info
     */
    @Override
    public void addHotel(Hotel hotel) {
        try {
            lock.lockWrite();
            this.addHotel(hotel.getHotelId(),
                    hotel.getName(),
                    hotel.getCity(),
                    hotel.getState(),
                    hotel.getStreetAddress(),
                    hotel.getLatitude(),
                    hotel.getLongitude());
        } finally {
            lock.unlockWrite();
        }
    }

    /**
     * Overrides addReview method from HotelData class to make it thread-safe; uses the lock.
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
    @Override
    public boolean addReview(String hotelId, String reviewId, int rating, String reviewTitle, String review,
                             boolean isRecom, String date, String username) {
        try {
            lock.lockWrite();
            return super.addReview(hotelId, reviewId, rating, reviewTitle, review, isRecom, date, username);
        } finally {
            lock.unlockWrite();
        }
    }

    /**
     * Overrides addReview method from HotelData class to make it thread-safe; uses the lock.
     *
     * @param review - the review object contains all review's information
     * @return true if successful, false if unsuccessful because of invalid date
     * or rating. Needs to catch and handle the following exceptions:
     * ParseException if the date is invalid InvalidRatingException if
     * the rating is out of range
     */
    @Override
    public boolean addReview(Review review) {
        try {
            lock.lockWrite();
            return super.addReview(review);
        } finally {
            lock.unlockWrite();
        }
    }

    /**
     * Overrides addAllReviews method from HotelData class to make it thread-safe, use the lock
     *
     * @param hotelId Single Hotel Id
     * @param reviews the list of reviews
     * @return true if success
     */
    @Override
    public boolean addAllReviews(String hotelId, List<Review> reviews) {
        try {
            lock.lockWrite();
            return super.addAllReviews(hotelId, reviews);
        } finally {
            lock.unlockWrite();
        }
    }

    /**
     * Overrides toString method of class HotelData to make it thread-safe.
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
     * @param hotelId
     * @return - output string.
     */
    @Override
    public String toString(String hotelId) {
        try {
            lock.lockRead();
            return super.toString(hotelId);
        } finally {
            lock.unlockRead();
        }
    }

    /**
     * Overrides the method printToFile of the parent class to make it thread-safe.
     * Save the string representation of the hotel data to the file specified by
     * filename in the following format: an empty line A line of 20 asterisks
     * ******************** on the next line information for each hotel, printed
     * in the format described in the toString method of this class.
     * <p>
     * The hotels should be sorted by hotel ids
     *
     * @param filename - Path specifying where to save the output.
     */
    @Override
    public void printToFile(Path filename) {
        try {
            lock.lockRead();
            super.printToFile(filename);
        } finally {
            lock.unlockRead();
        }
    }

    /**
     * Overrides a method of the parent class to make it thread-safe.
     * Return an alphabetized list of the ids of all hotels
     *
     * @return
     */
    @Override
    public List<String> getHotels() {
        try {
            lock.lockRead();
            return new ArrayList<>(hotels);
        } finally {
            lock.unlockRead();
        }
    }

    @Override
    public Hotel getHotelInstance(String hotelId) {
        try {
            lock.lockRead();
            return super.getHotelInstance(hotelId);
        } finally {
            lock.unlockRead();
        }
    }

    /**
     * @param hotelId hotel id
     * @return true if exists hotel id
     */
    @Override
    public boolean hasHotel(String hotelId) {
        try {
            lock.lockRead();
            return super.hasHotel(hotelId);
        } finally {
            lock.unlockRead();
        }
    }

    @Override
    public void clearAttractions() {
        try {
            lock.lockWrite();
            super.clearAttractions();
        } finally {
            lock.unlockWrite();
        }
    }

    /**
     * put attractions into map
     *
     * @param hotelId            hotel id
     * @param touristAttractions a list of TouristAttraction
     */
    @Override
    public void putAttractions(String hotelId, List<TouristAttraction> touristAttractions) {
        try {
            lock.lockWrite();
            super.putAttractions(hotelId, touristAttractions);
        } finally {
            lock.unlockWrite();
        }
    }

    /**
     * get a list of attractions by hotel id
     *
     * @param hotelId hotel id
     * @return a list of attractions
     */
    @Override
    public List<TouristAttraction> getAttractions(String hotelId) {
        try {
            lock.lockRead();
            return super.getAttractions(hotelId);
        } finally {
            lock.unlockRead();
        }
    }

    @Override
    public List<Review> getReviews(String hotelId) {
        try {
            lock.lockRead();
            return super.getReviews(hotelId);
        } finally {
            lock.unlockRead();
        }
    }
}