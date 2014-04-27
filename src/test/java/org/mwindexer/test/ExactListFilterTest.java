package org.mwindexer.test;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.mwindexer.filters.ExactListFilter;
import org.mwindexer.indexer.NamespaceSet;
import org.mwindexer.model.Page;
import org.mwindexer.model.Title;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExactListFilterTest {
	private static Logger LOG = LoggerFactory
			.getLogger(ExactListFilterTest.class);

	private ExactListFilter filter;

	private MockDumpWriter writer;

	private NamespaceSet namespaceSet;

	@Before
	public void setup() {
		namespaceSet = new NamespaceSet();
		namespaceSet.add(0, "");
		namespaceSet.add(1, "Talk");

		writer = new MockDumpWriter();
		try {
			filter = new ExactListFilter(writer,
					"src/test/resources/filters/exactlistfilter.txt");
		} catch (IOException e) {
			LOG.error("Problem reading exact list filter data", e);
		}
	}

	@Test
	public void testPagePermitted() {
		Page page = new Page();
		page.Title = new Title(0, "permitted", namespaceSet);

		try {
			filter.writeStartPage(page);
			filter.writeEndPage();
		} catch (IOException e) {
			LOG.error("Problem writing revision", e);
		}

		assertEquals(true, writer.isPagePassed());
	}

	@Test
	public void testTalkPagePermitted() {
		Page page = new Page();
		page.Title = new Title(1, "permitted", namespaceSet);

		try {
			filter.writeStartPage(page);
			filter.writeEndPage();
		} catch (IOException e) {
			LOG.error("Problem writing revision", e);
		}

		assertEquals(false, writer.isPagePassed());
	}

	@Test
	public void testPageFiltered() {
		Page page = new Page();
		page.Title = new Title(0, "filtered", namespaceSet);

		try {
			filter.writeStartPage(page);
			filter.writeEndPage();
		} catch (IOException e) {
			LOG.error("Problem writing revision", e);
		}

		assertEquals(false, writer.isPagePassed());
	}

	@Test
	public void testTalkPageFiltered() {
		Page page = new Page();
		page.Title = new Title(1, "filtered", namespaceSet);

		try {
			filter.writeStartPage(page);
			filter.writeEndPage();
		} catch (IOException e) {
			LOG.error("Problem writing revision", e);
		}

		assertEquals(false, writer.isPagePassed());
	}

}