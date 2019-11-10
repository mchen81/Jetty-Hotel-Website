package hotelapp.bean;

import com.google.gson.annotations.SerializedName;

public class TouristAttraction {
    // FILL IN CODE: add instance variables to store
    // name, rating, address, id

    private String id;
    private String name;
    private double rating;
    @SerializedName("formatted_address")
    private String address;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * Constructor for TouristAttraction
     *
     * @param id      id of the attraction
     * @param name    name of the attraction
     * @param rating  overall rating of the attraction
     * @param address address of the attraction
     */
    public TouristAttraction(String id, String name, double rating, String address) {
        this.id = id;
        this.name = name;
        this.rating = rating;
        this.address = address;
    }
    // FILL IN CODE: add getters as needed

    /**
     * toString() method
     *
     * @return a String representing this TouristAttraction
     */
    @Override
    public String toString() {

        // FILL IN CODE
        return name; // do not forget to change this
    }
}
