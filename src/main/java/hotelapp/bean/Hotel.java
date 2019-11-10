package hotelapp.bean;

public class Hotel {
    private String name;
    private String hotelId;
    private Double latitude;
    private Double longitude;
    private String city;
    private String state;
    private String streetAddress;

    private String areaDescription;
    private String propertyDescription;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHotelId() {
        return hotelId;
    }

    public void setHotelId(String hotelId) {
        this.hotelId = hotelId;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getStreetAddress() {
        return streetAddress;
    }

    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }

    public String getAddress() {
        return String.format("%s, %s, %s", streetAddress, city, state);
    }

    public String getAreaDescription() {
        return areaDescription;
    }

    public void setAreaDescription(String areaDescription) {
        this.areaDescription = areaDescription;
    }

    public String getPropertyDescription() {
        return propertyDescription;
    }

    public void setPropertyDescription(String propertyDescription) {
        this.propertyDescription = propertyDescription;
    }

    @Override
    public String toString() {
        return "Name: " + this.name
                + "\nId: " + this.hotelId
                + "\nLatitude: " + this.latitude
                + "\nLongitude: " + this.longitude
                + "\nAddress: " + getAddress();
    }

    public String getDescription() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(this.hotelId);
        stringBuilder.append(": ");
        stringBuilder.append(this.name);
        stringBuilder.append("\n\n");
        stringBuilder.append("About This Area: ");
        stringBuilder.append("\n");
        stringBuilder.append(null != areaDescription ? areaDescription : "No Area Description Found");
        stringBuilder.append("\n\n");
        stringBuilder.append("Property Description: ");
        stringBuilder.append("\n");
        stringBuilder.append(null != propertyDescription ? propertyDescription : "No Property Description Found");
        stringBuilder.append("\n");
        return stringBuilder.toString();
    }
}