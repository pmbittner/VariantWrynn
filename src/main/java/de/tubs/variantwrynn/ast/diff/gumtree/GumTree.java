package de.tubs.variantwrynn.ast.diff.gumtree;

import com.github.gumtreediff.actions.ActionGenerator;
import com.github.gumtreediff.actions.model.Action;
import com.github.gumtreediff.client.Run;
import com.github.gumtreediff.gen.Generators;
import com.github.gumtreediff.matchers.Matcher;
import com.github.gumtreediff.matchers.Matchers;
import com.github.gumtreediff.tree.ITree;
import de.tubs.variantwrynn.util.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class GumTree {
    public static void main(String[] args) {
        File inPath = new File(FileUtils.getWorkingDirectory(), "genvariants/marlin/Variant1/");

        String file1 = new File(inPath, "linsbauer0.cpp").getAbsolutePath();
        String file2 = new File(inPath, "linsbauer1.cpp").getAbsolutePath();

        //String file1 = new File(inPath, "mops.java").getAbsolutePath();
        //String file2 = new File(inPath, "mops.h").getAbsolutePath();

        Run.initGenerators();

        ITree src;
        ITree dst;
        try {
            src = Generators.getInstance().getTree(file1).getRoot();
            dst = Generators.getInstance().getTree(file2).getRoot();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        Matcher m = Matchers.getInstance().getMatcher(src, dst); // retrieve the default matcher
        m.match();
        ActionGenerator g = new ActionGenerator(src, dst, m.getMappings());
        g.generate();
        List<Action> actions = g.getActions(); // return the actions

        System.out.println(src.toTreeString());
        System.out.println();
        System.out.println(actions);
        System.out.println();
        System.out.println(dst.toTreeString());

        System.out.println();
        System.out.println();

        System.out.println("Actions:");
        for (Action a : actions) {

        }
    }
}
