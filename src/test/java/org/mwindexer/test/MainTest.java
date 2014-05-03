package org.mwindexer.test;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.core.CoreContainer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mwindexer.Main;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainTest {

	private static Logger LOG = LoggerFactory.getLogger(MainTest.class);

	private SolrServer server;

	// @Before
	// public void setUp() throws Exception {
	// LOG.info("setting up");
	// CoreContainer container = new CoreContainer("src/test/resources/solr");
	// server = new EmbeddedSolrServer(container, "core1");
	// }

	// @After
	// public void shutDown() {
	// LOG.info("shutting down");
	// server.shutdown();
	// }

	@Test
	public void testIndexer() {
		LOG.info("testing indexer");
		String args[] = { "-i", "src/test/resources/sample.xml" };

		Main main = new Main();
		// main.execute(args);

	}

}
