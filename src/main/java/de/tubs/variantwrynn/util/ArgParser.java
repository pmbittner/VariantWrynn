package de.tubs.variantwrynn.util;

import java.util.*;
import java.util.function.Consumer;

public class ArgParser {
    public static class Argument {
        public final static int Flag = 0;
        public final static int AnyNumberOfParameters = -1;
        public final static boolean IsOptional = false;
        public final static boolean IsMandatory = true;

        protected boolean mandatory;
        protected String name;
        protected int len;
        protected Consumer<Void> definedCallback;
        protected Consumer<String> parameterCallback;

        /**
         *
         * @param name
         * @param len Number of additional parameters (0: Argument is a plain flag; -1: arbitrary number of parameters)
         */
        public Argument(String name, int len, boolean isMandatory, Consumer<Void> definedCallback, Consumer<String> parameterCallback) {
            if (name == null || name.isEmpty()) {
                throw new IllegalArgumentException("name can neither be null nor empty! was: " + name);
            }

            if (name.length() == 1 && len != 0) {
                throw new IllegalArgumentException("Flags cannot have parameters! Flag \"" + name + "\" has " + len + " parameters but should have 0!");
            }

            this.name = name;
            this.len = len;
            this.mandatory = isMandatory;
            this.definedCallback = definedCallback;
            this.parameterCallback = parameterCallback;
        }

        public Argument(char flag, Consumer<Void> definedCallback) {
            this("" + flag, Flag, IsOptional, definedCallback, null);
        }
    }

    private Map<String, Argument> arguments;

    public ArgParser(Argument... args) {
        arguments = new HashMap<>();

        for (Argument arg : args) {
            String argumentName;

            if (arg.name.length() == 1) {
                argumentName = "-";
            } else {
                argumentName = "--";
            }

            argumentName += arg.name;

            if (arguments.containsKey(argumentName)) {
                throw new IllegalArgumentException("Duplicate argument specification: Argument \"" + argumentName + "\" already exists!");
            } else {
                arguments.put(argumentName, arg);
            }
        }
    }

    public void parse(String[] args) {
        Argument currentArgument = null;
        List<Argument> handledArgs = new ArrayList<>();
        List<String> handledStrargs = new ArrayList<>();

        for (String strarg : args) {
            if (strarg.startsWith("-")) {
                Argument arg = arguments.get(strarg);

                if (arg != null) {
                    if (handledStrargs.contains(strarg)) {
                        throw new IllegalArgumentException("Duplicate specification of rgument \"" + strarg + "\"!");
                    }

                    currentArgument = arg;
                    handledStrargs.add(strarg);
                    handledArgs.add(currentArgument);

                    if (currentArgument.definedCallback != null) {
                        currentArgument.definedCallback.accept(null);
                    }
                } else {
                    StringBuilder errorMsg = new StringBuilder("Unknown argument \"" + strarg + "\" given!\nAvailable arguments are:");

                    for (Map.Entry<String, Argument> availableArg : arguments.entrySet()) {
                        errorMsg.append("\n").append(availableArg.getKey());
                    }

                    throw new IllegalArgumentException(errorMsg.toString());
                }
            } else {
                if (currentArgument != null && currentArgument.parameterCallback != null) {
                    currentArgument.parameterCallback.accept(strarg);
                } else {
                    throw new IllegalArgumentException("Unknown parameter \"" + strarg + "\" given! An argument (beginning with \"-\" or \"--\") has to be specified first");
                }
            }
        }

        for (Map.Entry<String, Argument> kv : this.arguments.entrySet()) {
            Argument arg = kv.getValue();
            if (arg.mandatory && !handledArgs.contains(arg)) {
                throw new IllegalArgumentException("Argument \"" + arg.name + "\" is mandatory but not specified!");
            }
        }
    }
}
