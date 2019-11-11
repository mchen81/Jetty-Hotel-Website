package tests;

import hotelapp.HotelDataDriver;
import hotelapp.ThreadSafeHotelData;
import rawHttpServer.HttpRequest;

public class HttpRequestTest {


    public static void main(String[] args) {
        testHotelData();

    }

    public static void testHttpRequest() {
        String httpRequest = "GET /action?name=Jerry&age=23 /HTTP/1.1";

        HttpRequest myHttpRequest = new HttpRequest(httpRequest);

        System.out.println("CRUD: " + myHttpRequest.getHttpCRUD());
        System.out.println("Action: " + myHttpRequest.getAction());
        System.out.println("My name is " + myHttpRequest.getValue("name"));
        System.out.println("My age is " + myHttpRequest.getValue("age"));
    }

    public static void testHotelData() {
        HotelDataDriver hotelDataDriver = new HotelDataDriver();
        ThreadSafeHotelData hotelData = hotelDataDriver.getHotelData();

        System.out.println(hotelData.getHotels());
    }

}
