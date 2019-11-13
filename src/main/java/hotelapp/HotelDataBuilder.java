package hotelapp;

import com.google.gson.stream.JsonReader;
import hotelapp.bean.Hotel;
import hotelapp.bean.Review;
import hotelapp.exceptions.InvalidRatingException;
import hotelapp.exceptions.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Class HotelDataBuilder. Loads hotel info from input files to ThreadSafeHotelData (using multithreading).
 */
public class HotelDataBuilder {
    private ThreadSafeHotelData hdata; // the "big" ThreadSafeHotelData that will contain all hotel and reviews info
    private ExecutorService executor;


    /**
     * Constructor for class HotelDataBuilder.
     *
     * @param data
     */
    public HotelDataBuilder(ThreadSafeHotelData data) {
        hdata = data;
        executor = Executors.newCachedThreadPool();
    }

    /**
     * Constructor for class HotelDataBuilder that takes ThreadSafeHotelData and
     * the number of threads to create as a parameter.
     *
     * @param data
     * @param numThreads
     */
    public HotelDataBuilder(ThreadSafeHotelData data, int numThreads) {
        hdata = data;
        executor = Executors.newFixedThreadPool(numThreads);
    }

    /**
     * Read the json file with information about the hotels and load it into the
     * appropriate data structure(s).
     *
     * @param jsonFilename
     */
    public void loadHotelInfo(String jsonFilename) throws IOException {
        parseHotel(jsonFilename);
    }

