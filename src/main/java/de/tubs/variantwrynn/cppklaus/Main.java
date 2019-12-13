package de.tubs.variantwrynn.cppklaus;

import de.ovgu.featureide.fm.core.base.IFeatureModel;
import de.ovgu.featureide.fm.core.configuration.Configuration;
import de.ovgu.featureide.fm.core.functional.Functional;
import de.ovgu.featureide.fm.core.job.monitor.IMonitor;
import de.ovgu.featureide.fm.core.job.monitor.NullMonitor;
import de.tubs.variantwrynn.util.FileUtils;
import de.tubs.variantwrynn.util.fide.ConfigurationUtils;
import de.tubs.variantwrynn.util.fide.FeatureModelUtils;
import de.tubs.variantwrynn.util.fide.IO;
import org.prop4j.analyses.RandomConfigurationGenerator;
import org.prop4j.solver.SatInstance;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Main {
    static class ARGS {
        private static final String NamePrefix = "-name=";
        private static final String FMPrefix = "-fm=";
        private static final String FMInResourcesPrefix = "-resfm=";
        private static final String SrcDirPrefix = "-src=";
        private static final String SrcInResourcesPrefix = "-resrc=";

        String name = null;
        List<File> projectFiles;
        IFeatureModel fm = null;

        public ARGS() {
            projectFiles = new ArrayList<>();
        }

        private void warnAboutDuplicateSpecificationOf(String thingy) {
            System.err.println("Duplicate specification of " + thingy + " in program arguments.");
        }

        public void argParse(String[] args) {
            for (String arg : args) {
                if (arg.startsWith(NamePrefix)) {
                    if (name == null) {
                        name = arg.substring(NamePrefix.length());
                    } else {
                        warnAboutDuplicateSpecificationOf("name");
                    }
                } else if (arg.startsWith(FMPrefix)) {
                    if (fm == null) {
                        fm = IO.loadFile(arg.substring(FMPrefix.length()));
                    } else {
                        warnAboutDuplicateSpecificationOf("feature model");
                    }
                } else if (arg.startsWith(FMInResourcesPrefix)) {
                    if (fm == null) {
                        String fmPath = arg.substring(FMInResourcesPrefix.length());
                        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
                        File file = FileUtils.toFile(Objects.requireNonNull(classLoader.getResource(fmPath)));
                        fm = IO.loadFile(file.getAbsolutePath());
                    } else {
                        warnAboutDuplicateSpecificationOf("feature model");
                    }
                } else if (arg.startsWith(SrcDirPrefix)) {
                    String sources = arg.substring(SrcDirPrefix.length());
                    File file = new File(sources);
                    processSourceDirectory(file, projectFiles);
                } else if (arg.startsWith(SrcInResourcesPrefix)) {
                    String sources = arg.substring(SrcInResourcesPrefix.length());

                    ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
                    File file = FileUtils.toFile(Objects.requireNonNull(classLoader.getResource(sources)));

                    //System.out.println("ARGS: Processing sources: " + sources);
                    //File file = new File(sources);
                    processSourceDirectory(file, projectFiles);
                }
            }
        }

        private void processSourceDirectory(File file, List<File> projectFiles) {
            if (file.isFile()) {
                projectFiles.add(file);
            } else if (file.isDirectory()) {
                for (File childFile : Objects.requireNonNull(file.listFiles())) {
                    processSourceDirectory(childFile, projectFiles);
                }
            }
        }
    }

    private static CPPSPL loadProject(String[] files) {
        ARGS args = new ARGS();
        args.argParse(files);

        CPPSPL project = new CPPSPL(args.name);
        project.setFeatureModel(args.fm);

        for (File file : args.projectFiles) {
            project.addSourceFile(file);
        }

        return project;
    }

    public static void main(String[] args) {
        CPPSPL project = loadProject(args);

        FeatureTrace trace = project.processSourceFiles();
        //trace.cascadeFormulas();
        System.out.println("=========== SPL =========================================");
        trace.prettyPrint(System.out);
        System.out.println("CNF: " + trace.isConjunctiveNormalForm());

        int numVariants = 1;
        IFeatureModel fm = project.getFeatureModel();
        if (fm != null) {
            RandomConfigurationGenerator configurationGenerator =
                    new RandomConfigurationGenerator(
                            new SatInstance(fm.getAnalyser().getCnf()),//, FeatureModelUtils.getFeatureNames(fm)),
                            numVariants
            );
            try {
                configurationGenerator.execute(new NullMonitor());
            } catch (Exception e) {
                e.printStackTrace();
            }
            List<List<String>> configs = configurationGenerator.getConfigurations();

            for (List<String> selectedFeatures : configs) {
                Configuration config = ConfigurationUtils.toConfiguration(fm, selectedFeatures);
                FeatureTrace variant = trace.toVariant(config);

                System.out.println("Variant " + selectedFeatures + " =============================");
                variant.prettyPrint(System.out);
                System.out.println();
            }
        }

        System.out.println("\n--- Done -------------------------------");
    }
}
