package org.mwindexer.indexer;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

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

	private static boolean DEBUG = true;

	private SolrServer solrServer;

	private SolrFieldMap fieldMap;

	private List<TextIndexer> textIndexers;

	private ThreadPoolExecutor executor;

	private int bufferSize;

	private List<Article> articles;

	private Article currentArticle;

	private boolean handleArticle;

	public SolrDumpWriter(int bufferSize, String type, String location) {
		String msg = String.format("init %s %s %s", bufferSize, type, location);
		LOG.info(msg);
		this.bufferSize = bufferSize;

		// solrInputDocuments = new LinkedList<>();
		// solrInputDocuments =
		// Collections.synchronizedList(solrInputDocuments);
		textIndexers = new LinkedList<>();

		switch (type) {
		case "http":
			solrServer = new HttpSolrServer(location);
			break;
		case "cloud":
			solrServer = new CloudSolrServer(location);
			break;
		}

		articles = new LinkedList<>();
		// worker = new ArticleWorker();
	}

	public void setSolrFieldMap(SolrFieldMap map) {
		this.fieldMap = map;
	}

	public void setTextIndexers(List<TextIndexer> indexers) {
		this.textIndexers = indexers;
	}

	public void setThreadPoolExecutor(ThreadPoolExecutor executor) {
		this.executor = executor;
		executor.setRejectedExecutionHandler(new RejectedExecutionHandler() {

			@Override
			public void rejectedExecution(Runnable r,
					ThreadPoolExecutor executor) {
				LOG.debug("ThreadPoolExecutor rejected task, retrying in 100ms");
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					LOG.warn("Thread interrupted while waiting...", e);
				} finally {
					executor.submit(r);
				}
			}
		});
	}

	@Override
	public void close() throws IOException {

	}

	@Override
	public void writeStartWiki() throws IOException {

	}

	@Override
	public void writeEndWiki() throws IOException {
		// complete the last set of working articles
		startArticleWorker();
		// commit to the solr index
		commitSolrDocuments();
	}

	@Override
	public void writeSiteinfo(Siteinfo info) throws IOException {

	}

	@Override
	public void writeStartPage(Page page) throws IOException {
		currentArticle = new Article();
		currentArticle.Page = page;

		if (page.isRedirect) {
			handleArticle = false;
		} else {
			handleArticle = true;
		}
		// queue.add(page);
		// doc = new SolrInputDocument();

	}

	@Override
	public void writeEndPage() throws IOException {
		// if we're handling the article (ie not a redirect)
		if (handleArticle) {
			// add to list of working articles
			articles.add(currentArticle);

			// if the working articles list is at max
			if (articles.size() == bufferSize) {
				startArticleWorker();
			}
		}
		// solrInputDocuments.add(doc);
		//
		// if (solrInputDocuments.size() == bufferSize) {
		// addSolrDocuments();
		// commitSolrDocuments();
		// }
	}

	@Override
	public void writeRevision(Revision revision) throws IOException {
		// add the revision to the current article
		if (handleArticle) {
			currentArticle.Revisions.add(revision);
		}
		// doc.addField(fieldMap.getTimestampField(),
		// Tools.formatTimestamp(revision.Timestamp));
		// doc.addField(fieldMap.getCommentField(), revision.Comment);
		// doc.addField(fieldMap.getTextField(), revision.Text);
		//
		// for (TextIndexer indexer : textIndexers) {
		// Map<String, Object> fields = indexer.indexText(revision.Text);
		// for (Map.Entry<String, Object> entry : fields.entrySet()) {
		// doc.addField(entry.getKey(), entry.getValue());
		// }
		// }
	}

	private void startArticleWorker() {
		// create an article worker using the current list of articles
		Runnable worker = new ArticleWorker(articles);
		// submit the article worker execution
		executor.submit(worker);
		// reset the article list
		articles = new LinkedList<>();
	}

	private void addSolrDocuments(List<SolrInputDocument> doc) {
		// solr complains if you add 0 documents 'missing content stream'
		if (doc.size() > 0 && !DEBUG) {
			try {
				UpdateResponse response = solrServer.add(doc);
				LOG.debug("Solr Add Response {}", response);
			} catch (SolrServerException | IOException e) {
				LOG.error("Problem sending documents to solr server", e);
			}
		}
	}

	private void commitSolrDocuments() {
		if (!DEBUG) {
			UpdateResponse response;
			try {
				response = solrServer.commit();
				LOG.debug("Solr Commit Response {}", response);
			} catch (SolrServerException | IOException e) {
				LOG.error("Problem commiting documents to solr server", e);
			}
		}
	}

	private class Article {
		private Page Page;
		private List<Revision> Revisions = new LinkedList<>();
	}

	private class ArticleWorker implements Runnable {
		private List<SolrInputDocument> solrInputDocuments;

		private List<Article> articles;

		public ArticleWorker(List<Article> articles) {
			solrInputDocuments = new LinkedList<>();
			this.articles = articles;
		}

		@Override
		public void run() {
			for (Article article : articles) {
				parseArticle(article);
			}

			addSolrDocuments(solrInputDocuments);
			for (SolrInputDocument doc : solrInputDocuments) {
				doc.clear();
			}
			solrInputDocuments.clear();

			for (Article a : articles) {
				for (Revision r : a.Revisions) {
					r.Text = "";
				}
				a.Revisions.clear();
			}
			articles.clear();
		}

		private void parseArticle(Article article) {
			SolrInputDocument doc = new SolrInputDocument();

			Page page = article.Page;
			doc.addField(fieldMap.getPageIdField(), page.Id);
			doc.addField(fieldMap.getTitleField(), page.Title.Text);
			doc.addField(fieldMap.getRedirectField(), page.isRedirect);
			doc.addField(fieldMap.getRestrictionsField(), page.Restrictions);

			List<Revision> revisions = article.Revisions;
			// should only be one revision at this time
			// since we're using the 'latest' version of
			// wikipedia dataset
			Revision revision = revisions.get(0);

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

			solrInputDocuments.add(doc);
		}
	}

}
