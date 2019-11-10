package hotelapp;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import hotelapp.bean.Hotel;
import hotelapp.bean.TouristAttraction;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

/**
 * Class responsible for getting tourist attractions near each hotel from the Google Places API.
 * Also scrapes some data about hotels from expedia html webpage.
 */
public class TouristAttractionFinder {

    private static final String host = "https://maps.googleapis.com";
    private static final String path = "/maps/api/place/textsearch/json";

    private String myGoogleAPI;

    private ThreadSafeHotelData hdata;
    // Add instance variables as needed (for example, store a reference to ThreadSafeHotelData)
    // FILL IN CODE: add data structures to store attractions
    // Alternatively, you can store these data structures in ThreadSafeHotelData

    /**
     * Constructor for TouristAttractionFinder
     *
     * @param hdata
     */
    public TouristAttractionFinder(ThreadSafeHotelData hdata) {
        parseConfigFile("input/config.json");
        this.hdata = hdata;
    }

    /**
     * Creates a secure socket to communicate with Google Places API server,
     * sends a GET request (to find attractions close to
     * the hotel within a given radius), and gets a response as a string.
     * Removes headers from the response string and parses the remaining json to
     * get Attractions info. Adds attractions to the corresponding data structure that supports
     * efficient search for tourist attractions given the hotel id.
     */
    public void fetchAttractions(int radiusInMiles) {
        // This method should call getRequest method
        hdata.clearAttractions();
        for (String hotelId : hdata.getHotels()) {
            Hotel hotel = hdata.getHotelInstance(hotelId);
            String urlString = createQueryURL(hotel.getCity(), hotel.getLatitude(), hotel.getLongitude(), convertMilesToMeters(radiusInMiles));
            hdata.putAttractions(hotelId, parseUrlToAttractions(urlString));
        }
    }

    /**
     * Print attractions near the hotels to a file.
     * The format is described in the project description.
     *
     * @param filename
     */
    public void printAttractions(Path filename) {
        try (PrintWriter pw = new PrintWriter(filename.toFile())) {
            for (String hotelId : hdata.getHotels()) {
                Hotel hotel = hdata.getHotelInstance(hotelId);
                pw.printf("Attractions near %s, %s\n", hotelId, hotel.getName());
                for (TouristAttraction touristAttraction : hdata.getAttractions(hotelId)) {
                    pw.println(touristAttraction);
                }
                pw.println("\n++++++++++++++++++++");
            }
            pw.close();
        } catch (IOException e) {
            throw new IllegalArgumentException(e.getCause());
        }
    }


    /**
     * convert miles to meters
     *
     * @param miles the radius in miles
     * @return the radius in meters
     */
    private int convertMilesToMeters(int miles) {
        return miles * 1609;
    }

    /**
     * Takes a host and a string containing path/resource/query and creates a
     * string of the HTTP GET request
     *
     * @param host              Hots
     * @param pathResourceQuery path resource query
     * @return
     */
    private String getRequest(String host, String pathResourceQuery) {
        String request = "GET " + pathResourceQuery + " HTTP/1.1" + System.lineSeparator() // GET
                // request
                + "Host: " + host + System.lineSeparator() // Host header required for HTTP/1.1
                + "Connection: close" + System.lineSeparator() // make sure the server closes the
                // connection after we fetch one page
                + System.lineSeparator();
        return request;
    }

    /**
     * make an URL of request
     *
     * @param city      Hotel name
     * @param latitude  Hotel latitude
     * @param longitude Hotel longitute
     * @param radius    radius in meter
     * @return an URL
     */
    private String createQueryURL(String city, double latitude, double longitude, int radius) {
        String query = "?query=tourist%20attractions+in+" + city.replace(" ", "%20");
        String queryLocation = String.format("&location=%.6f,%.6f&", latitude, longitude);
        String queryRaduis = "radius=" + radius;
        String myApi = "&key=" + myGoogleAPI;
        String result = host + path + query + queryLocation + queryRaduis + myApi;
        return result;
    }

    /**
     * parse a url into a list of attractions
     *
     * @param urlString
     */
    private List<TouristAttraction> parseUrlToAttractions(String urlString) {
        //System.out.println(urlString);
        URL url;
        PrintWriter out = null;
        BufferedReader in = null;
        SSLSocket socket = null;
        try {
            url = new URL(urlString);
            SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
            // HTTPS uses port 443
            socket = (SSLSocket) factory.createSocket(url.getHost(), 443);
            // output stream for the secure socket
            out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
            String request = getRequest(url.getHost(), url.getPath() + "?" + url.getQuery());
            // System.out.println("Request: " + request);
            out.println(request); // send a request to the server
            out.flush();
            // input stream for the secure socket.
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            // use input stream to read server's response
            String line;
            StringBuilder jsonData = new StringBuilder();
            boolean jsonStart = false;
            while ((line = in.readLine()) != null) {
                if (line.startsWith("{")) {
                    jsonStart = true;
                }
                if (jsonStart) {
                    jsonData.append(line);
                }
            }
            //  jsonReader starts from tourist attraction array, parse the jsonReader to an array of TouristAttraction
            TouristAttraction[] touristAttractionArray = parseJsonToTouristAttractions(jsonData.toString());
            return Arrays.asList(touristAttractionArray);
        } catch (IOException e) {
            throw new IllegalArgumentException(
                    "An IOException occured while writing to the socket stream or reading from the stream: " + e);
        } catch (Exception e) {
            throw new IllegalArgumentException("An Exception happen:" + e);
        } finally {
            try {
                // close the streams and the socket
                out.close();
                in.close();
                socket.close();
            } catch (IOException e) {
                System.out.println("An exception occured while trying to close the streams or the socket: " + e);
            }
        }
    }

    /**
     * parse jsonData of String to an array of TouristAttraction
     *
     * @param jsonData JsonData in String
     * @return an array of TouristAttractions
     * @throws IOException
     */
    private TouristAttraction[] parseJsonToTouristAttractions(String jsonData) throws IOException {
        //---------parse json data to gson
        Gson gson = new Gson();
        JsonReader jsonReader = new JsonReader(new StringReader(jsonData)); // read the json data from html
        jsonReader.beginObject();
        while (jsonReader.hasNext()) { // start the jsonReader from "results", the value of results is an json array
            String basicInfo = jsonReader.nextName();
            if (basicInfo.equals("results")) {
                break;
            } else {
                jsonReader.skipValue();
            }
        }
        return gson.fromJson(jsonReader, TouristAttraction[].class);
    }

    /**
     * parse Google API key in config file
     *
     * @param configFilePath the path of config file
     */
    public void parseConfigFile(String configFilePath) {
        Path configPath = Paths.get(configFilePath);
        try {
            JsonReader jsonReader = new JsonReader(new FileReader(configPath.toString()));
            jsonReader.beginObject();
            while (jsonReader.hasNext()) {
                String key = jsonReader.nextName();
                if (key.equals("apikey")) {
                    myGoogleAPI = jsonReader.nextString();
                } else {
                    jsonReader.skipValue();
                }
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("Cannot find config file: Default Path: input/config.json");
        }
    }


}