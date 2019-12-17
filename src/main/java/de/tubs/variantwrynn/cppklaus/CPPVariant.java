package de.tubs.variantwrynn.cppklaus;

import de.ovgu.featureide.fm.core.base.IFeature;
import de.ovgu.featureide.fm.core.configuration.Configuration;
import de.tubs.variantwrynn.core.model.Artefact;
import de.tubs.variantwrynn.core.model.Variant;
import de.tubs.variantwrynn.util.FileUtils;
import de.tubs.variantwrynn.util.fide.ConfigurationUtils;

import java.io.File;
import java.util.List;

public class CPPVariant implements Variant {
    private List<CPPFile> sourceFiles;
    private Configuration configuration;

    public CPPVariant(List<CPPFile> code, Configuration config) {
        this.sourceFiles = code;
        this.configuration = config;
    }

    @Override
    public boolean contains(Artefact artefact) {
        for (CPPFile file : sourceFiles) {
            if (file.contains(artefact)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Configuration getConfiguration() {
        return configuration;
    }

    public void storeAt(File outputDir) {
        for (CPPFile cppfile : sourceFiles) {
            cppfile.storeAt(outputDir);
        }

        // Write config
        StringBuilder configText = new StringBuilder();
        for (IFeature f : configuration.getSelectedFeatures()) {
            configText.append(f).append("\n");
        }
        FileUtils.writeText(FileUtils.getOrCreate(new File(outputDir, "config.txt")), configText.toString());
    }
}
