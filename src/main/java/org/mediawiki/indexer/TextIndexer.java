package org.mediawiki.indexer;

import java.util.Map;

public interface TextIndexer {

	public Map<String, Object> indexText(String text);
}
