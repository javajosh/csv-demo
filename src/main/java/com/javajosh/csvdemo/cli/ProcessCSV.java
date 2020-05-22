package com.javajosh.csvdemo.cli;

import de.siegmar.fastcsv.reader.CsvContainer;
import de.siegmar.fastcsv.reader.CsvReader;
import de.siegmar.fastcsv.reader.CsvRow;
import io.dropwizard.cli.Command;
import io.dropwizard.setup.Bootstrap;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparser;

import java.io.File;
import java.nio.charset.StandardCharsets;

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
        String inputFile = namespace.getString("file");
        String outputDir = namespace.getString("out");
        System.out.println("file: " + inputFile + " out dir: " + outputDir);

        File file = new File(
                inputFile);
        CsvReader csvReader = new CsvReader();
        csvReader.setContainsHeader(true);

        CsvContainer csv = csvReader.read(file, StandardCharsets.UTF_8);
        for (CsvRow row : csv.getRows()) {
            System.out.println("First column of line: " + row.getField("lastName"));
        }
    }
}
