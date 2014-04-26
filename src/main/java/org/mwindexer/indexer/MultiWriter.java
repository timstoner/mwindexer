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

package org.mwindexer.indexer;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.mwindexer.DumpWriter;
import org.mwindexer.model.Page;
import org.mwindexer.model.Revision;
import org.mwindexer.model.Siteinfo;

public class MultiWriter implements DumpWriter {
	List<DumpWriter> sinks;

	public MultiWriter() {
		sinks = new LinkedList<DumpWriter>();
	}

	public void close() throws IOException {
		for (DumpWriter sink : sinks) {
			sink.close();
		}
	}

	public void writeStartWiki() throws IOException {
		for (DumpWriter sink : sinks) {
			sink.writeStartWiki();
		}
	}

	public void writeEndWiki() throws IOException {
		for (DumpWriter sink : sinks) {
			sink.writeEndWiki();
		}
	}

	public void writeSiteinfo(Siteinfo info) throws IOException {
		for (DumpWriter sink : sinks) {
			sink.writeSiteinfo(info);
		}
	}

	public void writeStartPage(Page page) throws IOException {
		for (DumpWriter sink : sinks) {
			sink.writeStartPage(page);
		}
	}

	public void writeEndPage() throws IOException {
		for (DumpWriter sink : sinks) {
			sink.writeEndPage();
		}
	}

	public void writeRevision(Revision revision) throws IOException {
		for (DumpWriter sink : sinks) {
			sink.writeRevision(revision);
		}
	}

	public void add(DumpWriter sink) {
		sinks.add(sink);
	}

	public void addWriters(List<DumpWriter> writers) {
		sinks.addAll(writers);
	}
}
