package de.tubs.variantwrynn.diff.gumtree;

import com.github.gumtreediff.actions.ActionGenerator;
import com.github.gumtreediff.actions.model.Action;
import com.github.gumtreediff.client.Run;
import com.github.gumtreediff.gen.Generators;
import com.github.gumtreediff.matchers.Matcher;
import com.github.gumtreediff.matchers.Matchers;
import com.github.gumtreediff.tree.ITree;
import de.tubs.variantwrynn.util.FileUtils;

import java.io.File;
import java.util.List;

public class GumTree {
    static void main(String[] args) {
        File inPath = new File(FileUtils.getWorkingDirectory(), "genvariants/marlin");
        String file2 = "file_v1.java";

        /*
        Run.initGenerators();
        ITree src = Generators.getInstance().getTree(file1).getRoot();
        ITree dst = Generators.getInstance().getTree(file2).getRoot();
        Matcher m = Matchers.getInstance().getMatcher(src, dst); // retrieve the default matcher
        m.match();
        ActionGenerator g = new ActionGenerator(src, dst, m.getMappings());
        g.generate();
        List<Action> actions = g.getActions(); // return the actions
        //*/
    }
}
