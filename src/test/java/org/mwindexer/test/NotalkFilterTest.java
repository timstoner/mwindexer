package org.mwindexer.test;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.text.ParseException;
import java.util.Calendar;

import org.junit.Before;
import org.junit.Test;
import org.mwindexer.Tools;
import org.mwindexer.filters.AfterTimeStampFilter;
import org.mwindexer.filters.NotalkFilter;
import org.mwindexer.indexer.NamespaceSet;
import org.mwindexer.model.Page;
import org.mwindexer.model.Revision;
import org.mwindexer.model.Title;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NotalkFilterTest {
	private static Logger LOG = LoggerFactory.getLogger(NotalkFilterTest.class);

	private NotalkFilter filter;

	private MockDumpWriter writer;

	private NamespaceSet namespaceSet;

	@Before
	public void setUp() throws ParseException {
		namespaceSet = new NamespaceSet();
		namespaceSet.add(0, "");
		namespaceSet.add(1, "Talk");

		writer = new MockDumpWriter();
		filter = new NotalkFilter(writer);
	}

	@Test
	public void testPagePermitted() {
		LOG.info("Testing filter pass");

		Page page = new Page();
		page.Title = new Title(0, "test", namespaceSet);

		try {
			filter.writeStartPage(page);
			filter.writeEndPage();
		} catch (IOException e) {
			LOG.error("Problem writing revision", e);
		}

		assertEquals(true, writer.isPagePassed());
	}

	@Test
	public void testPageFiltered() {
		LOG.info("Testing filter fail");

		Page page = new Page();
		page.Title = new Title(1, "test", namespaceSet);

		try {
			filter.writeStartPage(page);
			filter.writeEndPage();
		} catch (IOException e) {
			LOG.error("Problem writing revision", e);
		}

		assertEquals(false, writer.isPagePassed());
	}
}
