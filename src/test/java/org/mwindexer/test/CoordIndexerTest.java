package org.mwindexer.test;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mediawiki.indexer.io.CoordIndexer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CoordIndexerTest {

	private static Logger LOG = LoggerFactory.getLogger(CoordIndexerTest.class);

	private CoordIndexer indexer;

	@Before
	public void setup() {
		indexer = new CoordIndexer("\\{\\{Coord.*\\}\\}", "coord_dt");
	}

	@Test
	public void test1() {
		LOG.info("Running test1");
		String text = "{{Coord|display=title|43.096569|-75.231887}}";
		String value = getLatLong(text);
		assertEquals("43.096569,-75.231887", value);
	}

	@Test
	public void test2() {
		LOG.info("Running test2");
		String text = "{{coord|43.65|-79.38}}";
		String value = getLatLong(text);
		assertEquals("43.65,-79.38", value);
	}

	@Test
	public void test3() {
		LOG.info("Running test3");
		String text = "{{coord|43.6500|-79.3800}}";
		String value = getLatLong(text);
		assertEquals("43.65,-79.38", value);
	}

	@Test
	public void test4() {
		LOG.info("Running test4");
		String text = "{{coord|43.651234|N|79.383333|W}}";
		String value = getLatLong(text);
		assertEquals("43.651234,-79.383333", value);
	}

	@Test
	public void test5() {
		LOG.info("Running test5");
		String text = "{{coord|43|29|N|79|23|W}}";
		String value = getLatLong(text);
		assertEquals("43.483333333333334,-79.38333333333334", value);
	}

	@Test
	public void test6() {
		LOG.info("Running test6");
		String text = "{{coord|43|29|4|N|79|23|0|W}}";
		String value = getLatLong(text);
		assertEquals("43.48444444444444,-79.38333333333334", value);
	}

	@Test
	public void test7() {
		LOG.info("Running test7");
		String text = "{{coord|43|29|4.5|N|79|23|0.5|W}}";
		String value = getLatLong(text);
		assertEquals("43.48458333333333,-79.38347222222222", value);
	}

	@Test
	public void test8() {
		LOG.info("Running test8");
		String text = "{{coord|55.752222|N|37.615556|E|format=dec|name=Moscow}}";
		String value = getLatLong(text);
		assertEquals("55.752222,37.615556", value);
	}

	private String getLatLong(String text) {
		Map<String, Object> fields = indexer.indexText(text);
		@SuppressWarnings("unchecked")
		String value = ((List<String>) fields.get("coord_dt")).get(0);
		return value;
	}
}
