package org.mwindexer.test;

import java.io.IOException;

import org.mwindexer.DumpWriter;
import org.mwindexer.model.Page;
import org.mwindexer.model.Revision;
import org.mwindexer.model.Siteinfo;

public class MockDumpWriter implements DumpWriter {

	private boolean pagePassed = false;

	private boolean revisionPassed = false;

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

	}

	@Override
	public void writeEndPage() throws IOException {
		pagePassed = true;
	}

	@Override
	public void writeRevision(Revision revision) throws IOException {
		revisionPassed = true;
	}

	public boolean isPagePassed() {
		return pagePassed;
	}

	public boolean isRevisionPassed() {
		return revisionPassed;
	}

}
