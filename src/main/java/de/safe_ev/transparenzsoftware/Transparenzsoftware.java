/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * Copyright 2021-2023 S.A.F.E. e.V., Deutschland, safe-ev.de
 */
package de.safe_ev.transparenzsoftware;

import javax.swing.UIManager;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;

import de.safe_ev.transparenzsoftware.gui.TransparenzSoftwareMain;
import de.safe_ev.transparenzsoftware.i18n.Translator;
import de.safe_ev.transparenzsoftware.output.ConsoleFileProcessor;
import de.safe_ev.transparenzsoftware.verification.VerificationParserFactory;

public class Transparenzsoftware {

	private final static Logger LOGGER = LogManager.getLogger(Transparenzsoftware.class);
	private static boolean testEnvironment;

	static void main(String[] args, boolean testEnvironment) throws Exception {
		Transparenzsoftware.testEnvironment = testEnvironment;
		main(args);
	}

	public static void main(String[] args) throws Exception {

		try {
			final String cn = UIManager.getSystemLookAndFeelClassName();
			UIManager.setLookAndFeel(cn);
		} catch (final Exception cnf) {
		}

		final VerificationParserFactory factory = new VerificationParserFactory();
		final CommandLineParser commandLineParser = new DefaultParser();
		final Options options = setUpCliOptions();
		try {
			final CommandLine commandLine = commandLineParser.parse(options, args);
			if (commandLine.hasOption("v")) {
				setUpVerboseLogging();
			}
			if (commandLine.hasOption("l")) {
				final String optionValue = commandLine.getOptionValue("l");
				Translator.init(optionValue);

			}
			String filePath = null;
			if (commandLine.hasOption("f")) {
				filePath = commandLine.getOptionValue("f");
			}

			if (commandLine.hasOption("w")) {
				filePath = commandLine.getOptionValue("f");
			}

			if (commandLine.hasOption("h")) {
				LOGGER.debug("print help");
				printHelp(options);
			} else if (!commandLine.hasOption("cli")) {
				try {
					TransparenzSoftwareMain.initWithParser(factory, filePath);
				} catch (final Exception e) {
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
				boolean overwrite = false;
				if (commandLine.hasOption("o")) {
					outputPath = commandLine.getOptionValue("o");
				}
				if (commandLine.hasOption("w")) {
					overwrite = true;
				}
				LOGGER.info("Read in file " + filePath);

				final ConsoleFileProcessor fileProcessor = new ConsoleFileProcessor(factory);
				final boolean result = fileProcessor.processFile(filePath, outputPath, overwrite);
				exit(result ? 2 : 0);
			}
		} catch (final ParseException e) {
			LOGGER.error(Translator.get("error.invalid.input.parameters"));
			System.err.println(Translator.get("error.invalid.input.parameters"));
			printHelp(options);
		}

	}

	private static void exit(int code) {
		if (!testEnvironment) {
			System.exit(code);
		} else {
			LOGGER.info(String.format("App started and resulted and exited with code %s", code));
		}
	}

	private static void printHelp(Options options) {
		final HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("ant", options);
	}

	private static void setUpVerboseLogging() {
		final LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
		final Configuration config = ctx.getConfiguration();
		final LoggerConfig loggerConfig = config.getLoggerConfig(LogManager.ROOT_LOGGER_NAME);
		loggerConfig.setLevel(Level.DEBUG);
		ctx.updateLoggers();
	}

	private static Options setUpCliOptions() {
		final Options options = new Options();
		options.addOption("v", "verbose", false, "Enables verbose logging in the stdout");
		options.addOption("f", "file", true,
				"Path to a file which should be read. If in cli mode it will be directly, processed. If it is opened with the gui, the file will opened automatically.");
		options.addOption("l", "locale", true,
				"Choose the the language which will be used in the gui and for messages. Currently there is english (en_EN) and german (de_DE) present. If an invalid locale or no locale is given, the app will try to use the default settings of the system. If it is neither german or english, english will be used.");
		options.addOption("o", "output", true,
				"File where the output should be written to. If the file does not exist, the app will try to create it. Content that has been there will not be overwritten");
		options.addOption("w", "write", false, "Overwrite the output file if it already exists.");
		options.addOption("h", "help", false, "Print that help page");
		options.addOption("cli", false,
				"Command line mode. This means no gui will be opened. Must always be with called with a -f parameter");
		return options;
	}

}
