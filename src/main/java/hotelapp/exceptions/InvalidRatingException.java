package hotelapp.exceptions;

public class InvalidRatingException extends Exception {
    public InvalidRatingException() {
        super("Rating should be between 0 and 5");
    }
}
