package za.ac.sun.cs.providentia.domain;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.Serializable;

public class Business implements Serializable {

    private static final long serialVersionUID = 100210885490796577L;
    private String businessId;
    private String address;
    private String postalCode;
    private boolean isOpen;
    private String[] categories;
    private String city;
    private String name;
    private double longitude;
    private double latitude;
    private String state;
    private double stars;

    public Business() {
    }

    public String getBusinessId() {
        return businessId;
    }

    public void setBusinessId(final String businessId) {
        this.businessId = businessId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(final String address) {
        this.address = address;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(final String postalCode) {
        this.postalCode = postalCode;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setIsOpen(final boolean isOpen) {
        this.isOpen = isOpen;
    }

    public String[] getCategories() {
        return categories;
    }

    public void setCategories(final String[] categories) {
        this.categories = categories;
    }

    public String getCity() {
        return city;
    }

    public void setCity(final String city) {
        this.city = city;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(final double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(final double latitude) {
        this.latitude = latitude;
    }

    public String getState() {
        return state;
    }

    public void setState(final String state) {
        this.state = state;
    }

    public double getStars() {
        return stars;
    }

    public void setStars(final double stars) {
        this.stars = stars;
    }

    public String toEsString() {
        final StringBuilder sb = new StringBuilder("{");
        sb.append("\"businessId\":\"").append(businessId).append('\"');
        sb.append(", \"address\":\"").append(address).append('\"');
        sb.append(", \"postalCode\":\"").append(postalCode).append('\"');
        sb.append(", \"open\":").append(isOpen);
        sb.append(", \"categories\": [");
        for (int i = 0; i < categories.length; i++) {
            sb.append('\"').append(categories[i]).append('\"');
            if (i < categories.length - 1)
                sb.append(", ");
        }
        sb.append("]");
        sb.append(", \"city\":\"").append(city).append('\"');
        sb.append(", \"name\":\"").append(name.replaceAll("\"", "'")).append('\"');
        sb.append(", \"location\": \"").append(latitude).append(", ").append(longitude).append("\"");
        sb.append(", \"state\":\"").append(state).append('\"');
        sb.append(", \"stars\":").append(stars);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public String toString() {
        final ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(this);
        } catch (final IOException e) {
            final StringBuilder sb = new StringBuilder("Business{");
            sb.append("businessId='").append(businessId).append('\'');
            sb.append(", address='").append(address).append('\'');
            sb.append(", postalCode='").append(postalCode).append('\'');
            sb.append(", isOpen=").append(isOpen);
            sb.append(", categories=").append(categories);
            sb.append(", city='").append(city).append('\'');
            sb.append(", name='").append(name).append('\'');
            sb.append(", longitude=").append(longitude);
            sb.append(", latitude=").append(latitude);
            sb.append(", state='").append(state).append('\'');
            sb.append(", stars=").append(stars);
            sb.append('}');
            return sb.toString();
        }
    }
}
