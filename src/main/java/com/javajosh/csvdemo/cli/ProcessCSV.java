package com.javajosh.csvdemo.cli;

import de.siegmar.fastcsv.reader.CsvContainer;
import de.siegmar.fastcsv.reader.CsvReader;
import de.siegmar.fastcsv.reader.CsvRow;
import de.siegmar.fastcsv.writer.CsvWriter;
import io.dropwizard.cli.Command;
import io.dropwizard.setup.Bootstrap;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparser;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;

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
        for (CsvRow row : csv.getRows()) {
            System.out.println("First column of line: " + row.getField(0));
        }

        // Write it out
        File outputFile = new File(outputDir + "/foo.csv");
        CsvWriter csvWriter = new CsvWriter();
        Collection<String[]> data = new ArrayList<>();
        data.add(new String[] { "header1", "header2" });
        data.add(new String[] { "value1", "value2" });
        csvWriter.write(outputFile, StandardCharsets.UTF_8, data);
    }
}
