import com.google.common.base.Joiner;

import java.io.*;
import java.util.List;

/**
 * No wonders, it writes data.
 */
public class DataWriter {

    /**
     * Writes data as csv.
     *
     * @param outputFile file to write to
     * @param data       list of locations
     * @throws IOException error while writing data
     */
    public void writeCsv(File outputFile, List<Location> data) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(outputFile), Configuration.CHARSET))) {
            for (Location location : data) {
                String csv = Joiner.on(",").join(
                        location.get_type(),
                        location.get_id(),
                        location.getName(),
                        location.getType(),
                        location.getCoordinates().getLatitude(),
                        location.getCoordinates().getLongitude()
                );
                writer.write(csv + "\n");
            }
        }
    }
}
