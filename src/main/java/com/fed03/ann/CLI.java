package com.fed03.ann;

import corpus_texmex_reader.FvecReader;
import corpus_texmex_reader.TexMexVector;
import org.apache.commons.cli.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CLI {
    public static void main(String[] args) {
        Options options = generateCommonOptions();
        CommandLine commandLine = generateCommandLine(options, args);

        if (commandLine.hasOption("parametrized")) {
            startParametrized(args);
        } else {
            startNormal(args);
        }
    }

    private static void startNormal(String[] args) {
        Options options = generateNormalOptions();
        CommandLine commandLine = generateCommandLine(options, args);

        List<TexMexVector> dataset = getDataset(commandLine);
        final LSH lsh = new LSH(Integer.parseInt(commandLine.getOptionValue("k")), Integer.parseInt(commandLine.getOptionValue("L")), dataset);

        Index index = lsh.buildIndex();
        Map<TexMexVector, List<TexMexVector>> results = index.query(getQueries(commandLine), 10);
    }

    private static void startParametrized(String[] args) {
        Options options = generateParametrizedOptions();
        CommandLine commandLine = generateCommandLine(options, args);

        List<TexMexVector> dataset = getDataset(commandLine);
        final LSH lsh = new LSH(Double.parseDouble(commandLine.getOptionValue("d")), Double.parseDouble(commandLine.getOptionValue("e")), Double.parseDouble(commandLine.getOptionValue("w")), dataset);

        Index index = lsh.buildIndex();
        Map<TexMexVector, List<TexMexVector>> results = index.query(getQueries(commandLine), 10);
    }

    private static List<TexMexVector> getDataset(CommandLine commandLine) {
        List<TexMexVector> dataset = new ArrayList<>();
        try (FvecReader reader = new FvecReader(commandLine.getOptionValue("dataset"))) {
            if (commandLine.hasOption("vector-load-number")) {
                dataset = reader.getNextVectors(Integer.parseInt(commandLine.getOptionValue("vector-load-number")));
            } else {
                dataset = reader.getAllVectors();
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        return dataset;
    }

    private static List<TexMexVector> getQueries(CommandLine commandLine) {
        List<TexMexVector> queries = new ArrayList<>();
        try (FvecReader reader = new FvecReader(commandLine.getOptionValue("queries"))) {
            if (commandLine.hasOption("query-load-number")) {
                queries = reader.getNextVectors(Integer.parseInt(commandLine.getOptionValue("query-load-number")));
            } else {
                queries = reader.getAllVectors();
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        return queries;
    }

    private static CommandLine generateCommandLine(final Options options, final String[] args) {
        final CommandLineParser cmdLineParser = new DefaultParser();
        CommandLine commandLine = null;

        try {
            commandLine = cmdLineParser.parse(options, args, true);
        } catch (MissingOptionException e) {
            String missingOptions = String.join(", ", e.getMissingOptions());
            printHelp(options, "Missing required options: " + missingOptions);
            System.exit(1);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return commandLine;
    }

    private static void printHelp(Options options, String header) {
        HelpFormatter helpFormatter = new HelpFormatter();
        helpFormatter.printHelp("ann", header, options, null, true);
    }

    private static Options generateCommonOptions() {
        final Option parametrized = Option.builder("p")
                .desc("Switch to parametrized mode.")
                .longOpt("parametrized")
                .build();

        final Option datasetPath = Option.builder()
                .longOpt("dataset")
                .argName("DATASET_PATH")
                .required(true)
                .desc("The dataset file in .fvecs format")
                .hasArg()
                .build();

        final Option queryPath = Option.builder()
                .longOpt("queries")
                .argName("QUERIES_PATH")
                .required(true)
                .desc("The queries file in .fvecs format")
                .hasArg()
                .build();

        final Option vecLoadNumber = Option.builder("vl")
                .longOpt("vector-load-number")
                .required(false)
                .desc("How many vectors to load from dataset")
                .hasArg()
                .type(Integer.TYPE)
                .build();

        final Option queryLoadNumber = Option.builder("ql")
                .longOpt("query-load-number")
                .required(false)
                .desc("How many query vectors to load")
                .hasArg()
                .type(Integer.TYPE)
                .build();

        final Options options = new Options();
        options.addOption(parametrized);
        options.addOption(datasetPath);
        options.addOption(vecLoadNumber);
        options.addOption(vecLoadNumber);
        options.addOption(queryLoadNumber);
        options.addOption(queryPath);
        return options;
    }

    private static Options generateNormalOptions() {
        final Option k = Option.builder("k")
                .desc("The number of hash functions used to project the dataset.")
                .hasArg()
                .type(Integer.TYPE)
                .required(true)
                .build();

        final Option L = Option.builder("L")
                .desc("The number of hash tables.")
                .hasArg()
                .type(Integer.TYPE)
                .required(true)
                .build();

        final Options options = generateCommonOptions();
        options.addOption(k);
        options.addOption(L);
        return options;
    }

    private static Options generateParametrizedOptions() {
        final Option w = Option.builder("w")
                .desc("It's the projection radius. It quantizes the projection in hash buckets,\n" +
                        "therefore the number of points in every bucket depend on this param.")
                .hasArg()
                .type(Double.TYPE)
                .required(true)
                .build();

        final Option eps = Option.builder("e")
                .longOpt("epsilon")
                .desc("The approximation error, where vectors within R(1+eps) are likely to be returned.\n" +
                        "Must be positive")
                .hasArg()
                .type(Double.TYPE)
                .required(true)
                .build();

        final Option delta = Option.builder("d")
                .longOpt("delta")
                .desc("(1-delta) is the probability that at least one of the L projections\n" +
                        "produces a collision between the query and the true nearest neighbor.\n" +
                        "It is used to calc the number of hash table L.\n" +
                        "0.10 is a good choice.")
                .hasArg()
                .type(Double.TYPE)
                .required(true)
                .build();

        final Options options = generateCommonOptions();
        options.addOption(w);
        options.addOption(eps);
        options.addOption(delta);
        return options;
    }
}
