package org.mwindexer.indexer;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.CloudSolrServer;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrInputDocument;
import org.mwindexer.DumpWriter;
import org.mwindexer.TextIndexer;
import org.mwindexer.Tools;
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

	private SolrFieldMap fieldMap;

	private List<TextIndexer> textIndexers;

	public SolrDumpWriter(int bufferSize, String type, String location) {
		String msg = String.format("init %s %s %s", bufferSize, type, location);
		LOG.info(msg);
		this.bufferSize = bufferSize;
		solrInputDocuments = new LinkedList<>();

		switch (type) {
		case "http":
			solrServer = new HttpSolrServer(location);
			break;
		case "cloud":
			solrServer = new CloudSolrServer(location);
			break;
		}
	}

	public void setSolrFieldMap(SolrFieldMap map) {
		this.fieldMap = map;
	}

	public void setTextIndexers(List<TextIndexer> indexers) {
		this.textIndexers = indexers;
	}

	@Override
	public void close() throws IOException {

	}

	@Override
	public void writeStartWiki() throws IOException {

	}

	@Override
	public void writeEndWiki() throws IOException {
		addSolrDocuments();
		commitSolrDocuments();
	}

	@Override
	public void writeSiteinfo(Siteinfo info) throws IOException {

	}

	@Override
	public void writeStartPage(Page page) throws IOException {
		LOG.debug("writeStartPage {}", page.Title.Text);
		doc = new SolrInputDocument();
		doc.addField(fieldMap.getPageIdField(), page.Id);
		doc.addField(fieldMap.getTitleField(), page.Title.Text);
		doc.addField(fieldMap.getRedirectField(), page.isRedirect);
		doc.addField(fieldMap.getRestrictionsField(), page.Restrictions);
	}

	@Override
	public void writeEndPage() throws IOException {
		solrInputDocuments.add(doc);

		if (solrInputDocuments.size() == bufferSize) {
			addSolrDocuments();
		}
	}

	@Override
	public void writeRevision(Revision revision) throws IOException {
		doc.addField(fieldMap.getTimestampField(),
				Tools.formatTimestamp(revision.Timestamp));
		doc.addField(fieldMap.getCommentField(), revision.Comment);
		doc.addField(fieldMap.getTextField(), revision.Text);

		for (TextIndexer indexer : textIndexers) {
			Map<String, Object> fields = indexer.indexText(revision.Text);
			for (Map.Entry<String, Object> entry : fields.entrySet()) {
				doc.addField(entry.getKey(), entry.getValue());
			}
		}
	}

	private void addSolrDocuments() {
		// solr complains if you add 0 documents 'missing content stream'
		if (solrInputDocuments.size() > 0) {
			try {
				UpdateResponse response = solrServer.add(solrInputDocuments);
				LOG.debug("Solr Add Response {}", response);
			} catch (SolrServerException | IOException e) {
				LOG.error("Problem sending documents to solr server", e);
			} finally {
				solrInputDocuments.clear();
			}
		}
	}

	private void commitSolrDocuments() {
		UpdateResponse response;
		try {
			response = solrServer.commit();
			LOG.debug("Solr Commit Response {}", response);
		} catch (SolrServerException | IOException e) {
			LOG.error("Problem commiting documents to solr server", e);
		}
	}
}