    /**
     * Loads reviews from json files. Recursively processes subfolders.
     * Each json file with reviews should be processed concurrently (you need to create a new runnable job for each
     * json file that you encounter)
     *
     * @param dir
     */
    public void loadReviews(Path dir) throws IOException {
        List<String> files = readDir(dir.toString());
        for (String filepath : files) {
            executor.execute(new ReviewParser(filepath));
        }
        executor.shutdown();
        try {
            executor.awaitTermination(60, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * parse a single json file to a List of a hotel's reviews
     *
     * @param filePath a json filepath
     * @return a List of a hotel's reviews
     */
    private List<Review> parseSingleReviewJsonFile(String filePath) {
        List<Review> result = new ArrayList<>();
        String hotelId = "";
        try (JsonReader jsonReader = new JsonReader(new FileReader(filePath))) {
            jsonReader.beginObject(); // begin first json object (start with reviewDetails)
            while (jsonReader.hasNext()) {
                String reviewName = jsonReader.nextName();
                if (reviewName.equals("reviewDetails")) {
                    jsonReader.beginObject(); // begin obj2 (start with startIndex)
                    while (jsonReader.hasNext()) { // in obj2 - review details
                        String detailName = jsonReader.nextName();
                        if (detailName.equals("reviewCollection")) {
                            jsonReader.beginObject(); // begin obj3
                            while (jsonReader.hasNext()) { // in obj3- review collection
                                String collectionName = jsonReader.nextName();
                                if (collectionName.equals("review")) {
                                    // in review array
                                    jsonReader.beginArray();
                                    while (jsonReader.hasNext()) { // in arry
                                        jsonReader.beginObject();
                                        Review review = new Review();
                                        while (jsonReader.hasNext()) {
                                            String reviewInfoName = jsonReader.nextName();
                                            switch (reviewInfoName) {
                                                case "hotelId":
                                                    review.setHotelId(jsonReader.nextString());
                                                    break;
                                                case "reviewId":
                                                    review.setReviewId(jsonReader.nextString());
                                                    break;
                                                case "title":
                                                    review.setTitle(jsonReader.nextString());
                                                    break;
                                                case "reviewText":
                                                    review.setReviewText(jsonReader.nextString());
                                                    break;
                                                case "userNickname":
                                                    review.setUserNickname(jsonReader.nextString());
                                                    break;
                                                case "ratingOverall":
                                                    review.setRatingOverall(jsonReader.nextInt());
                                                    break;
                                                case "reviewSubmissionTime":
                                                    review.setSubmissionTime(jsonReader.nextString());
                                                    break;
                                                default:
                                                    jsonReader.skipValue();
                                            }
                                        }
                                        result.add(review);
                                        jsonReader.endObject();
                                    }
                                    jsonReader.endArray();
                                } else {
                                    jsonReader.skipValue(); // obj 3
                                }
                            }
                            jsonReader.endObject(); // end obj3
                        } else {
                            jsonReader.skipValue();
                        }
                    }
                    jsonReader.endObject();// end obj2
                } else {
                    jsonReader.skipValue();
                }
            }
            jsonReader.endObject(); // obj 1
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        } catch (ParseException e) {
            throw new IllegalArgumentException(e);
        } catch (InvalidRatingException e) {
            throw new IllegalArgumentException(e);
        }
        return result;
    }

    /**
     * get all file path in a directory
     *
     * @param dir the path of a dir
     * @return a string list of json file paths
     */
    private List<String> readDir(String dir) throws IOException {
        Path dirPath = Paths.get(dir);
        // for return
        List<String> filePaths = new ArrayList<>();
        // get json files in the directory and add it int to a list

        DirectoryStream<Path> filesList = Files.newDirectoryStream(dirPath);
        for (Path p : filesList) {
            if (p.toString().endsWith(".json")) {
                filePaths.add(p.toString());
            } else if (Files.isDirectory(p)) {
                filePaths.addAll(readDir(p.toString()));
            }
        }
        return filePaths;
    }

    /**
     * parsing hotel's info
     *
     * @param filePath file's path
     */
    private void parseHotel(String filePath) throws IOException {
        JsonReader jsonReader = new JsonReader(new FileReader(filePath));
        // first object in json
        jsonReader.beginObject();
        while (jsonReader.hasNext()) {
            // json key
            String name = jsonReader.nextName();
            // we just need hotels info in the array of key "sr"
            if (name.equals("sr")) {
                // begin array
                jsonReader.beginArray();
                while (jsonReader.hasNext()) { // for each hotel in the array
                    // begin "real" hotel json object
                    jsonReader.beginObject();
                    Hotel hotel = new Hotel();
                    while (jsonReader.hasNext()) { // for hotel info
                        String hotelInfo = jsonReader.nextName();
                        switch (hotelInfo) {
                            case "id":
                                hotel.setHotelId(jsonReader.nextString());
                                break;
                            case "f":
                                hotel.setName(jsonReader.nextString());
                                break;
                            case "ad":
                                hotel.setStreetAddress(jsonReader.nextString());
                                break;
                            case "ci":
                                hotel.setCity(jsonReader.nextString());
                                break;
                            case "pr":
                                hotel.setState(jsonReader.nextString());
                                break;
                            case "ll": // for setting latitude and longitude
                                jsonReader.beginObject(); // open coordinate object
                                while (jsonReader.hasNext()) {
                                    String coordinate = jsonReader.nextName();
                                    if (coordinate.equals("lat")) {
                                        hotel.setLatitude(Double.valueOf(jsonReader.nextString()));
                                    } else if (coordinate.equals("lng")) {
                                        hotel.setLongitude(Double.valueOf(jsonReader.nextString()));
                                    } else {
                                        jsonReader.skipValue();
                                    }
                                }
                                jsonReader.endObject(); // close coordinate object
                                break;
                            default:
                                jsonReader.skipValue();
                        }
                    }
                    jsonReader.endObject(); // end hotels object
                    // put hotel-Id and hotel object to the map
                    hdata.addHotel(hotel);
                }
            } else { // skip value except "sr"
                jsonReader.skipValue();
            }
        }
    }

    /**
     * Prints all hotel info to the file. Calls hdata's printToFile method.
     */
    public void printToFile(Path filename) {
        hdata.printToFile(filename);
    }

    // FILL IN CODE: add an inner class and other methods as needed
    class ReviewParser implements Runnable {
        private String filePath;

        private ReviewParser(String filePath) {
            this.filePath = filePath;
        }

        @Override
        public void run() {
            List<Review> reviews = parseSingleReviewJsonFile(filePath);
            if (reviews == null || reviews.size() == 0) {
                return;
            }
            hdata.addAllReviews(reviews.get(0).getHotelId(), reviews);
        }
    }
}
