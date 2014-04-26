package org.mwindexer;

import java.util.List;

import org.apache.solr.common.SolrInputField;

public interface TextIndexer {

	public List<SolrInputField> indexText(String text);
}
