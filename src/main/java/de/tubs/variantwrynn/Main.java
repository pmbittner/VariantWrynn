package de.tubs.variantwrynn;

import de.ovgu.featureide.fm.core.base.IFeatureModel;
import de.tubs.variantwrynn.core.model.Artefact;
import de.tubs.variantwrynn.core.simpleimpl.ListVariant;
import de.tubs.variantwrynn.core.simpleimpl.OrthogonalStringArtefact;
import de.tubs.variantwrynn.core.simpleimpl.SimpleVariantSyncProject;
import de.tubs.variantwrynn.core.synthesis.quinemccluskey.QuineMcCluskey;
import de.tubs.variantwrynn.util.Bits;
import de.tubs.variantwrynn.util.fide.IO;
import de.tubs.variantwrynn.util.fide.NodeUtils;
import org.prop4j.And;
import org.prop4j.Node;

import java.util.*;
import java.util.function.BiConsumer;

public class Main {
    private static String DefaultResourceDirectory = "src/main/resources";

    /**
     * Represents artefacts with a feature mapping formula.
     * We use this to represent ground truth data for tests and evaluation.
     */
    private static class MappedArtefact {
        Node mapping;
        Artefact artefact;

        MappedArtefact(Node mapping, Artefact artefact) {
            this.mapping = mapping;
            this.artefact = artefact;
        }
    }

    private static SimpleVariantSyncProject createTestScenario() {
        final int numVariants = 5;

        final String fName_Assignment = "Assignment";
        final String fName_Print = "Print";
        final String fName_Mem = "Mem";
        final String fName_Constexpr = "Constexpr";
        final String fName_Comment = "Comment";

        String fmFile = DefaultResourceDirectory + "/fm/DumbPL.xml";
        IFeatureModel globalFM = IO.loadFile(fmFile);

        List<MappedArtefact> A = new ArrayList<>();
        {
            BiConsumer<Node, String> addArtefact = (Node m, String a) -> A.add(new MappedArtefact(m, new OrthogonalStringArtefact(a)));

            addArtefact.accept(NodeUtils.reference(fName_Comment), "// This is the coolest variant!");
            addArtefact.accept(NodeUtils.reference(fName_Print), "print(\"Moin\");");
            addArtefact.accept(NodeUtils.reference(fName_Mem),"delete smart_ptr_pointer;");
            addArtefact.accept(NodeUtils.reference(fName_Assignment), "these.examples.are.too.detailed = \"lol\"");
            addArtefact.accept(new And(NodeUtils.reference(fName_Mem), NodeUtils.reference(fName_Assignment)), "void* mem = malloc(1 << 40);");
            addArtefact.accept(new And(NodeUtils.reference(fName_Constexpr), NodeUtils.reference(fName_Assignment)),"constexpr inline int add(int a, int b) { constexpr int sum = a + b; return sum; }");
            addArtefact.accept(new And(NodeUtils.reference(fName_Print), NodeUtils.reference(fName_Constexpr)),"constexpr int i = add(4, 2); print(\"I am active on constexpr \" + i);");
            addArtefact.accept(new And(NodeUtils.reference(fName_Print), NodeUtils.negate(NodeUtils.reference(fName_Constexpr))), "int i = add(4, 2); print(\"I am active on not constexpr \" + i);");
        }

        List<ListVariant> variants = new ArrayList<>(numVariants);
        {
            for (int i = 0; i < numVariants; ++i) {
                variants.add(new ListVariant(globalFM));
            }

            // Set Configurations
            // Comment is mandatory and therefore already set automatically.
            variants.get(0).select(fName_Print);

            variants.get(1).select(fName_Print);
            variants.get(1).select(fName_Constexpr);

            variants.get(2).select(fName_Assignment);
            variants.get(2).select(fName_Print);
            variants.get(2).select(fName_Constexpr);

            variants.get(3).select(fName_Assignment);
            variants.get(3).select(fName_Mem);

            variants.get(4).select(fName_Mem);

            // Add each artefact to all variants whose confguration satisfies the artefacts mapping.
            for (MappedArtefact ma : A) {
                for (ListVariant v : variants) {
                    if (v.configurationSatisfies(ma.mapping)) {
                        v.add(ma.artefact);
                    }
                }
            }
        }

        return new SimpleVariantSyncProject(globalFM, variants);
    }

    private static void QuineMcCluskeyTest() {
        QuineMcCluskey qmc = new QuineMcCluskey();
        List<Bits> satisfyingAssignments =
                Bits.fromDecimals(4,1, 3, 6, 11, 13);
        List<Bits> dontcareAssignments =
                Bits.fromDecimals(4,2, 7, 15);

        /*
        System.out.println("Satisfying Assignments:");
        for (Bits b : satisfyingAssignments) {
            System.out.println("  " + b);
        }

        //*
        System.out.println("Don't-Care Assignments:");
        for (Bits b : dontcareAssignments) {
            System.out.println("  " + b);
        }//*/

        //*
        for (Node recommendation : qmc.synthesise(satisfyingAssignments, null, dontcareAssignments)) {
            System.out.println("  " + recommendation);
        }//*/
    }

    private static void BitsTest() {
        final int numBits = 9;
        final int inDecimal = 1 + 2 + 4 + 128 + 256;

        Bits b = new Bits(numBits, inDecimal);
        System.out.println("Dec = " + inDecimal);
        System.out.println("Bin = " + b);
        System.out.println("    = " + b.toBigInt());
        System.out.println("Crd = " + b.cardinality());

        Bits other = new Bits(numBits, 1 + 8);
        System.out.println("\n  " + b);
        System.out.println(  "& " + other);
        System.out.println(  "-----------");
        System.out.println(  "= " + b.and(other));

        System.out.println("\n  " + b);
        System.out.println(  "| " + other);
        System.out.println(  "-----------");
        System.out.println(  "= " + b.or(other));

        System.out.println("\n  " + b);
        System.out.println(  "& " + other);
        System.out.println(  "-----------");
        System.out.println(  "^ " + b.xor(other));

        int setBit = 4;
        System.out.print("\nSet bit " + setBit + ": ");
        b.setBitTo(setBit, true);
        System.out.println(b);
        System.out.print("Clr bit " + setBit + ": ");
        b.setBitTo(setBit, false);
        System.out.println(b);
    }

    public static void main(String[] args) {
//*
        System.out.println("\n========== Quine-McCluskey Algorithm Test ==============================================\n");
        QuineMcCluskeyTest();
/*/
        SimpleVariantSyncProject vs = createTestScenario();

        vs.print();

        System.out.println("\n========== Recommendations =============================================================\n");

        /// And now derive the feature mappings
        VariantWrynn variantWrynn = new VariantWrynn(vs);

        /// 1.) Get all artefacts A
        /// So far we only consider exact equality (similarity = 1). Hence, represent A as a set to avoid duplicates.
        Set<Artefact> A = new HashSet<>();
        for (ListVariant v : vs.getVariants()) {
            A.addAll(v.getArtefacts());
        }

        /// 2.) Generate recommendations for each artefact
        for (Artefact a : A) {
            System.out.println("VariantWrynn[\"" + a + "\"] recommends:");

            for (Node recommendation : variantWrynn.recommendFeatureMappingFor(a)) {
                System.out.println("  " + recommendation);
            }
        }
//*/
    }
}
