package org.mwindexer.test;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.text.ParseException;
import java.util.Calendar;

import org.junit.Before;
import org.junit.Test;
import org.mediawiki.indexer.Tools;
import org.mediawiki.indexer.filter.BeforeTimeStampFilter;
import org.mediawiki.indexer.model.Revision;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BeforeTimeStampFilterTest {

	private static Logger LOG = LoggerFactory
			.getLogger(BeforeTimeStampFilterTest.class);

	private BeforeTimeStampFilter filter;

	private MockDumpWriter writer;

	@Before
	public void setUp() throws ParseException {
		writer = new MockDumpWriter();
		String date = "2014-03-04T03:30:28Z";

		filter = new BeforeTimeStampFilter(writer, date);
	}

	@Test
	public void testPagePermitted() {
		LOG.info("Testing filter pass");

		Revision revision = new Revision();

		String date = "2014-03-01T03:30:28Z";
		Calendar cal = Tools.parseUTCTimestamp(date);
		revision.Timestamp = cal;

		try {
			filter.writeRevision(revision);
		} catch (IOException e) {
			LOG.error("Problem writing revision", e);
		}

		assertEquals(true, writer.isRevisionPassed());
	}

	@Test
	public void testPageFiltered() {
		LOG.info("Testing filter fail");

		Revision revision = new Revision();

		String date = "2014-03-07T03:30:28Z";
		Calendar cal = Tools.parseUTCTimestamp(date);
		revision.Timestamp = cal;

		try {
			filter.writeRevision(revision);
		} catch (IOException e) {
			LOG.error("Problem writing revision", e);
		}

		assertEquals(false, writer.isRevisionPassed());
	}

}
