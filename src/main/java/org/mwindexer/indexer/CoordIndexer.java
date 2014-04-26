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
		List<SolrInputField> fields = new ArrayList<>();
		// "\\{\\{Coord.*\\}\\}";
		Pattern pattern = Pattern.compile(coordPattern,
				Pattern.CASE_INSENSITIVE);

		SolrInputField field = new SolrInputField(fieldName);

		Matcher matcher = pattern.matcher(text);
		while (matcher.matches()) {
			String group = matcher.group();
			LOG.debug("Coord Found: {}", group);
		}

		return fields;
	}

	public void setPattern(String pattern) {
		this.coordPattern = pattern;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
}
