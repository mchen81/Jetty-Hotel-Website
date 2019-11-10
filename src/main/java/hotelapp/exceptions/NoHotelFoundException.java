package hotelapp.exceptions;

public class NoHotelFoundException extends RuntimeException {
    public NoHotelFoundException() {
        super("No Hotel Found");
    }
}