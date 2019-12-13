package de.tubs.variantwrynn.cppklaus;

import antlr.cpp.CPPLexer;
import antlr.cpp.CPPParser;
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
    private FeatureModel featureModel;

    public CPPSPL(String name) {
        this.name = name;
        this.sourceFiles = new ArrayList<>();
    }

    public boolean addSourceFile(File file) {
        return sourceFiles.add(file);
    }

    public FeatureTrace processSourceFiles() {
        CPP2MHParser cpp2mh = new CPP2MHParser();
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
}
