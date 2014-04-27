package org.mwindexer.indexer;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.mwindexer.TextIndexer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CoordIndexer implements TextIndexer {

	private static Logger LOG = LoggerFactory.getLogger(CoordIndexer.class);

	/**
	 * The Regular Expression pattern that finds Coord templates
	 */
	private String coordPattern;

	/**
	 * The Solr Schema Field Name that values will be indexed at
	 */
	private String fieldName;

	private Pattern pattern;

	public CoordIndexer(String coordPattern, String fieldName) {
		this.coordPattern = coordPattern;
		this.fieldName = fieldName;

		LOG.debug("Pattern: {}", coordPattern);
		// compile the pattern in the constructor for performance
		pattern = Pattern.compile(coordPattern, Pattern.CASE_INSENSITIVE);
	}

	@Override
	public Map<String, Object> indexText(String text) {
		Map<String, Object> fields = new HashMap<>();

		LOG.debug(text);

		Matcher matcher = pattern.matcher(text);
		String group;
		String coord;
		String[] split;
		while (matcher.matches()) {
			group = matcher.group();
			LOG.debug("Coord Found: {}", group);

			// strip off brackets
			coord = group.substring(2, group.length() - 2);
			LOG.debug("Coord Found: {}", group);

			// tokenizer
			split = coord.split("\\|");
			for (String value : split) {
				LOG.debug(value);
			}
		}

		return fields;
	}

}
