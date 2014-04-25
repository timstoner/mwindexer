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

package org.mwindexer.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mwindexer.indexer.NamespaceSet;
import org.mwindexer.model.Title;

public class TitleTest {
	NamespaceSet namespaces;

	@Before
	public void setUp() throws Exception {
		namespaces = new NamespaceSet();
		namespaces.add(-2, "Media");
		namespaces.add(-1, "Special");
		namespaces.add(0, "");
		namespaces.add(1, "Talk");
		namespaces.add(2, "User");
		namespaces.add(3, "User talk");
		namespaces.add(4, "Project");
		namespaces.add(5, "Project talk");
		namespaces.add(6, "Image");
		namespaces.add(7, "Image talk");
		namespaces.add(8, "MediaWiki");
		namespaces.add(9, "MediaWiki talk");
		namespaces.add(10, "Template");
		namespaces.add(11, "Template talk");
		namespaces.add(12, "Help");
		namespaces.add(13, "Help talk");
		namespaces.add(14, "Category");
		namespaces.add(15, "Category talk");
	}

	@After
	public void tearDown() throws Exception {
		namespaces = null;
	}

	private class TestItem {
		public int ns;
		public String text;
		public String prefixed;

		TestItem(int ns, String text, String prefixed) {
			this.ns = ns;
			this.text = text;
			this.prefixed = prefixed;
		}

		public String toString() {
			return "(" + ns + ",\"" + text + "\") [[" + prefixed + "]]";
		}
	}

	TestItem[] tests = { new TestItem(0, "Page title", "Page title"),
			new TestItem(1, "Page title", "Talk:Page title"),
			new TestItem(-1, "Recentchanges", "Special:Recentchanges"),
			new TestItem(13, "Logging in", "Help talk:Logging in"),
			new TestItem(0, "2001: A Space Odyssey", "2001: A Space Odyssey"),
			new TestItem(0, "2:2", "2:2") };

	/*
	 * Test method for 'org.mediawiki.importer.Title.Title(int, String,
	 * NamespaceSet)'
	 */
	@Test
	public void testTitleIntStringNamespaceSet() {
		for (int i = 0; i < tests.length; i++) {
			Title title = new Title(new Integer(tests[i].ns), tests[i].text,
					namespaces);
			assertEquals(tests[i].toString(), tests[i].prefixed,
					title.toString());
		}
	}

	/*
	 * Test method for 'org.mediawiki.importer.Title.Title(String,
	 * NamespaceSet)'
	 */
	@Test
	public void testTitleStringNamespaceSet() {
		for (int i = 0; i < tests.length; i++) {
			Title title = new Title(tests[i].prefixed, namespaces);
			assertEquals(tests[i].toString(), tests[i].ns,
					title.Namespace.intValue());
			assertEquals(tests[i].toString(), tests[i].text, title.Text);
		}
	}

	/*
	 * Test method for 'org.mediawiki.importer.Title.toString()'
	 */
	@Test
	public void testToString() {
		for (int i = 0; i < tests.length; i++) {
			Title title = new Title(tests[i].prefixed, namespaces);
			assertEquals(tests[i].toString(), tests[i].prefixed,
					title.toString());
		}
	}

	/*
	 * Test method for 'org.mediawiki.importer.Title.isSpecial()'
	 */
	@Test
	public void testIsSpecial() {
		for (int i = 0; i < tests.length; i++) {
			Title title = new Title(tests[i].prefixed, namespaces);
			if (tests[i].ns < 0)
				assertTrue(tests[i].toString(), title.isSpecial());
			else
				assertFalse(tests[i].toString(), title.isSpecial());
		}
	}

	/*
	 * Test method for 'org.mediawiki.importer.Title.isTalk()'
	 */
	@Test
	public void testIsTalk() {
		for (int i = 0; i < tests.length; i++) {
			Title title = new Title(tests[i].prefixed, namespaces);
			if (title.isSpecial())
				assertFalse(tests[i].toString(), title.isTalk());
			else if (tests[i].ns % 2 == 0)
				assertFalse(tests[i].toString(), title.isTalk());
			else
				assertTrue(tests[i].toString(), title.isTalk());
		}
	}

	/*
	 * Test method for 'org.mediawiki.importer.Title.talkPage()'
	 */
	@Test
	public void testTalkPage() {
		for (int i = 0; i < tests.length; i++) {
			Title title = new Title(tests[i].prefixed, namespaces);
			if (title.isTalk())
				assertEquals(tests[i].toString(), title, title.talkPage());
			else if (title.isSpecial())
				assertNull(tests[i].toString(), title.talkPage());
			else
				assertFalse(tests[i].toString(), title.equals(title.talkPage()));
		}
	}

	/*
	 * Test method for 'org.mediawiki.importer.Title.subjectPage()'
	 */
	@Test
	public void testSubjectPage() {
		for (int i = 0; i < tests.length; i++) {
			Title title = new Title(tests[i].prefixed, namespaces);
			if (title.isTalk())
				assertNotSame(tests[i].toString(), title, title.subjectPage());
			else
				assertSame(tests[i].toString(), title, title.subjectPage());
		}
	}

	@Test
	public void testTalkSubjectPage() {
		for (int i = 0; i < tests.length; i++) {
			Title title = new Title(tests[i].prefixed, namespaces);
			if (title.isTalk())
				assertEquals(tests[i].toString(), title, title.subjectPage()
						.talkPage());
			else if (title.isSpecial())
				assertNull(tests[i].toString(), title.subjectPage().talkPage());
			else
				assertEquals(tests[i].toString(), title, title.talkPage()
						.subjectPage());
		}
	}

}
