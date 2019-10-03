package de.tubs.variantwrynn.util.fide;

import de.ovgu.featureide.fm.core.base.IFeatureModel;
import de.ovgu.featureide.fm.core.base.impl.FMFormatManager;
import de.ovgu.featureide.fm.core.configuration.Configuration;
import de.ovgu.featureide.fm.core.io.IFeatureModelFormat;
import de.ovgu.featureide.fm.core.io.Problem;
import de.ovgu.featureide.fm.core.io.ProblemList;
import de.ovgu.featureide.fm.core.io.dimacs.DIMACSFormat;
import de.ovgu.featureide.fm.core.io.manager.SimpleFileHandler;
import de.ovgu.featureide.fm.core.io.xml.XmlFeatureModelFormat;

import java.io.File;
import java.nio.file.Paths;

public class IO {
    public static boolean fileExists(String path) {
        return new File(path).exists();
    }

    public static IFeatureModel loadFile(String path) {
        IFeatureModel fm = FeatureModelCreation.createFeatureModel();
        SimpleFileHandler.load(Paths.get(path), fm, FMFormatManager.getInstance());
        return fm;
    }

    public static boolean writeFile(String path, IFeatureModel model, boolean overwrite) {
        if (fileExists(path) && !overwrite)
            return false;

        IFeatureModelFormat fmFormat = null;

        if (path.endsWith(".xml")) {
            fmFormat = new XmlFeatureModelFormat();
        } else if(path.endsWith(".dimacs")) {
            fmFormat = new DIMACSFormat();
        }

        if (fmFormat == null) {
            System.out.println("[util.fide.IO.writeFile] Format \"" + path.substring(path.lastIndexOf('.')) + "\" not recognised! \n Aborting export ...");
            return false;
        }

        ProblemList problems = SimpleFileHandler.save(Paths.get(path), model, fmFormat);
        for (Problem p : problems) {
            System.out.println("[util.fide.IO.writeFile] " + p);
        }

        return true;
    }

    public static boolean writeFile(String path, Configuration configuration, boolean overwrite) {

        return false;
    }
}
