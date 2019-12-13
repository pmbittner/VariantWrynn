package de.tubs.variantwrynn.cppklaus;

import de.tubs.variantwrynn.util.FileUtils;

import java.net.URL;

public class Main {
    class ARGS {
        public final static int FeatureModelPath = 0;
        public final static int ProjectName = 0;
    }

    private static CPPSPL loadProject(String[] files) {
        CPPSPL project = new CPPSPL(files[ARGS.ProjectName]);

        for (int i = ARGS.ProjectName + 1; i < files.length; ++i) {
            String file = files[i];
            ClassLoader classloader = Thread.currentThread().getContextClassLoader();
            URL exampleRes = classloader.getResource(file);
            if (exampleRes == null)
                throw new NullPointerException("Resource \"" + file + "\" not found!");

            project.addSourceFile(FileUtils.toFile(exampleRes));
        }

        return project;
    }

    //private static FeatureModel loadFeatureModel(String[] args) { }

    public static void main(String[] args) {
        CPPSPL project;
        project = loadProject(args);

        FeatureTrace trace = project.processSourceFiles();
        trace.cascadeFormulas();
        trace.prettyPrint(System.out);
        System.out.println("CNF: " + trace.isConjunctiveNormalForm());

        System.out.println("\n--- Done -------------------------------");
    }
}
