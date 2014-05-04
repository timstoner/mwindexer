package org.mwindexer.test;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.mediawiki.indexer.io.MediaWikiIndexer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MediaWikiIndexerTest {

	private static Logger LOG = LoggerFactory
			.getLogger(MediaWikiIndexerTest.class);

	@Test
	public void test() {
		File utica = new File("src/test/resources/text/utica.txt");
		try {
			String text = FileUtils.readFileToString(utica);
			MediaWikiIndexer indexer = new MediaWikiIndexer();
			indexer.indexText(text);
		} catch (IOException e) {
			LOG.error("missing file", e);
		}
	}
}
