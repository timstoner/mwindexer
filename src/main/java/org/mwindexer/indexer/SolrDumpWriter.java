package org.mwindexer.indexer;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.CloudSolrServer;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrInputDocument;
import org.mwindexer.DumpWriter;
import org.mwindexer.model.Page;
import org.mwindexer.model.Revision;
import org.mwindexer.model.Siteinfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SolrDumpWriter implements DumpWriter {
	private static Logger LOG = LoggerFactory.getLogger(SolrDumpWriter.class);

	private SolrServer solrServer;

	private List<SolrInputDocument> solrInputDocuments;

	private SolrInputDocument doc;

	private int bufferSize;

	public SolrDumpWriter(int bufferSize, String location, String solrType) {
		LOG.info("SolrDumpWriter init");
		this.bufferSize = bufferSize;
		solrInputDocuments = new LinkedList<>();

		switch (solrType) {
		case "http":
			solrServer = new HttpSolrServer(location);
			break;
		case "cloud":
			solrServer = new CloudSolrServer(location);
			break;
		}
	}

	@Override
	public void close() throws IOException {

	}

	@Override
	public void writeStartWiki() throws IOException {

	}

	@Override
	public void writeEndWiki() throws IOException {
		try {
			UpdateResponse response = solrServer.commit();
			LOG.debug("Solr Commit Response {}", response);
		} catch (SolrServerException e) {
			LOG.error("Problem commiting documents to solr server", e);
		}
	}

	@Override
	public void writeSiteinfo(Siteinfo info) throws IOException {

	}

	@Override
	public void writeStartPage(Page page) throws IOException {
		doc = new SolrInputDocument();
		doc.addField("id", page.Id);
		doc.addField("title", page.Title.Text);
		doc.addField("redirect_b", page.isRedirect);
		doc.addField("restrictions_t", page.Restrictions);
	}

	@Override
	public void writeEndPage() throws IOException {
		solrInputDocuments.add(doc);

		if (solrInputDocuments.size() == bufferSize) {
			try {
				UpdateResponse response = solrServer.add(solrInputDocuments);
				LOG.debug("Solr Add Response {}", response);
			} catch (SolrServerException e) {
				LOG.error("Problem sending documents to solr server", e);
			} finally {
				solrInputDocuments.clear();
			}
		}
	}

	@Override
	public void writeRevision(Revision revision) throws IOException {
		doc.addField("timestamp_dt", revision.Timestamp);
		doc.addField("comment_t", revision.Comment);
		doc.addField("content", revision.Text);
	}

}
