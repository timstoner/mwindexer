package org.mwindexer;

import java.io.IOException;
import java.text.MessageFormat;

import org.mwindexer.indexer.DumpWriter;
import org.mwindexer.indexer.Page;
import org.mwindexer.indexer.PageFilter;
import org.mwindexer.indexer.Revision;

public class ProgressFilter extends PageFilter {
	int pages = 0;
	int revisions = 0;
	int interval = 1000;
	MessageFormat format = new MessageFormat(
			"{0} pages ({1}/sec), {2} revs ({3}/sec)");
	long start = System.currentTimeMillis();

	public ProgressFilter(DumpWriter sink, int interval) {
		super(sink);
		this.interval = interval;
		if (interval <= 0)
			throw new IllegalArgumentException(
					"Reporting interval must be positive.");
	}

	public void writeStartPage(Page page) throws IOException {
		super.writeStartPage(page);
		pages++;
	}

	public void writeRevision(Revision rev) throws IOException {
		super.writeRevision(rev);
		revisions++;
		reportProgress();
	}

	/**
	 * If we didn't just show a progress report on the last revision, show the
	 * final results.
	 * 
	 * @throws IOException
	 */
	public void writeEndWiki() throws IOException {
		super.writeEndWiki();
		if (revisions % interval != 0)
			showProgress();
	}

	private void reportProgress() {
		if (revisions % interval == 0)
			showProgress();
	}

	private void showProgress() {
		long delta = System.currentTimeMillis() - start;
		sendOutput(format.format(new Object[] { new Integer(pages),
				rate(delta, pages), new Integer(revisions),
				rate(delta, revisions) }));
	}

	protected void sendOutput(String text) {
		System.err.println(text);
	}

	private static Object rate(long delta, int count) {
		return (delta > 0.001) ? (Object) new Double(1000.0 * (double) count
				/ (double) delta) : (Object) "-";
	}
}