package org.mwindexer;

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
import org.mwindexer.indexer.AfterTimeStampFilter;
import org.mwindexer.indexer.BeforeTimeStampFilter;
import org.mwindexer.indexer.DumpWriter;
import org.mwindexer.indexer.ExactListFilter;
import org.mwindexer.indexer.LatestFilter;
import org.mwindexer.indexer.ListFilter;
import org.mwindexer.indexer.NamespaceFilter;
import org.mwindexer.indexer.NotalkFilter;
import org.mwindexer.indexer.RevisionListFilter;
import org.mwindexer.indexer.TitleMatchFilter;
import org.mwindexer.indexer.XmlDumpReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Indexer {

	private static Logger LOG = LoggerFactory.getLogger(Indexer.class);

	public static void main(String[] args) {
		new Indexer(args);
	}

	public Indexer(String[] args) {
		LOG.info("MediaWiki Dump File Indexer");

		Configuration configuration = new Configuration();

		CommandLine cl = parseArguments(args);
		String solrURL = cl.getOptionValue("u");
		String inputPath = cl.getOptionValue("i");

		if (solrURL != null) {
			LOG.info("Solr URL: " + solrURL);
			System.setProperty("solr.url", solrURL);
		}

		InputStream inputStream = null;

		try {
			if (inputPath == null) {
				LOG.info("No Input File specified, using Standard Input");
				inputStream = Tools.openStandardInput();
			} else {
				LOG.info("Input File: " + inputPath);
				inputStream = Tools.openInputFile(inputPath);
			}
		} catch (IOException e) {
			LOG.error("Error opening input stream", e);
		}

		configuration.setInputStream(inputStream);

		ApplicationContext context = new ClassPathXmlApplicationContext();

		 XmlDumpReader reader = context.getBean(XmlDumpReader.class);

		LOG.info("Reading dump input");
		DateTime startDateTime = DateTime.now();

		try {
			reader.readDump();
		} catch (IOException e) {
			LOG.error("Problem reading dump", e);
		}

		DateTime endDateTime = DateTime.now();
		Interval interval = new Interval(startDateTime, endDateTime);

		LOG.info("Total Time: " + interval.toDuration().getStandardSeconds());
		LOG.info("All done.");
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
		if (filter.equals("latest"))
			return new LatestFilter(sink);
		else if (filter.equals("namespace"))
			return new NamespaceFilter(sink, param);
		else if (filter.equals("notalk"))
			return new NotalkFilter(sink);
		else if (filter.equals("titlematch"))
			return new TitleMatchFilter(sink, param);
		else if (filter.equals("list"))
			return new ListFilter(sink, param);
		else if (filter.equals("exactlist"))
			return new ExactListFilter(sink, param);
		else if (filter.equals("revlist"))
			return new RevisionListFilter(sink, param);
		else if (filter.equals("before"))
			return new BeforeTimeStampFilter(sink, param);
		else if (filter.equals("after"))
			return new AfterTimeStampFilter(sink, param);
		else
			throw new IllegalArgumentException("Filter unknown: " + filter);
	}
}
