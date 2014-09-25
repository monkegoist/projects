package com.goeuro.test;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Represents geographic location.
 */
public class Location {

    private final int id;
    private final String key;
    private final String name;
    private final String fullName;
    private final String airportCode;
    private final String type; // should be a enum, provided we have all possible values
    private final String country;
    private final Coordinates coordinates;
    private final Integer locationId;
    private final Boolean inEurope;
    private final String countryCode;
    private final Boolean coreCountry;
    private final Integer distance;

    public Location(@JsonProperty("_id") int id,
                    @JsonProperty("key") String key,
                    @JsonProperty("name") String name,
                    @JsonProperty("fullName") String fullName,
                    @JsonProperty("iata_airport_code") String airportCode,
                    @JsonProperty("type") String type,
                    @JsonProperty("country") String country,
                    @JsonProperty("geo_position") Coordinates coordinates,
                    @JsonProperty("locationId") Integer locationId,
                    @JsonProperty("inEurope") Boolean inEurope,
                    @JsonProperty("countryCode") String countryCode,
                    @JsonProperty("coreCountry") Boolean coreCountry,
                    @JsonProperty("distance") Integer distance) {
        this.id = id;
        this.key = key;
        this.name = checkNotNull(name, "name");
        this.fullName = fullName;
        this.airportCode = airportCode;
        this.type = checkNotNull(type, "type");
        this.country = checkNotNull(country, "country");
        this.coordinates = checkNotNull(coordinates, "coordinates");
        this.locationId = locationId;
        this.inEurope = inEurope;
        this.countryCode = countryCode;
        this.coreCountry = coreCountry;
        this.distance = distance;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    @Override
    public String toString() {
        return "Location{" +
                "id=" + id +
                ", key='" + key + '\'' +
                ", name='" + name + '\'' +
                ", fullName='" + fullName + '\'' +
                ", airportCode='" + airportCode + '\'' +
                ", type='" + type + '\'' +
                ", country='" + country + '\'' +
                ", coordinates=" + coordinates +
                ", locationId=" + locationId +
                ", inEurope=" + inEurope +
                ", countryCode='" + countryCode + '\'' +
                ", coreCountry=" + coreCountry +
                ", distance=" + distance +
                '}';
    }

    public static class Coordinates {
        private final double latitude;
        private final double longitude;

        @JsonCreator
        public Coordinates(@JsonProperty("latitude") double latitude,
                           @JsonProperty("longitude") double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }

        public double getLatitude() {
            return latitude;
        }

        public double getLongitude() {
            return longitude;
        }

        @Override
        public String toString() {
            return "Coordinates{" +
                    "latitude=" + latitude +
                    ", longitude=" + longitude +
                    '}';
        }
    }
}
