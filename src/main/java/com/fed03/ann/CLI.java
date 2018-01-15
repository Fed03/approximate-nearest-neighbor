package com.fed03.ann;

import com.fed03.corpus_texmex_reader.FvecReader;
import com.fed03.corpus_texmex_reader.IvecReader;
import org.apache.commons.cli.*;
import org.apache.commons.math3.linear.ArrayRealVector;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CLI {
    public static void main(String[] args) {
        Options options = generateOptions();
        CommandLine commandLine = generateCommandLine(options, args);

        List<ArrayRealVector> dataset = getDataset(commandLine);
        final Index index = buildIndex(commandLine, dataset);


        ArrayRealVector query = dataset.get(0);
        try (FvecReader reader = new FvecReader("C:\\Users\\templ\\Code\\ann\\src\\main\\resources\\dataset\\siftsmall\\siftsmall_query.fvecs")) {
            query = reader.getNextVector();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        final Map<ArrayRealVector, Double> result = index.query(query, 10);

        final int[] queryIdxs;
        try (IvecReader reader = new IvecReader("C:\\Users\\templ\\Code\\ann\\src\\main\\resources\\dataset\\siftsmall\\siftsmall_groundtruth.ivecs")) {
            queryIdxs = reader.nextGroundTruthIndexes();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        System.out.println("l");
    }

    private static List<ArrayRealVector> getDataset(CommandLine commandLine) {
        List<ArrayRealVector> dataset = new ArrayList<>();
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

    private static Index buildIndex(CommandLine commandLine, List<ArrayRealVector> dataset) {
        final LSH lsh = new LSH(Double.parseDouble(commandLine.getOptionValue("d")), Double.parseDouble(commandLine.getOptionValue("e")), Double.parseDouble(commandLine.getOptionValue("w")), dataset);
        return lsh.buildIndex();
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

        final Option vecLoadNumber = Option.builder("vl")
                .longOpt("vector-load-number")
                .required(false)
                .desc("How many vectors to load from dataset")
                .hasArg()
                .type(Integer.TYPE)
                .build();

        final Options options = new Options();
        options.addOption(w);
        options.addOption(eps);
        options.addOption(delta);
        options.addOption(datasetPath);
        options.addOption(vecLoadNumber);
        return options;
    }
}
