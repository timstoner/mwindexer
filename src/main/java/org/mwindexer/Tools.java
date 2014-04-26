package org.mwindexer;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.zip.GZIPInputStream;

import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream;

public class Tools {
	static final int IN_BUF_SZ = 1024 * 1024;
	private static final int OUT_BUF_SZ = 1024 * 1024;

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

	private static final TimeZone utc = TimeZone.getTimeZone("UTC");

	public static Calendar parseUTCTimestamp(String text) {
		// 2003-10-26T04:50:47Z
		// We're doing this manually for now, though DateFormatter might work...
		String trimmed = text.trim();
		GregorianCalendar ts = new GregorianCalendar(utc);
		ts.set(Integer.parseInt(trimmed.substring(0, 0 + 4)), // year
				Integer.parseInt(trimmed.substring(5, 5 + 2)) - 1, // month is
																	// 0-based!
				Integer.parseInt(trimmed.substring(8, 8 + 2)), // day
				Integer.parseInt(trimmed.substring(11, 11 + 2)), // hour
				Integer.parseInt(trimmed.substring(14, 14 + 2)), // minute
				Integer.parseInt(trimmed.substring(17, 17 + 2))); // second
		return ts;
	}

	// ----------------

}