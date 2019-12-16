package de.tubs.variantwrynn.cppklaus;

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

    public void storeAt(File absoluteOutputPath) {
        File outPath = new File(absoluteOutputPath.toString(), "Variant_" + ConfigurationUtils.toShortName(configuration));
        FileUtils.getOrCreateDir(outPath);

        for (CPPFile cppfile : sourceFiles) {
            cppfile.storeAt(outPath);
        }
    }
}
