package com.danidemi.jlubricant.logback;

import java.io.File;
import java.io.InputStream;
import java.net.URL;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.status.StatusUtil;
import ch.qos.logback.core.util.StatusPrinter;

public abstract class ConfigureLogback {

	static abstract class Configurator {
		public void configure() {
			LoggerContext context = (LoggerContext) LoggerFactory
					.getILoggerFactory();
			try {

				JoranConfigurator configurator = new JoranConfigurator();
				configurator.setContext(context);
				withConfigurator(configurator);
			} catch (JoranException je) {
				// StatusPrinter will handle this
				//throw new RuntimeException(je);
			} catch (Exception ex) {
				throw new RuntimeException(ex);
			}
			StatusPrinter.printInCaseOfErrorsOrWarnings(context); // Internal
			// status data is printed in case of warnings or errors.
		}

		abstract void withConfigurator(JoranConfigurator configurator)
				throws JoranException;
	}

	/** Configure logback logging system using the provided file. */
	static void configure(final File f) {
		new Configurator() {

			@Override
			void withConfigurator(JoranConfigurator configurator)
					throws JoranException {
				configurator.doConfigure(f);

			}
		}.configure();
	}

	/** Configure logback logging system using the file at the given path. */
	static void configure(final String f) {
		new Configurator() {

			@Override
			void withConfigurator(JoranConfigurator configurator)
					throws JoranException {
				configurator.doConfigure(f);

			}
		}.configure();
	}

	/** Configure logback logging system using the file at the given URL. */
	static void configure(final URL f) {
		new Configurator() {

			@Override
			void withConfigurator(JoranConfigurator configurator)
					throws JoranException {
				configurator.doConfigure(f);

			}
		}.configure();
	}

	/** Configure logback logging system using the xml from the given stream. */
	static void configure(final InputStream f) {
		new Configurator() {

			@Override
			void withConfigurator(JoranConfigurator configurator)
					throws JoranException {

				if (f == null)
					throw new IllegalArgumentException("File cannot be null");

				configurator.doConfigure(f);

			}
		}.configure();
	}

}
