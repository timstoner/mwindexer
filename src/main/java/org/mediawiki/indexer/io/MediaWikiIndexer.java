package org.mediawiki.indexer.io;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;
import org.apache.lucene.analysis.wikipedia.WikipediaTokenizer;
import org.mediawiki.indexer.TextIndexer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MediaWikiIndexer implements TextIndexer {

	private static Logger LOG = LoggerFactory.getLogger(MediaWikiIndexer.class);

	/**
	 * A writer that takes errors
	 */
	private BufferedWriter errorLog;

	public Map<String, Object> indexText(String text) {
		Map<String, Object> fields = new HashMap<>();

		try {
			errorLog = new BufferedWriter(new FileWriter("index.txt"));
		} catch (IOException e) {
			LOG.error("problem opening file to write error log", e);
		}

		WikipediaTokenizer tf = new WikipediaTokenizer(new StringReader(text));

		CharTermAttribute charAtt = tf.addAttribute(CharTermAttribute.class);
		TypeAttribute typeAtt = tf.addAttribute(TypeAttribute.class);

		StringBuilder buffer = new StringBuilder();

		try {
			// reset the stream to clean state
			tf.reset();
			String tokText;
			String typeText;
			String msg;
			while (tf.incrementToken()) {
				tokText = charAtt.toString();
				typeText = typeAtt.type();
				msg = String.format("token: %s type: %s ", tokText, typeText);
				errorLog.write(msg);
				errorLog.write("\n");
				// LOG.debug();

				if (typeText.equals("<ALPHANUM>")) {
					buffer.append(tokText);
					buffer.append(" ");
				}
			}

//			LOG.debug(buffer.toString());

			tf.close();
		} catch (IOException e) {
			LOG.warn("problem reading text", e);
		}

		return fields;
	}
}
