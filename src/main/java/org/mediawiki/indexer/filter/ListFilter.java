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

package org.mediawiki.indexer.filter;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

import org.mediawiki.indexer.DumpWriter;
import org.mediawiki.indexer.model.Page;

public class ListFilter extends PageFilter {
	protected HashMap<String, String> list;
	private static final String TALK_PREFIX = "Talk:";

	public ListFilter(DumpWriter sink, String sourceFileName)
			throws IOException {
		super(sink);
		list = new HashMap<String, String>();
		BufferedReader input = new BufferedReader(new InputStreamReader(
				new FileInputStream(sourceFileName), "utf-8"));

		String line;
		while ((line = input.readLine()) != null) {
			if (!line.startsWith("#")) {
				String title = line.trim();
				title = title.replace("_", " ");
				if (title.startsWith(":"))
					title = line.substring(1);

				if (title.length() > 0)
					list.put(title, title);
			}
		}
		input.close();
	}

	protected boolean pass(Page page) {
		String title = page.Title;
		int index;

		if ((index = page.Title.indexOf(TALK_PREFIX)) != -1) {
			title = page.Title.substring(index + TALK_PREFIX.length());
		}

		return list.containsKey(title);
	}
}
