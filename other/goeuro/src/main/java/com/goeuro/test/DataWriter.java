package com.goeuro.test;

import com.google.common.base.Joiner;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class DataWriter {

    /**
     * Writes data as csv.
     *
     * @param fileName file to write to
     * @param data     list of locations
     * @throws IOException error while writing data
     */
    public static void writeCsv(String fileName, List<Location> data) throws IOException {
        checkNotNull(fileName, "fileName");
        checkNotNull(data, "data");
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(fileName), Configuration.CHARSET))) {
            for (Location location : data) {
                String csv = Joiner.on(",").join(
                        location.getId(),
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
