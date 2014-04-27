package org.mwindexer.indexer;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.solr.common.SolrInputField;
import org.mwindexer.TextIndexer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CoordIndexer implements TextIndexer {

	private static Logger LOG = LoggerFactory.getLogger(CoordIndexer.class);

	private String coordPattern;

	private String fieldName;

	@Override
	public List<SolrInputField> indexText(String text) {
		// LOG.debug(text);
		LOG.debug(coordPattern);
		List<SolrInputField> fields = new ArrayList<>();
		// "\\{\\{Coord.*\\}\\}";
		Pattern pattern = Pattern.compile(coordPattern,
				Pattern.CASE_INSENSITIVE);

		SolrInputField field = new SolrInputField(fieldName);

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

	public void setPattern(String pattern) {
		this.coordPattern = pattern;
	}

	public String getPattern() {
		return coordPattern;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getFieldName() {
		return fieldName;
	}
}
