package org.mwindexer.test;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;
import org.mwindexer.Tools;
import org.mwindexer.indexer.XmlDumpReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class XmlDumpReaderTest {

	private static Logger LOG = LoggerFactory
			.getLogger(XmlDumpReaderTest.class);

	private MockDumpWriter writer;

	@Test
	public void testNs() {
		readSubjectPage();
		assertEquals(0, writer.getPage().Ns);
	}

	@Test
	public void testPageId() {
		readSubjectPage();
		assertEquals(1, writer.getPage().Id);
	}

	@Test
	public void testRevisionId() {
		readSubjectPage();
		assertEquals(651, writer.getRevision().Id);
	}

	@Test
	public void testTimestamp() {
		readSubjectPage();
		String ts = Tools.formatTimestamp(writer.getRevision().Timestamp);
		assertEquals("2010-08-26T22:38:36Z", ts);
	}

	@Test
	public void testText() {
		readSubjectPage();
		assertEquals("this is a sample page for testing",
				writer.getRevision().Text);
	}

	public void testTalkNamespace() {
		readTalkPage();
		assertEquals(1, writer.getPage().Ns);
	}

	private void readSubjectPage() {
		writer = new MockDumpWriter();
		String file = "src/test/resources/samplepage.xml";
		InputStream is = null;

		try {
			is = Tools.openInputFile(file);
		} catch (IOException e) {
			LOG.warn("problem opening the sample file", e);
		}

		XmlDumpReader dumpReader = new XmlDumpReader();
		dumpReader.setDumpWriter(writer);
		dumpReader.setInputStream(is);

		try {
			dumpReader.readDump();
		} catch (IOException e) {
			LOG.warn("problem reading dump", e);
		}
	}

	private void readTalkPage() {
		writer = new MockDumpWriter();
		String file = "src/test/resources/sampletalkpage.xml";
		InputStream is = null;

		try {
			is = Tools.openInputFile(file);
		} catch (IOException e) {
			LOG.warn("problem opening the sample file", e);
		}

		XmlDumpReader dumpReader = new XmlDumpReader();
		dumpReader.setDumpWriter(writer);
		dumpReader.setInputStream(is);

		try {
			dumpReader.readDump();
		} catch (IOException e) {
			LOG.warn("problem reading dump", e);
		}
	}
}
