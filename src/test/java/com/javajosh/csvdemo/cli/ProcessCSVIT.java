package com.javajosh.csvdemo.cli;

import junit.framework.TestCase;

import java.io.File;

// IT stands for integration test
public class ProcessCSVIT extends TestCase {
    public void testExecute() throws Exception {
        String inputFile = "test/resources/fixtures/example.csv";
        String outputDir = "target";

        execute(new String[]{"-f", inputFile, "-o", outputDir});

        assertTrue(new File(outputDir + "/" + "aetna.csv").exists());
        assertTrue(new File(outputDir + "/" + "acme.csv").exists());
    }

    private int execute(String[] args)
            throws Exception {
        File jar = new File("target/csv-demo-1.0-SNAPSHOT.jar");

        String[] execArgs = new String[args.length + 3];
        System.arraycopy(args, 0, execArgs, 3, args.length);
        execArgs[0] = "java";
        execArgs[1] = "-jar";
        execArgs[2] = jar.getCanonicalPath();
        Process p = Runtime.getRuntime().exec(execArgs);
        p.waitFor();


        return p.exitValue();
    }
}
