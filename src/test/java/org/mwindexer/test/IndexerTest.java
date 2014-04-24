package org.mwindexer.test;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.util.AbstractSolrTestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mwindexer.Indexer;

public class IndexerTest {

	private SolrServer server;

	public static String getSchemaFile() {
		return "conf/schema.xml";
	}

	public static String getSolrConfigFile() {
		return "conf/solrconfig.xml";
	}

	@Before
	public void setUp() throws Exception {
//		server = new EmbeddedSolrServer(h.getCoreContainer(), h.getCore()
//				.getName());
	}

	@After
	public void shutDown() {
//		server.shutdown();
	}

	@Test
	public void testIndexer() {
		String args[] = { "-i", "sample.xml" };

//		Indexer indexer = new Indexer(args);

	}

}
