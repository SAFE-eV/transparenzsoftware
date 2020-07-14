package com.hastobe.transparenzsoftware;

import com.hastobe.transparenzsoftware.gui.TransparenzSoftwareMain;
import com.hastobe.transparenzsoftware.i18n.Translator;
import com.hastobe.transparenzsoftware.output.ConsoleFileProcessor;
import com.hastobe.transparenzsoftware.verification.VerificationParserFactory;
import org.apache.commons.cli.*;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;

public class App {

    private final static Logger LOGGER = LogManager.getLogger(App.class);
    private static boolean testEnvironment;

    static void main(String[] args, boolean testEnvironment) throws Exception {
        App.testEnvironment = testEnvironment;
        main(args);
    }

    public static void main(String[] args) throws Exception {

        VerificationParserFactory factory = new VerificationParserFactory();
        CommandLineParser commandLineParser = new DefaultParser();
        Options options = setUpCliOptions();
        try {
            CommandLine commandLine = commandLineParser.parse(options, args);
            if (commandLine.hasOption("v")) {
                setUpVerboseLogging();
            }
            if(commandLine.hasOption("l")){
                String optionValue = commandLine.getOptionValue("l");
                Translator.init(optionValue);

            }
            String filePath = null;
            if (commandLine.hasOption("f")) {
                filePath = commandLine.getOptionValue("f");
            }

            if (commandLine.hasOption("h")) {
                LOGGER.debug("print help");
                printHelp(options);
            } else if (!commandLine.hasOption("cli")) {
                try {
                    TransparenzSoftwareMain.initWithParser(factory, filePath);
                } catch (Exception e){
                    LOGGER.error("Error on main window", e);
                    exit(0);
                    return;
                }
            } else {
                if (filePath == null) {
                    printHelp(options);
                    System.err.println(Translator.get("error.no.input.file"));
                    exit(0);
                    return;
                }
                String outputPath = null;
                if (commandLine.hasOption("o")) {
                    outputPath = commandLine.getOptionValue("o");
                }
                LOGGER.info("Read in file " + filePath);

                ConsoleFileProcessor fileProcessor = new ConsoleFileProcessor(factory);
                boolean result = fileProcessor.processFile(filePath, outputPath);
                exit(result ? 2 : 0);
            }
        } catch (ParseException e) {
            LOGGER.error(Translator.get("error.invalid.input.parameters"));
            System.err.println(Translator.get("error.invalid.input.parameters"));
            printHelp(options);
        }


    }

    private static void exit(int code){
        if(!testEnvironment) {
            System.exit(code);
        } else {
            LOGGER.info(String.format("App started and resulted and exited with code %s", code));
        }
    }
    private static void printHelp(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("ant", options);
    }

    private static void setUpVerboseLogging() {
        LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
        Configuration config = ctx.getConfiguration();
        LoggerConfig loggerConfig = config.getLoggerConfig(LogManager.ROOT_LOGGER_NAME);
        loggerConfig.setLevel(Level.DEBUG);
        ctx.updateLoggers();
    }

    private static Options setUpCliOptions() {
        Options options = new Options();
        options.addOption("v", "verbose", false, "Enables verbose logging in the stdout");
        options.addOption("f", "file", true, "Path to a file which should be read. If in cli mode it will be directly, processed. If it is opened with the gui, the file will opened automatically.");
        options.addOption("l", "locale", true, "Choose the the language which will be used in the gui and for messages. Currently there is english (en_EN) and german (de_DE) present. If an invalid locale or no locale is given, the app will try to use the default settings of the system. If it is neither german or english, english will be used.");
        options.addOption("o", "output", true, "File where the output should be written to. If the file does not exist, the app will try to create it. Content that has been there will be overwritten");
        options.addOption("h", "help", false, "Print that help page");
        options.addOption("cli", false, "Command line mode. This means no gui will be opened. Must always be with called with a -f parameter");
        return options;
    }

}

