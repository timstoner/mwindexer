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
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.mwindexer.DumpWriter;
import org.mwindexer.Tools;
import org.mwindexer.model.Contributor;
import org.mwindexer.model.Page;
import org.mwindexer.model.Revision;
import org.mwindexer.model.Siteinfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class XmlDumpReader extends DefaultHandler {

	private static Logger LOG = LoggerFactory.getLogger(XmlDumpReader.class);

	private InputStream inputStream;
	private DumpWriter dumpWriter;

	private char[] buffer;
	private int len;
	private boolean hasContent = false;
	private boolean deleted = false;

	Siteinfo siteinfo;
	Page page;
	boolean pageSent;
	Contributor contrib;
	Revision rev;
	int nskey;

	boolean abortFlag;

	/**
	 * Initialize a processor for a MediaWiki XML dump stream. Events are sent
	 * to a single DumpWriter output sink, but you can chain multiple output
	 * processors with a MultiWriter.
	 */
	public XmlDumpReader() {
		buffer = new char[4096];
		len = 0;
		hasContent = false;
	}

	/**
	 * Sets the input stream that this reader will read from
	 * 
	 * @param input
	 *            Stream to read XML from
	 */
	public void setInputStream(InputStream input) {
		this.inputStream = input;
	}

	/**
	 * Sets the dump writer that this reader will send processor events to
	 * 
	 * @param writer
	 *            a dump write to handle events
	 */
	public void setDumpWriter(DumpWriter writer) {
		this.dumpWriter = writer;
	}

	/**
	 * Reads through the entire XML dump on the input stream, sending events to
	 * the DumpWriter as it goes. May throw exceptions on invalid input or due
	 * to problems with the output.
	 * 
	 * @throws IOException
	 */
	public void readDump() throws IOException {
		try {
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser parser = factory.newSAXParser();

			parser.parse(inputStream, this);
		} catch (ParserConfigurationException e) {
			throw (IOException) new IOException(e.getMessage()).initCause(e);
		} catch (SAXException e) {
			throw (IOException) new IOException(e.getMessage()).initCause(e);
		}
		dumpWriter.close();
	}

	/**
	 * Request that the dump processing be aborted. At the next element, an
	 * exception will be thrown to stop the XML parser.
	 * 
	 * @fixme Is setting a bool thread-safe? It should be atomic...
	 */
	public void abort() {
		abortFlag = true;
	}

	public void startElement(String uri, String localname, String qName,
			Attributes attributes) throws SAXException {
		// Clear the buffer for character data; we'll initialize it
		// if and when character data arrives -- at that point we
		// have a length.
		len = 0;
		hasContent = false;

		if (abortFlag)
			throw new SAXException("XmlDumpReader set abort flag.");

		// check for deleted="deleted", and set deleted flag for the current
		// element.
		String d = attributes.getValue("deleted");
		deleted = (d != null && d.equals("deleted"));

		// frequent tags:
		switch (qName) {
		case "revision":
			openRevision();
			break;
		case "contributor":
			openContributor();
			break;
		case "page":
			openPage();
			break;
		case "mediawiki":
			openMediaWiki();
			break;
		case "siteinfo":
			openSiteinfo();
			break;
		case "namespaces":
			openNamespaces();
			break;
		case "namespace":
			openNamespace(attributes);
			break;
		}
	}

	public void characters(char[] ch, int start, int length) {
		if (buffer.length < len + length) {
			int maxlen = buffer.length * 2;
			if (maxlen < len + length)
				maxlen = len + length;
			char[] tmp = new char[maxlen];
			System.arraycopy(buffer, 0, tmp, 0, len);
			buffer = tmp;
		}
		System.arraycopy(ch, start, buffer, len, length);
		len += length;
		hasContent = true;
	}

	public void endElement(String uri, String localname, String qName)
			throws SAXException {
		switch (qName) {
		case "id":
			readId();
			break;
		case "revision":
			closeRevision();
			break;
		case "timestamp":
			readTimestamp();
			break;
		case "text":
			readText();
			break;
		case "contributor":
			closeContributor();
			break;
		case "username":
			readUsername();
			break;
		case "ip":
			readIp();
			break;
		case "comment":
			readComment();
			break;
		case "minor":
			readMinor();
			break;
		case "page":
			closePage();
			break;
		case "title":
			readTitle();
			break;
		case "restrictions":
			readRestrictions();
			break;
		case "redirect":
			readRedirect();
			break;
		case "mediawiki":
			closeMediaWiki();
			break;
		case "siteinfo":
			closeSiteinfo();
			break;
		case "base":
			readBase();
			break;
		case "generator":
			readGenerator();
			break;
		case "case":
			readCase();
			break;
		case "namespaces":
			closeNamespaces();
			break;
		case "namespace":
			closeNamespace();
			break;
		}

	}

	void openMediaWiki() {
		try {
			siteinfo = null;
			dumpWriter.writeStartWiki();
		} catch (IOException e) {
			LOG.warn("Problem", e);
		}
	}

	void closeMediaWiki() {
		try {
			dumpWriter.writeEndWiki();
			siteinfo = null;
		} catch (IOException e) {
			LOG.warn("Problem", e);
		}
	}

	// ------------------

	void openSiteinfo() {
		siteinfo = new Siteinfo();
	}

	void closeSiteinfo() {
		try {
			dumpWriter.writeSiteinfo(siteinfo);
		} catch (IOException e) {
			LOG.warn("Problem", e);
		}
	}

	private String bufferContentsOrNull() {
		if (!hasContent)
			return null;
		else
			return bufferContents();
	}

	private String bufferContents() {
		return len == 0 ? "" : new String(buffer, 0, len);
	}

	void readSitename() {
		siteinfo.Sitename = bufferContents();
	}

	void readBase() {
		siteinfo.Base = bufferContents();
	}

	void readGenerator() {
		siteinfo.Generator = bufferContents();
	}

	void readCase() {
		siteinfo.Case = bufferContents();
	}

	void openNamespaces() {
		siteinfo.Namespaces = new NamespaceSet();
	}

	void openNamespace(Attributes attribs) {
		nskey = Integer.parseInt(attribs.getValue("key"));
	}

	void closeNamespace() {
		siteinfo.Namespaces.add(nskey, bufferContents());
	}

	void closeNamespaces() {
		// NOP
	}

	// -----------

	void openPage() {
		page = new Page();
		pageSent = false;
	}

	void closePage() {
		try {
			if (pageSent)
				dumpWriter.writeEndPage();
			page = null;
		} catch (IOException e) {
			LOG.warn("Problem", e);
		}
	}

	void readTitle() {
		page.Title = bufferContents();
	}

	void readNs() {
		int ns = Integer.parseInt(bufferContents());
		page.Ns = ns;
	}

	void readId() {
		int id = Integer.parseInt(bufferContents());
		if (contrib != null)
			contrib.Id = id;
		else if (rev != null)
			rev.Id = id;
		else if (page != null)
			page.Id = id;
		else
			throw new IllegalArgumentException(
					"Unexpected <id> outside a <page>, <revision>, or <contributor>");
	}

	void readRedirect() {
		page.isRedirect = true;
	}

	void readRestrictions() {
//		page.Restrictions = bufferContents();
	}

	// ------

	void openRevision() {
		try {
			if (!pageSent) {
				dumpWriter.writeStartPage(page);
				pageSent = true;
			}

			rev = new Revision();
		} catch (IOException e) {
			LOG.warn("Problem", e);
		}
	}

	void closeRevision() {
		try {
			dumpWriter.writeRevision(rev);
			rev = null;
		} catch (IOException e) {
			LOG.warn("Problem", e);
		}
	}

	void readTimestamp() {
		rev.Timestamp = Tools.parseUTCTimestamp(bufferContents());
	}

	void readComment() {
		rev.Comment = bufferContentsOrNull();
		if (rev.Comment == null && !deleted)
			rev.Comment = ""; // NOTE: null means deleted/supressed
	}

	void readMinor() {
		rev.Minor = true;
	}

	void readText() {
		rev.Text = bufferContentsOrNull();
		if (rev.Text == null && !deleted)
			rev.Text = ""; // NOTE: null means deleted/supressed
	}

	// -----------
	void openContributor() {
		// XXX: record deleted flag?! as it is, any empty <contributor> tag
		// counts as "deleted"
		contrib = new Contributor();
	}

	void closeContributor() {
		// NOTE: if the contributor was supressed, nither username nor id have
		// been set in the Contributor object
		rev.Contributor = contrib;
		contrib = null;
	}

	void readUsername() {
		contrib.Username = bufferContentsOrNull();
	}

	void readIp() {
		contrib.Username = bufferContents();
		contrib.isIP = true;
	}
}
