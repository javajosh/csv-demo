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
import java.util.*;

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

    enum FIELDS {userId, lastName, firstName, version, company};

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
        // Ugh this is ugly. TODO: use guava's multimap instead
        Map<String, Map<String, CsvRow>> outputFiles = new HashMap();
        Map<String, CsvRow> partition;
        CsvRow foundRow;
        for (CsvRow row : csv.getRows()) {
            String id = row.getField(0).trim();
//          String lastName = row.getField(1).trim();
//          String firstName = row.getField(2).trim();
            int version = Integer.parseInt(row.getField(3).trim());
            String company = row.getField(4).trim();

            partition = outputFiles.get(company);
            if (partition == null){
                partition = new HashMap();
                partition.put(company, row);
                continue;
            }

            foundRow = partition.get(id);
            if (foundRow == null){
                partition.put(id, row);
            } else {
                int foundVersion = Integer.parseInt(foundRow.getField(3).trim());
                if (version > foundVersion){
                    partition.put(id, row);
                }
            }
        }
        System.out.println(outputFiles);

        //sort by name then version
//        List<CsvRow> allRows = csv.getRows();
//        allRows.sort((r1, r2) -> {
//            String name1 = r1.getField(1) + r1.getField(2);
//            String name2 = r2.getField(1) + r2.getField(2);
//            int v1 = Integer.parseInt(r1.getField(3));
//            int v2 = Integer.parseInt(r2.getField(3));
//            int nameComparison = name1.compareTo(name2);
//            return nameComparison == 0 ? v1 - v2 : nameComparison;
//        });
//
//        // prune
//        int lastId = Integer.MIN_VALUE;
//        for (CsvRow row : csv.getRows()) {
//            System.out.println("First column of line: " + row.getField(0));
//        }



        for (CsvRow row : csv.getRows()) {

            /// System.out.println("First column of line: " + row.getField(0));
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
