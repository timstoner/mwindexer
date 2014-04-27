package org.mwindexer.test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.mwindexer.indexer.CoordIndexer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CoordIndexerTest {

	private static Logger LOG = LoggerFactory.getLogger(CoordIndexerTest.class);

	private CoordIndexer indexer;

	private String text;

	@Before
	public void setup() {
		indexer = new CoordIndexer("\\{\\{Coord.*\\}\\}", "coord_dt");

		String path = "src/test/resources/text/utica.txt";
		File file = new File(path);

		try {
			text = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
		} catch (IOException e) {
			LOG.error("Problem reading file", e);
		}
	}

	@Test
	public void testTextIndexer() {
		LOG.info("Running test");
		indexer.indexText(text);

	}

}
