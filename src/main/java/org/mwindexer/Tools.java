package org.mwindexer;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.zip.GZIPInputStream;

import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Tools {
	private static Logger LOG = LoggerFactory.getLogger(Tools.class);

	static final int IN_BUF_SZ = 1024 * 1024;
	private static final int OUT_BUF_SZ = 1024 * 1024;

	private static final DateFormat dateFormat = new SimpleDateFormat(
			"yyyy'-'MM'-'dd'T'HH':'mm':'ss'Z'");

	static {
		dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
	}

	public static InputStream openInputFile(String arg) throws IOException {
		if (arg.equals("-"))
			return openStandardInput();
		InputStream infile = new BufferedInputStream(new FileInputStream(arg),
				IN_BUF_SZ);
		if (arg.endsWith(".gz"))
			return new GZIPInputStream(infile);
		else if (arg.endsWith(".bz2"))
			return openBZip2Stream(infile);
		else
			return infile;
	}

	static InputStream openStandardInput() throws IOException {
		return new BufferedInputStream(System.in, IN_BUF_SZ);
	}

	static InputStream openBZip2Stream(InputStream infile) throws IOException {
		return new BZip2CompressorInputStream(infile);
	}

	public static String formatTimestamp(Calendar ts) {
		return dateFormat.format(ts.getTime());
	}

	public static Calendar parseUTCTimestamp(String text) {
		// thanks java for the quick hack to replace a UTC timestamp with a time
		// zone
		text = text.replaceAll("Z$", "+0000");
		SimpleDateFormat format = getUTCDateFormat();
		Calendar cal = Calendar.getInstance();
		Date date = new Date();
		try {
			date = format.parse(text);
		} catch (ParseException e) {
			LOG.warn("Problem parsing date " + text, e);
		}
		cal.setTime(date);
		return cal;

	}

	public static SimpleDateFormat getUTCDateFormat() {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
		return format;
	}
}