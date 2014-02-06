import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Represents geographic location. Immutable.
 */
public class Location {

    // field could be enum, or omitted at all, if we're sure that nothing but 'Position' is ever returned
    private final String _type;
    private final int _id;
    private final String name;
    private final String type;
    private final Coordinates coordinates;

    @JsonCreator
    public Location(@JsonProperty("_type") String _type,
                    @JsonProperty("_id") int _id,
                    @JsonProperty("name") String name,
                    @JsonProperty("type") String type,
                    @JsonProperty("geo_position") Coordinates coordinates) {
        this._type = checkNotNull(_type, "_type");
        this._id = _id;
        this.name = checkNotNull(name, "name");
        this.type = checkNotNull(type, "type");
        this.coordinates = checkNotNull(coordinates, "coordinates");
    }

    public String get_type() {
        return _type;
    }

    public int get_id() {
        return _id;
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
                "_type='" + _type + '\'' +
                ", _id=" + _id +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", coordinates=" + coordinates +
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