package de.tubs.variantwrynn.cppklaus;

import de.ovgu.featureide.fm.core.base.IFeatureModel;
import de.ovgu.featureide.fm.core.configuration.Configuration;
import de.ovgu.featureide.fm.core.job.monitor.NullMonitor;
import de.tubs.variantwrynn.util.ArgParser;
import de.tubs.variantwrynn.util.FileUtils;
import de.tubs.variantwrynn.util.fide.ConfigurationUtils;
import de.tubs.variantwrynn.util.fide.IO;
import org.prop4j.analyses.RandomConfigurationGenerator;
import org.prop4j.solver.SatInstance;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class VariantGenerator {
    public static boolean PRINTWITHMAPPINGS = false;

    private static void addAllFilesInDirectory(File file, final List<CPPFile> projectFiles, final File relativeDir) {
        if (file.isFile()) {
            projectFiles.add(new CPPFile(file, relativeDir));
        } else if (file.isDirectory()) {
            for (File childFile : Objects.requireNonNull(file.listFiles())) {
                addAllFilesInDirectory(childFile, projectFiles, relativeDir);
            }
        }
    }

    public static void main(String[] args) {
        ArgParser argParser;
        CPPSPL project;
        AtomicInteger numberOfVariantsToGenerate = new AtomicInteger(0);
        File outputDirectory;

        {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            AtomicReference<String> name = new AtomicReference<>();
            AtomicReference<IFeatureModel> fm = new AtomicReference<>();
            List<CPPFile> projectFiles = new ArrayList<>();
            AtomicReference<String> outputDirectoryStr = new AtomicReference<>();

            argParser = new ArgParser(
                    new ArgParser.Argument('m', v -> PRINTWITHMAPPINGS = true),
                    new ArgParser.Argument("name", 1, ArgParser.Argument.IsOptional, null, name::set),
                    new ArgParser.Argument("fm", 1, ArgParser.Argument.IsOptional, null,
                            s -> fm.set(IO.loadFeatureModel(s))),
                    new ArgParser.Argument("resfm", 1, ArgParser.Argument.IsOptional, null,
                            s -> fm.set(IO.loadFeatureModel(FileUtils.toFile(Objects.requireNonNull(classLoader.getResource(s))).getAbsolutePath()))),
                    new ArgParser.Argument("src", ArgParser.Argument.AnyNumberOfParameters, ArgParser.Argument.IsOptional, null,
                            s -> addAllFilesInDirectory(new File(s), projectFiles, FileUtils.getDirectory(new File(s)))),
                    new ArgParser.Argument("ressrc", ArgParser.Argument.AnyNumberOfParameters, ArgParser.Argument.IsOptional, null,
                            s -> {
                                File file = FileUtils.toFile(Objects.requireNonNull(classLoader.getResource(s)));
                                addAllFilesInDirectory(file, projectFiles, FileUtils.getDirectory(file));
                            }),
                    new ArgParser.Argument("numVariants", 1, ArgParser.Argument.IsMandatory, null,
                            s -> numberOfVariantsToGenerate.set(Integer.parseInt(s))),
                    new ArgParser.Argument("outputDir", 1, ArgParser.Argument.IsMandatory, null, outputDirectoryStr::set)
            );
            argParser.parse(args);

            outputDirectory = FileUtils.getOrCreateDir(outputDirectoryStr.get());
            if (!outputDirectory.isAbsolute()) {
                String workingDirectory = System.getProperty("user.dir");
                outputDirectory = new File(workingDirectory, outputDirectoryStr.get());
            }

            if (!outputDirectory.isDirectory()) {
                throw new IllegalArgumentException("Given output directory \"" + outputDirectory + "\" is not a directory!");
            }

            project = new CPPSPL(name.get(), fm.get(), projectFiles);
        }

        project.processSourceFiles();

        IFeatureModel featuremodel = project.getFeatureModel();
        if (featuremodel != null) {
            RandomConfigurationGenerator configurationGenerator =
                    new RandomConfigurationGenerator(
                            new SatInstance(featuremodel.getAnalyser().getCnf()),
                            numberOfVariantsToGenerate.get()
            );

            try {
                configurationGenerator.execute(new NullMonitor());
            } catch (Exception e) {
                e.printStackTrace();
            }

            System.out.println("[VariantGenerator.main] Resetting directory \"" + outputDirectory + "\"!");
            FileUtils.deleteDirectory(outputDirectory, "genvariants");

            List<List<String>> configs = configurationGenerator.getConfigurations();
            for (List<String> selectedFeatures : configs) {
                Configuration config = ConfigurationUtils.toConfiguration(featuremodel, selectedFeatures);
                CPPVariant variant = project.toVariant(config);
                variant.storeAt(outputDirectory);
            }
        }

        System.out.println("\n--- Done -------------------------------");
    }
}
