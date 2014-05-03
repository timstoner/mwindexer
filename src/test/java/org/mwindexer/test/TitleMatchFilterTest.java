package org.mwindexer.test;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.text.ParseException;

import org.junit.Before;
import org.junit.Test;
import org.mwindexer.filters.TitleMatchFilter;
import org.mwindexer.indexer.NamespaceSet;
import org.mwindexer.model.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TitleMatchFilterTest {
	private static Logger LOG = LoggerFactory
			.getLogger(TitleMatchFilterTest.class);

	private MockDumpWriter writer;

	private TitleMatchFilter filter;

	@Before
	public void setUp() throws ParseException {
		writer = new MockDumpWriter();
		filter = new TitleMatchFilter(writer, "[1]+");
	}

	@Test
	public void testPagePermitted() {
		Page page = new Page();
		page.Title = "111";
		page.Ns = 0;

		try {
			filter.writeStartPage(page);
			filter.writeEndPage();
		} catch (IOException e) {
			LOG.error("Problem writing page", e);
		}

		assertEquals(true, writer.isPagePassed());
	}

	@Test
	public void testPageFiltered() {
		Page page = new Page();
		page.Title = "asdf";
		page.Ns = 0;

		try {
			filter.writeStartPage(page);
			filter.writeEndPage();
		} catch (IOException e) {
			LOG.error("Problem writing page", e);
		}

		assertEquals(false, writer.isPagePassed());
	}
}
