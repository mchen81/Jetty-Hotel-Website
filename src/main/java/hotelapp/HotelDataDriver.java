package hotelapp;

import java.nio.file.Paths;

public class HotelDataDriver {

    private ThreadSafeHotelData hotelData;

    public HotelDataDriver() {
        prepareHotelData();
    }

    private void prepareHotelData() {
        hotelData = new ThreadSafeHotelData();
        HotelDataBuilder hotelDataBuilder = new HotelDataBuilder(hotelData);
        hotelDataBuilder.loadHotelInfo("input/hotels.json");
        hotelDataBuilder.loadReviews(Paths.get("input/reviews"));
    }

    public ThreadSafeHotelData getHotelData() {
        return hotelData;
    }
}
