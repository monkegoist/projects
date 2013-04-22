package com.devexperts.logvinov.shared;

import java.io.*;

public final class QuoteUtils {

    public static byte[] serialize(Quote quote) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(baos);
            oos.writeObject(quote);
            return baos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (oos != null)
                    oos.close();
                baos.close();
            } catch (IOException e) {
                // ignore
            }
        }
    }

    public static Quote deserialize(byte[] arr) {
        ByteArrayInputStream bais = new ByteArrayInputStream(arr);
        ObjectInputStream ois = null;
        try {
            ois = new ObjectInputStream(bais);
            return (Quote) ois.readObject();
        } catch (ClassNotFoundException | IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (ois != null)
                    ois.close();
                bais.close();
            } catch (IOException e) {
                // ignore
            }
        }
    }
}
