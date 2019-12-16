package de.tubs.variantwrynn.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.Objects;

public class FileUtils {
    public static File getOrCreateDir(File path) {
        if (!path.exists()) {
            getOrCreateDir(path.getParentFile());
            path.mkdirs();
            assert path.exists();
        }

        return path;
    }

    public static File getOrCreateDir(String path) {
        return getOrCreateDir(new File(path));
    }

    public static File getOrCreate(File file) {
        if (!file.exists()) {
            getOrCreateDir(file.getParentFile());
            try {
                boolean fileCreated = file.createNewFile();
                assert(fileCreated);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }

    public static File getOrCreate(String path) {
        return getOrCreate(new File(path));
    }

    public static void addAllFilesInDirectory(File file, List<File> files) {
        if (file.isFile()) {
            files.add(file);
        } else if (file.isDirectory()) {
            for (File childFile : Objects.requireNonNull(file.listFiles())) {
                addAllFilesInDirectory(childFile, files);
            }
        }
    }

    public static File toFile(URL url) {
        File f;

        try {
            f = new File(url.toURI());
        } catch (URISyntaxException e) {
            f = new File(url.getPath());
        }

        return f;
    }

    public static String toUnix(String path) {
        return path.replaceAll("\\\\", "/");
    }

    public static File getDirectory(File file) {
        if (file.isDirectory())
            return file;
        return file.getParentFile();
    }

    public static void writeText(File outFile, String code) {
        try (PrintWriter out = new PrintWriter(outFile)) {
            out.println(code);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static boolean deleteDirectory(File directory, String guard) {
        if (!directory.getAbsolutePath().contains(guard)) {
            System.err.println("[FileUtils.deleteDirectory] Aborting because file \"" + directory + "\" does not contain guard \"" + guard + "\"!");
        }

        if (directory.exists()) {
            File[] files = directory.listFiles();

            if (null != files) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        deleteDirectory(file, guard);
                    } else {
                        if (!file.delete()) {
                            System.err.println("[FileUtils.deleteDirectory] File \"" + file + "\" could not be deleted!");
                        }
                    }
                }
            }
        }
        return directory.delete();
    }
}
