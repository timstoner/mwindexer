package org.mwindexer.indexer;

import java.util.HashMap;
import java.util.Map;

import org.mwindexer.TextIndexer;

public class CategoryIndexer implements TextIndexer {

	@Override
	public Map<String, Object> indexText(String text) {
		return new HashMap<String, Object>();
	}

}
