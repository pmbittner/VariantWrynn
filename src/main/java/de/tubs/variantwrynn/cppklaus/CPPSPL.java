package de.tubs.variantwrynn.cppklaus;

import antlr.cpp.CPPLexer;
import antlr.cpp.CPPParser;
import de.ovgu.featureide.fm.core.base.IFeatureModel;
import de.ovgu.featureide.fm.core.configuration.Configuration;
import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.CommonTokenStream;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

public class CPPSPL {
    private String name;
    private List<CPPFile> sourceFiles;
    private IFeatureModel featureModel;
    private boolean finalised;

    public CPPSPL(String name, IFeatureModel featureModel, List<CPPFile> sourceFiles) {
        this.name = name;
        this.featureModel = featureModel;
        this.sourceFiles = sourceFiles;
        this.finalised = false;
    }

    public boolean addSourceFile(CPPFile file) {
        if (finalised) {
            System.out.println("[CPPSPL.addSourceFile] Cannot add source file \"" + file + "\" because the project is already finalised!");
            return false;
        }

        System.out.println("[CPPSPL.addSourceFile] " + file);
        return sourceFiles.add(file);
    }

    public void processSourceFiles() {
        CPPAnnotatedCodeParser cpp = new CPPAnnotatedCodeParser();

        for (CPPFile srcFile : sourceFiles) {
            FeatureAnnotation<String> annotation = new FeatureAnnotation<>();
            srcFile.setContent(annotation);
            cpp.reset(annotation);

            ANTLRFileStream antlrInput = null;
            try {
                antlrInput = new ANTLRFileStream(srcFile.getFile().getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
            }

            CPPLexer lexer = new CPPLexer(antlrInput);
            CPPParser parser = new CPPParser(new CommonTokenStream(lexer));
            cpp.visitDocument(parser.document());
        }

        finalised = true;
    }

    public void setFeatureModel(IFeatureModel fm) {
        this.featureModel = fm;
    }

    public IFeatureModel getFeatureModel() {
        return this.featureModel;
    }

    public CPPVariant toVariant(Configuration configuration) {
        List<CPPFile> code = new ArrayList<>(this.sourceFiles.size());

        for (CPPFile file : this.sourceFiles) {
            code.add(file.toVariant(configuration));
        }

        return new CPPVariant(code, configuration);
    }
}
