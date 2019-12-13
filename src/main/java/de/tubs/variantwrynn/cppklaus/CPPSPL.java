package de.tubs.variantwrynn.cppklaus;

import antlr.cpp.CPPLexer;
import antlr.cpp.CPPParser;
import de.ovgu.featureide.fm.core.base.IFeatureModel;
import de.ovgu.featureide.fm.core.base.impl.FeatureModel;
import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.CommonTokenStream;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

public class CPPSPL {
    private String name;
    private List<File> sourceFiles;
    private FeatureTrace featureTrace;
    private IFeatureModel featureModel;

    public CPPSPL(String name) {
        this.name = name;
        this.sourceFiles = new ArrayList<>();
    }

    public boolean addSourceFile(File file) {
        System.out.println("[CPPSPL.addSourceFile] " + file);
        return sourceFiles.add(file);
    }

    public FeatureTrace processSourceFiles() {
        FeatureTraceParser cpp2mh = new FeatureTraceParser();
        FeatureTrace root = new FeatureTrace();

        for (File srcFile : sourceFiles) {
            FeatureTrace scrFileHierarchy = new FeatureTrace();
            scrFileHierarchy.addArtefact(new CPPSPLCodeFragment(srcFile.getPath(), -1));
            root.addChild(scrFileHierarchy);

            cpp2mh.reset(scrFileHierarchy);

            ANTLRFileStream antlrInput = null;
            try {
                antlrInput = new ANTLRFileStream(srcFile.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
            }

            CPPLexer l = new CPPLexer(antlrInput);
            CPPParser p = new CPPParser(new CommonTokenStream(l));
            cpp2mh.visitDocument(p.document());
        }

        return featureTrace = root;
    }

    public FeatureTrace getFeatureTrace() {
        return featureTrace;
    }

    public void setFeatureModel(IFeatureModel fm) {
        this.featureModel = fm;
    }

    public IFeatureModel getFeatureModel() {
        return this.featureModel;
    }
}
