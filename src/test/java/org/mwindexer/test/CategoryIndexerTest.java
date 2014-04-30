package org.mwindexer.test;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mwindexer.indexer.CategoryIndexer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CategoryIndexerTest {

	private static Logger LOG = LoggerFactory
			.getLogger(CategoryIndexerTest.class);

	private String fieldName;

	private CategoryIndexer indexer;

	@Before
	public void setup() {
		fieldName = "category_ss";
		indexer = new CategoryIndexer("\\[\\[Category:.*\\]\\]", fieldName);
	}

	@Test
	public void test1() {
		LOG.info("Running test1");
		String text = "[[Category:Test]]";
		String value = getCategory(text);
		assertEquals("Test", value);
	}

	private String getCategory(String text) {
		Map<String, Object> fields = indexer.indexText(text);
		@SuppressWarnings("unchecked")
		String value = ((List<String>) fields.get(fieldName)).get(0);
		return value;
	}

}
