package org.mwindexer.indexer;

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
	 * The Solr Schema Field Name that values will be indexed at
	 */
	private String fieldName;

	private Pattern coordPattern;

	public CoordIndexer(String pattern, String fieldName) {
		this.fieldName = fieldName;

		LOG.debug("Pattern: {}", pattern);
		// compile the pattern in the constructor for performance
		coordPattern = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
	}

	@Override
	public Map<String, Object> indexText(String text) {
		Map<String, Object> fields = new HashMap<>();
		List<String> groups = new LinkedList<>();

		Matcher matcher = coordPattern.matcher(text);
		String group;

		// {{Coord|display=title|43.096569|-75.231887}}

		int index;
		while (matcher.find()) {
			group = matcher.group();
			LOG.debug("Coord Found: {}", group);

			// strip off brackets
			group = group.substring(2, group.length() - 2);

			index = group.indexOf("{{");
			if (index != -1) {
				// truncate anything after the brackets
				group = group.substring(0, index);
			}

			groups.add(group);
		}

		List<String> latLons = parseCoordMatches(groups);
		fields.put(fieldName, latLons);

		return fields;
	}

	private List<String> parseCoordMatches(List<String> matches) {
		List<String> latLons = new LinkedList<>();

		match: for (String match : matches) {
			boolean missing = checkForMissing(match);
			if (missing) {
				// most likely {{coord missing...}}
				continue;
			}

			// LOG.info(match);

			List<String> buffer = convertTemplateToBuffer(match);

			if (buffer.size() % 2 != 0) {
				LOG.warn("Malformed Coord: {}", match);
				continue;
			}

			if (buffer.size() > 8) {
				LOG.warn("Unexpected buffer size {} {}", buffer.size(), match);
				continue;
			}

			for (String b : buffer) {
				if (b.equalsIgnoreCase("LAT") || b.equalsIgnoreCase("LON")) {
					// if this match has a LAT/LON placeholder, continue onto
					// the next match
					continue match;
				}
			}

			// printBuffer(buffer);

			int midpoint = buffer.size() / 2;

			try {
				double latitude = buildLatitudeCoordinate(new LinkedList<>(
						buffer.subList(0, midpoint)));
				double longitude = buildLongitudeCoordinate(new LinkedList<>(
						buffer.subList(midpoint, buffer.size())));

				String coord = latitude + "," + longitude;
				LOG.debug("Coord: {}", coord);

				latLons.add(coord);
			} catch (Exception e) {
				LOG.warn("Invalid Coordinates match:" + match, e);
			}
		}

		return latLons;
	}

	private boolean checkForMissing(String match) {
		boolean missing = false;
		match = match.toLowerCase();
		if (match.startsWith("coord missing")
				|| match.startsWith("coord unknown")) {
			missing = true;
		}

		return missing;
	}

	private List<String> convertTemplateToBuffer(String template) {
		String[] elements = template.split("\\|");
		List<String> buffer = new LinkedList<>();
		boolean add = true;
		for (String element : elements) {
			if (element.equalsIgnoreCase("coord") || element.contains("=")
					|| element.contains(":")) {
				add = false;
			} else {
				add = true;
			}

			if (add) {
				buffer.add(element);
			}
		}
		return buffer;
	}

	private double buildLatitudeCoordinate(List<String> buffer) {
		boolean negate = false;
		int lastIndex = buffer.size() - 1;
		String lastElement = buffer.get(lastIndex);
		lastElement = lastElement.trim();

		if (lastElement.equals("S")) {
			negate = true;
		}

		// remove trailing cardinal direction
		if (lastElement.equals("N") || lastElement.equals("S")) {
			buffer.remove(lastIndex);
		}

		double coord = buildCoordinate(buffer, negate);
		return coord;
	}

	private double buildLongitudeCoordinate(List<String> buffer) {
		boolean negate = false;
		int lastIndex = buffer.size() - 1;
		String lastElement = buffer.get(lastIndex);
		lastElement = lastElement.trim();

		if (lastElement.equals("W")) {
			negate = true;
		}

		// remove trailing cardinal direction
		if (lastElement.equals("E") || lastElement.equals("W")) {
			buffer.remove(lastIndex);
		}

		double coord = buildCoordinate(buffer, negate);
		return coord;
	}

	private double buildCoordinate(List<String> buffer, boolean negate) {
		double coord = 0;
		// convert seconds to fraction of minute
		if (buffer.size() > 2) {
			coord += Double.parseDouble(buffer.get(2)) / 60;
		}

		// add in seconds fraction and convert to fraction of degree
		if (buffer.size() > 1) {
			coord = (coord + Double.parseDouble(buffer.get(1))) / 60;
		}

		// add fraction of degree to the degrees
		coord += Double.parseDouble(buffer.get(0));

		if (negate) {
			coord = -coord;
		}

		return coord;
	}

	private void printBuffer(List<String> buffer) {
		StringBuilder sb = new StringBuilder();
		for (String b : buffer) {
			sb.append(b).append(" ");
		}
		LOG.info(sb.toString());
	}
}
