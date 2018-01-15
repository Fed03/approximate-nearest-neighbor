package com.fed03.ann;

import org.apache.commons.cli.*;

public class CLI {
    public static void main(String[] args) {
        Options options = generateOptions();
        CommandLine commandLine = generateCommandLine(options, args);

    }

    private static CommandLine generateCommandLine(final Options options, final String[] args) {
        final CommandLineParser cmdLineParser = new DefaultParser();
        CommandLine commandLine = null;

        try {
            commandLine = cmdLineParser.parse(options, args);
        } catch (MissingOptionException e) {
            String missingOptions = String.join(", ", e.getMissingOptions());
            printHelp(options, "Missing required options: " + missingOptions);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return commandLine;
    }

    private static void printHelp(Options options, String header) {
        HelpFormatter helpFormatter = new HelpFormatter();
        helpFormatter.printHelp("ann", header, options, null, true);
    }

    private static Options generateOptions() {
        final Option w = Option.builder("w")
                .required(true)
                .desc("It's the projection radius. It quantizes the projection in hash buckets,\n" +
                        "therefore the number of points in every bucket depend on this param.")
                .hasArg()
                .type(Double.TYPE)
                .build();

        final Option eps = Option.builder("e")
                .longOpt("epsilon")
                .required(true)
                .desc("The approximation error, where vectors within R(1+eps) are likely to be returned.\n" +
                        "Must be positive")
                .hasArg()
                .type(Double.TYPE)
                .build();

        final Option delta = Option.builder("d")
                .longOpt("delta")
                .required(true)
                .desc("(1-delta) is the probability that at least one of the L projections\n" +
                        "produces a collision between the query and the true nearest neighbor.\n" +
                        "It is used to calc the number of hash table L.\n" +
                        "0.10 is a good choice.")
                .hasArg()
                .type(Double.TYPE)
                .build();

        final Option datasetPath = Option.builder()
                .longOpt("dataset")
                .argName("DATASET_PATH")
                .required(true)
                .desc("The dataset file in .fvecs format")
                .hasArg()
                .build();

        final Options options = new Options();
        options.addOption(w);
        options.addOption(eps);
        options.addOption(delta);
        options.addOption(datasetPath);
        return options;
    }
}
