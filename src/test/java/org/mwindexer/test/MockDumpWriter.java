package org.mwindexer.test;

import java.io.IOException;

import org.mediawiki.indexer.DumpWriter;
import org.mediawiki.indexer.model.Page;
import org.mediawiki.indexer.model.Revision;
import org.mediawiki.indexer.model.Siteinfo;

public class MockDumpWriter implements DumpWriter {

	private boolean pagePassed = false;

	private boolean revisionPassed = false;

	private Page page;

	private Revision revision;

	@Override
	public void close() throws IOException {

	}

	@Override
	public void writeStartWiki() throws IOException {

	}

	@Override
	public void writeEndWiki() throws IOException {

	}

	@Override
	public void writeSiteinfo(Siteinfo info) throws IOException {

	}

	@Override
	public void writeStartPage(Page page) throws IOException {
		this.page = page;
	}

	@Override
	public void writeEndPage() throws IOException {
		pagePassed = true;
	}

	@Override
	public void writeRevision(Revision revision) throws IOException {
		revisionPassed = true;
		this.revision = revision;
	}

	public boolean isPagePassed() {
		return pagePassed;
	}

	public boolean isRevisionPassed() {
		return revisionPassed;
	}

	public Page getPage() {
		return page;
	}

	public void setPage(Page page) {
		this.page = page;
	}

	public Revision getRevision() {
		return revision;
	}

	public void setRevision(Revision revision) {
		this.revision = revision;
	}

}
