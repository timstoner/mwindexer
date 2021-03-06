/*
 * MediaWiki import/export processing tools
 * Copyright 2005 by Brion Vibber
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 * $Id$
 */

package org.mediawiki.indexer.io;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.mediawiki.indexer.DumpWriter;
import org.mediawiki.indexer.model.Page;
import org.mediawiki.indexer.model.Revision;
import org.mediawiki.indexer.model.Siteinfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MultiWriter implements DumpWriter {
	private static Logger LOG = LoggerFactory.getLogger(MultiWriter.class);

	List<DumpWriter> writers;

	public MultiWriter() {
		writers = new LinkedList<DumpWriter>();
	}

	public void close() throws IOException {
		for (DumpWriter sink : writers) {
			sink.close();
		}
	}

	public void writeStartWiki() throws IOException {
		for (DumpWriter sink : writers) {
			sink.writeStartWiki();
		}
	}

	public void writeEndWiki() throws IOException {
		for (DumpWriter sink : writers) {
			sink.writeEndWiki();
		}
	}

	public void writeSiteinfo(Siteinfo info) throws IOException {
		for (DumpWriter sink : writers) {
			sink.writeSiteinfo(info);
		}
	}

	public void writeStartPage(Page page) throws IOException {
		for (DumpWriter sink : writers) {
			sink.writeStartPage(page);
		}
	}

	public void writeEndPage() throws IOException {
		for (DumpWriter sink : writers) {
			sink.writeEndPage();
		}
	}

	public void writeRevision(Revision revision) throws IOException {
		for (DumpWriter sink : writers) {
			sink.writeRevision(revision);
		}
	}

	public void add(DumpWriter sink) {
		writers.add(sink);
	}

	public void setWriters(List<DumpWriter> writers) {
		this.writers = writers;
	}
}
