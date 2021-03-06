package org.mediawiki.indexer;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.mediawiki.indexer.filter.AfterTimeStampFilter;
import org.mediawiki.indexer.filter.BeforeTimeStampFilter;
import org.mediawiki.indexer.filter.ExactListFilter;
import org.mediawiki.indexer.filter.LatestFilter;
import org.mediawiki.indexer.filter.ListFilter;
import org.mediawiki.indexer.filter.NamespaceFilter;
import org.mediawiki.indexer.filter.RevisionListFilter;
import org.mediawiki.indexer.filter.TitleMatchFilter;
import org.mediawiki.indexer.io.XmlDumpReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Main {

	private static Logger LOG = LoggerFactory.getLogger(Main.class);

	private static ApplicationContext context;

	private int pageCount;

	public static void main(String[] args) {
		Main main = new Main();
		main.execute(args);
	}

	public void execute(String[] args) {
		LOG.info("MediaWiki Dump File Indexer");

		// parse the command line options
		CommandLine cl = parseArguments(args);
		// get the user's solr url
		String solrURL = cl.getOptionValue("u");
		// get the user's input file path
		String inputPath = cl.getOptionValue("i");

		// get number of pages to index from dataset
		String pagecount = cl.getOptionValue("p");
		if (pagecount != null) {
			pageCount = Integer.parseInt(pagecount);
		}

		String countDownTime = cl.getOptionValue("c");
		if (countDownTime != null) {
			int count = Integer.parseInt(countDownTime);
			showCountDownTimer(count);
		}

		// if a solr url was specified, overwrite the default
		if (solrURL != null) {
			LOG.info("Solr URL: " + solrURL);
			System.setProperty("solr.url", solrURL);
		}

		// create the input stream
		InputStream inputStream = getInputStream(inputPath);
		if (inputStream != null) {
			readInputStream(inputStream);
		} else {
			LOG.error("Input Stream is null, exiting");
			System.exit(-1);
		}
	}

	public void readInputStream(InputStream inputStream) {

		// instantiate the application context using a classpath
		// applicationContext.xml file
		LOG.info("Initializing Application Context");
		DateTime startDateTime = DateTime.now();

		context = new ClassPathXmlApplicationContext("applicationContext.xml");
		// get the configured dump reader
		XmlDumpReader reader = context
				.getBean("xmlReader", XmlDumpReader.class);

		// notify user we are starting data ingest
		LOG.info("Reading dump input");

		if(pageCount > 0) {
			reader.setPageCountLimit(pageCount);
		}
		
		try {
			reader.setInputStream(inputStream);
			reader.readDump();
		} catch (IOException e) {
			LOG.error("Problem reading dump", e);
		}

		DateTime endDateTime = DateTime.now();
		// calculate how long the process took
		Interval interval = new Interval(startDateTime, endDateTime);

		LOG.info("Total Time: " + interval.toDuration().getStandardSeconds());
		LOG.info("All done.");
	}

	public InputStream getInputStream(String path) {
		InputStream inputStream = null;
		try {
			if (path != null) {
				// if an input path was specified by user, use it as the input
				LOG.info("Input File: " + path);
				inputStream = Tools.openInputFile(path);
			} else {
				// if no input path specified, use the standard input
				LOG.info("No Input File specified, using Standard Input");
				inputStream = Tools.openStandardInput();
			}
		} catch (IOException e) {
			LOG.error("Error opening input stream", e);
		}

		return inputStream;
	}

	public CommandLine parseArguments(String[] args) {
		Options options = new Options();
		Option solrUrlOption = new Option("u", "url", true,
				"Url of HTTP Solr Server");
		options.addOption(solrUrlOption);

		Option progressOption = new Option("p", "progress", true,
				"Progress Interval");
		options.addOption(progressOption);

		Option inputFileOption = new Option("i", "input", true,
				"Path to input file (.xml/.bz2/.gz)");
		options.addOption(inputFileOption);

		Option pageCountOption = new Option("p", "pagecount", true,
				"Number of pages to index from data dump");
		options.addOption(pageCountOption);

		Option countDownTimer = new Option("c", "countdown", true,
				"Time in seconds to countdown before starting");
		options.addOption(countDownTimer);

		Option filterOption = new Option("f", "filter", true,
				"Filters information from the dump");

		CommandLineParser clparser = new BasicParser();
		CommandLine cl = null;

		try {
			cl = clparser.parse(options, args);
		} catch (org.apache.commons.cli.ParseException e) {
			LOG.error("Error parsing command line arguments", e);
			System.exit(-1);
		}

		return cl;
	}

	public DumpWriter addFilter(DumpWriter sink, String filter, String param)
			throws IOException, ParseException {
		DumpWriter writer = null;

		switch (filter) {
		case "latest":
			writer = new LatestFilter(sink);
			break;
		case "namespace":
			writer = new NamespaceFilter(sink, param);
			break;
		case "titlematch":
			writer = new TitleMatchFilter(sink, param);
			break;
		case "list":
			writer = new ListFilter(sink, param);
			break;
		case "exactlist":
			writer = new ExactListFilter(sink, param);
			break;
		case "before":
			writer = new BeforeTimeStampFilter(sink, param);
			break;
		case "after":
			writer = new AfterTimeStampFilter(sink, param);
			break;
		case "revlist":
			writer = new RevisionListFilter(sink, param);
		default:
			throw new IllegalArgumentException("Filter unknown: " + filter);
		}

		return writer;
	}

	private void showCountDownTimer(int count) {
		LOG.info("Starting Data Dump Indexer in:");
		try {
			for (int i = count; i > 0; i--) {
				LOG.info(String.valueOf(i));
				Thread.sleep(1000);
			}
		} catch (InterruptedException e) {
			LOG.warn("Problem sleeping...", e);
		}

	}
}
