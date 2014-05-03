package org.mwindexer.test;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.mwindexer.filters.ListFilter;
import org.mwindexer.model.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ListFilterTest {
	private static Logger LOG = LoggerFactory.getLogger(ListFilterTest.class);

	private ListFilter filter;

	private MockDumpWriter writer;

	@Before
	public void setup() {
		writer = new MockDumpWriter();
		try {
			filter = new ListFilter(writer,
					"src/test/resources/filters/listfilter.txt");
		} catch (IOException e) {
			LOG.error("Problem reading exact list filter data", e);
		}
	}

	@Test
	public void testPagePermitted() {
		Page page = new Page();
		page.Title = "permitted";
		page.Ns = 0;

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
		page.Title = "Talk:permitted";
		page.Ns = 1;

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
		Page page = new Page();
		page.Title = "filtered";
		page.Ns = 0;

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
		page.Title = "Talk:filtered";
		page.Ns = 1;

		try {
			filter.writeStartPage(page);
			filter.writeEndPage();
		} catch (IOException e) {
			LOG.error("Problem writing revision", e);
		}

		assertEquals(false, writer.isPagePassed());
	}
}
