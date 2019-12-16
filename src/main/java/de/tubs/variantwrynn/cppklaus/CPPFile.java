package de.tubs.variantwrynn.cppklaus;

import de.ovgu.featureide.fm.core.configuration.Configuration;
import de.tubs.variantwrynn.core.model.Artefact;
import de.tubs.variantwrynn.util.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class CPPFile implements Artefact {
    private final File sourceDir;
    private final File file;
    private FeatureAnnotation<String> content;

    /**
     *
     * @param file The file on disk represented by this CPPFile instance.
     * @param sourceDir The project directory (or parent directory) of the file containing this file.
     */
    public CPPFile(File file, File sourceDir) {
        this.file = file;
        this.sourceDir = sourceDir;

        if (!sourceDir.isDirectory()) {
            throw new IllegalArgumentException("sourceDir must be a directory as it represents the project directory of this source file.");
        }

        if (!file.getAbsolutePath().startsWith(sourceDir.getAbsolutePath())) {
            throw new IllegalArgumentException("Given file \"" + file + "\" is not inside given source directory \"" + sourceDir + "\"!");
        }
    }

    public File getFile() {
        return file;
    }

    public File getSourceDirectory() {
        return sourceDir;
    }

    public void setContent(FeatureAnnotation<String> content) {
        this.content = content;
    }

    public FeatureAnnotation<String> getContent() {
        return content;
    }

    public CPPFile toVariant(Configuration configuration) {
        CPPFile f = new CPPFile(file, sourceDir);
        f.setContent(content.toVariant(configuration));
        return f;
    }

    public boolean contains(Artefact artefact) {
        if (getSimilarityWith(artefact) == 1) {
            return true;
        }

        return content.contains(artefact);
    }

    @Override
    public float getSimilarityWith(Artefact other) {
        if (other instanceof CPPFile) {
            // This should be sufficient
            return this.file.equals(((CPPFile) other).file) ? 1 : 0;
        }

        return 0;
    }

    public void storeAt(File outPath) {
        String sourceDir = FileUtils.toUnix(getSourceDirectory().getAbsolutePath());
        String file = FileUtils.toUnix(getFile().getAbsolutePath());

        // We can assume: file.startsWith(sourceDir) due to CPPFile constructor
        String relativeOutputFile = file.replaceFirst(sourceDir, "");

        File outFile = FileUtils.getOrCreate(new File(outPath.getAbsolutePath(), relativeOutputFile));
        System.out.println("[CPPFile.storeAt] " + outFile);

        try (PrintWriter out = new PrintWriter(outFile)) {
            if (VariantGenerator.PRINTWITHMAPPINGS) {
                out.println(getContent().prettyPrint());
            } else {
                out.println(getContent().getCode());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
