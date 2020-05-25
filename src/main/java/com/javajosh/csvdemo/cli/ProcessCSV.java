package com.javajosh.csvdemo.cli;

import de.siegmar.fastcsv.reader.CsvContainer;
import de.siegmar.fastcsv.reader.CsvReader;
import de.siegmar.fastcsv.reader.CsvRow;
import de.siegmar.fastcsv.writer.CsvWriter;
import io.dropwizard.cli.Command;
import io.dropwizard.setup.Bootstrap;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparser;
import org.slf4j.helpers.MessageFormatter;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

public class ProcessCSV extends Command {
    public ProcessCSV() {
        super("process", "Process a csv file");
    }

    @Override
    public void configure(Subparser subparser) {
        subparser.addArgument("-f", "--file")
                .dest("file")
                .type(String.class)
                .required(false)
                .help("The csv file to process");

        subparser.addArgument("-o", "--out")
                .dest("out")
                .type(String.class)
                .required(false)
                .help("The directory into which we write the output files.");
    }

    // The expected order of fields in the input.
    enum FIELDS {userId, lastName, firstName, version, company}

    @Override
    public void run(Bootstrap<?> bootstrap, Namespace namespace) throws Exception {
        String inputFileName = namespace.getString("file");
        String outputDir = namespace.getString("out");
        System.out.println("file: " + inputFileName + " out dir: " + outputDir);

        // Read it in
        File inputFile = new File(inputFileName);
        CsvReader csvReader = new CsvReader();
        csvReader.setContainsHeader(true);
        CsvContainer csv = csvReader.read(inputFile, StandardCharsets.UTF_8);

        // provider to unique ids.
        Map<String, Map<String, CsvRow>> outputFiles = new HashMap<>();
        Map<String, CsvRow> partition;
        CsvRow foundRow;
        for (CsvRow row : csv.getRows()) {
            String id =      row.getField(FIELDS.userId.ordinal()).trim();
            String version = row.getField(FIELDS.version.ordinal()).trim();
            String company = row.getField(FIELDS.company.ordinal()).trim();

            // Make a new company partition if it's not there
            partition = outputFiles.get(company);
            if (partition == null) {
                partition = new HashMap<>();
                outputFiles.put(company, partition);
                partition.put(id, row);
                continue;
            }

            // The company partition already exists, so if the id already exists, only write if the new id is larger
            foundRow = partition.get(id);
            if (foundRow == null) {
                partition.put(id, row);
            } else {
                int foundVersion = Integer.parseInt(foundRow.getField(FIELDS.version.ordinal()).trim());
                int thisVersion = Integer.parseInt(version.trim());
                if (thisVersion > foundVersion) {
                    partition.put(id, row);
                }
            }
        }

//        Do a sanity check to see if the data looks right
//        Joiner.MapJoiner mapJoiner = Joiner.on(",").withKeyValueSeparator("=");
//        System.out.println(mapJoiner.join(outputFiles));


        CsvWriter csvWriter = new CsvWriter();
        // For each partition, convert the Map<String, CsvRow> into a List<String[]>, sort the list, and write it output
        for (String company : outputFiles.keySet()) {
            List<String[]> myFinalList = outputFiles.get(company).values().stream()
                    .map(row -> row.getFields().toArray(new String[0]))
                    .sorted((r1, r2) -> {
                        String name1 = r1[1] + r1[2];
                        String name2 = r2[1] + r2[2];
                        return name1.compareTo(name2);
                    }).collect(Collectors.toList());

            // Write it out
            File outputFile = new File(MessageFormatter.format("{}/{}.csv", outputDir, company).getMessage());
            csvWriter.write(outputFile, StandardCharsets.UTF_8, myFinalList);
        }
    }
}
