package com.javajosh.csvdemo;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class CSVDemoApplication extends Application<CSVDemoConfiguration> {

    public static void main(final String[] args) throws Exception {
        new CSVDemoApplication().run(args);
    }

    @Override
    public String getName() {
        return "CSVDemo";
    }

    @Override
    public void initialize(final Bootstrap<CSVDemoConfiguration> bootstrap) {
        // TODO: application initialization
    }

    @Override
    public void run(final CSVDemoConfiguration configuration,
                    final Environment environment) {
        // TODO: implement application
    }

}
