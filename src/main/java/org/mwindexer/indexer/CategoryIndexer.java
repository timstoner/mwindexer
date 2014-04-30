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

public class CategoryIndexer implements TextIndexer {

	private static Logger LOG = LoggerFactory.getLogger(CategoryIndexer.class);

	private Pattern categoryPattern;

	private String fieldName;

	public CategoryIndexer(String pattern, String fieldName) {
		LOG.info("Category Indexer, {} {}", pattern, fieldName);
		categoryPattern = Pattern.compile(pattern);
		this.fieldName = fieldName;
	}

	@Override
	public Map<String, Object> indexText(String text) {
		Map<String, Object> fields = new HashMap<>();
		Matcher matcher = categoryPattern.matcher(text);
		List<String> groups = new LinkedList<>();
		String group;
		try {

			while (matcher.find()) {
				group = matcher.group();
//				LOG.debug("Category Found: {}", group);

				if (group.contains("[")) {
					// strip off braces
					// [[Category:Utica, New York| ]]
					group = group.substring(2, group.length() - 2);
					groups.add(group);
				}
			}
		} catch (Exception e) {
			LOG.warn("Problem parsing text: " + text, e);
			System.exit(-1);
		}

		List<String> categories = parseCategoryGroups(groups);
		fields.put(fieldName, categories);
		return fields;
	}

	private List<String> parseCategoryGroups(List<String> groups) {
		List<String> categories = new LinkedList<>();

		int start;
		int end;
		String category;
		for (String group : groups) {
			start = group.indexOf(":");
			end = group.indexOf("|");
			if (end == -1) {
				end = group.length();
			}

			// strip off the Category:
			// strip off anything after |
			category = group.substring(start + 1, end);
			LOG.debug("Category {}", category);
			categories.add(category);
		}

		return categories;
	}

}
