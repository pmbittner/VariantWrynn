package de.tubs.variantwrynn.util;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

public class FileUtils {
    public static File toFile(URL url) {
        File f;

        try {
            f = new File(url.toURI());
        } catch (URISyntaxException e) {
            f = new File(url.getPath());
        }

        return f;
    }
}
