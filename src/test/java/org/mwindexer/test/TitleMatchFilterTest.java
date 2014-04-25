package org.mwindexer.test;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.text.ParseException;

import org.junit.Before;
import org.junit.Test;
import org.mwindexer.filters.TitleMatchFilter;
import org.mwindexer.indexer.NamespaceSet;
import org.mwindexer.model.Page;
import org.mwindexer.model.Title;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TitleMatchFilterTest {
	private static Logger LOG = LoggerFactory
			.getLogger(TitleMatchFilterTest.class);

	private MockDumpWriter writer;

	private TitleMatchFilter filter;

	private NamespaceSet namespaceSet;

	@Before
	public void setUp() throws ParseException {
		writer = new MockDumpWriter();

		namespaceSet = new NamespaceSet();
		namespaceSet.add(0, "");

		filter = new TitleMatchFilter(writer, "[1]+");
	}

	@Test
	public void testPagePermitted() {
		Page page = new Page();
		page.Title = new Title(0, "111", namespaceSet);

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
		page.Title = new Title(0, "asdf", namespaceSet);

		try {
			filter.writeStartPage(page);
			filter.writeEndPage();
		} catch (IOException e) {
			LOG.error("Problem writing page", e);
		}

		assertEquals(false, writer.isPagePassed());
	}
}
