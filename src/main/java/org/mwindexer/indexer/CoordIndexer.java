package org.mwindexer.indexer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
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

		Matcher matcher = pattern.matcher(text);
		String group;
		String coord;
		String[] split;

		List<String> coordMatches = new LinkedList<>();

		// {{Coord|display=title|43.096569|-75.231887}}

		while (matcher.matches()) {
			group = matcher.group();
			LOG.debug("Coord Found: {}", group);

			// strip off brackets
			coord = group.substring(2, group.length() - 2);
			coordMatches.add(coord);
		}

		List<String> latLons = parseCoordMatches(coordMatches);

		return fields;
	}

	private List<String> parseCoordMatches(List<String> matches) {
		List<String> latLons = new LinkedList<>();

		for (String match : matches) {
			List<String> buffer = new ArrayList<>();
			String[] elements = match.split("\\|");
			boolean add = true;
			for (String element : elements) {
				if (element.equalsIgnoreCase("coord")) {
					add = false;
				} else if (element.contains("=") || element.contains(":")) {
					add = false;
				} else {
					add = true;
				}

				if (add) {
					buffer.add(element);
				}
			}
		}

		return latLons;
	}

}
