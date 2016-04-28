package ru.lipetsk.camera.cmagent;

import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.management.ManagementFactory;

/**
 * Created by Ivan on 11.02.2016.
 */
public class Bootstrap {
    private final static Logger logger = LoggerFactory.getLogger(Bootstrap.class);

    public static void main(String[] args) {
        Options options = new Options();

        options.addOption("h", "help", false, "Help information");
        options.addOption(Option.builder().longOpt("id").hasArg(true).required(true).desc("Agent ID").build());
        options.addOption(Option.builder().longOpt("cfg-file").hasArg(true).required(true).desc("Path to agent configuration file").build());

        String configurationFile = null;

        try {
            CommandLineParser commandLineParser = new DefaultParser();

            CommandLine commandLine = commandLineParser.parse(options, args);

            if (commandLine.hasOption("h")) {
                HelpFormatter helpFormatter = new HelpFormatter();

                helpFormatter.printHelp("java -jar /path/to/cmagent.jar", options);

                System.exit(0);
            }

            System.setProperty("agent.id", commandLine.getOptionValue("id"));

            System.setProperty("agent.pid", ManagementFactory.getRuntimeMXBean().getName().split("[@]")[0]);

            configurationFile = commandLine.getOptionValue("cfg-file");
        } catch (ParseException e) {
            logger.error(e.getMessage());

            System.exit(-1);
        }

        ApplicationFacade.getInstance().bootstrap(configurationFile);

        //Publisher publisher = new Publisher();

        /*Options options = new Options();

        options.addOption(Option.builder().longOpt("fms-host").hasArg(true).required(true).desc("FMS Host").build());
        options.addOption(Option.builder().longOpt("fms-port").hasArg(true).required(true).desc("FMS Port").build());
        options.addOption(Option.builder().longOpt("fms-app").hasArg(true).required(true).desc("FMS App").build());
        options.addOption(Option.builder().longOpt("ip-address").hasArg(true).required(true).desc("IP address").build());
        options.addOption(Option.builder().longOpt("uri").hasArg(true).required(true).desc("URI").build());

        String fmsHost = null;

        int fmsPort = 0;

        String fmsApp = null;

        String ipAddress = null;

        String uri = null;

        try {
            CommandLineParser commandLineParser = new DefaultParser();

            CommandLine commandLine = commandLineParser.parse(options, args);

            fmsHost = commandLine.getOptionValue("fms-host");

            fmsPort = Integer.parseInt(commandLine.getOptionValue("fms-port"));

            fmsApp = commandLine.getOptionValue("fms-app");

            ipAddress = commandLine.getOptionValue("ip-address");

            uri = commandLine.getOptionValue("uri");

        } catch (ParseException e) {
            logger.error(e.getMessage());

            System.exit(-1);
        }

        new Thread(new Publisher(fmsHost, fmsPort, fmsApp, ipAddress, uri)).start();*/

        //new Thread(new Publisher("10.49.12.216", 1935, "dvr", "10.148.17.103", "h264")).start();
    }
}