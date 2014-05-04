package org.mediawiki.indexer.io;

import java.util.HashMap;
import java.util.Map;

public class SolrFieldMap {

	private Map<String, String> fieldMap;

	private static final String PAGE_ID = "page_id";
	private static final String TITLE = "title";
	private static final String NAMESPACE = "namespace";
	private static final String REVISION_ID = "revision_id";
	private static final String PARENT_ID = "parent_id";
	private static final String CONTRIBUTOR_USERNAME = "contributer_username";
	private static final String CONTRIBUTOR_ID = "contributor_id";
	private static final String REDIRECT = "redirect";
	private static final String RESTRICTIONS = "restrictions";
	private static final String TIMESTAMP = "timestamp";
	private static final String COMMENT = "comment";
	private static final String TEXT = "text";
	private static final String MODEL = "model";
	private static final String FORMAT = "format";

	public SolrFieldMap() {
		fieldMap = new HashMap<>();
	}

	public void setTitleField(String fieldName) {
		fieldMap.put(TITLE, fieldName);
	}

	public String getTitleField() {
		return fieldMap.get(TITLE);
	}

	public void setPageIdField(String field) {
		fieldMap.put(PAGE_ID, field);
	}

	public String getPageIdField() {
		return fieldMap.get(PAGE_ID);
	}

	public void setRestrictionsField(String field) {
		fieldMap.put(RESTRICTIONS, field);
	}

	public String getRestrictionsField() {
		return fieldMap.get(RESTRICTIONS);
	}

	public void setRedirectField(String field) {
		fieldMap.put(REDIRECT, field);
	}

	public String getRedirectField() {
		return fieldMap.get(REDIRECT);
	}

	public void setNamespaceField(String field) {
		fieldMap.put(NAMESPACE, field);
	}

	public String getNamespaceField() {
		return fieldMap.get(NAMESPACE);
	}

	public void setRevisionIdField(String field) {
		fieldMap.put(REVISION_ID, field);
	}

	public String getRevisionIdField() {
		return fieldMap.get(REVISION_ID);
	}

	public void setParentIdField(String field) {
		fieldMap.put(PARENT_ID, field);
	}

	public String getParentIdField() {
		return fieldMap.get(PARENT_ID);
	}

	public void setTimestampField(String field) {
		fieldMap.put(TIMESTAMP, field);
	}

	public String getTimestampField() {
		return fieldMap.get(TIMESTAMP);
	}

	public void setContributorUsernameField(String field) {
		fieldMap.put(CONTRIBUTOR_USERNAME, field);
	}

	public String getContributorUsernameField() {
		return fieldMap.get(CONTRIBUTOR_USERNAME);
	}

	public void setContributorIdField(String field) {
		fieldMap.put(CONTRIBUTOR_ID, field);
	}

	public String getContributorIdField() {
		return fieldMap.get(CONTRIBUTOR_ID);
	}

	public void setCommentField(String field) {
		fieldMap.put(COMMENT, field);
	}

	public String getCommentField() {
		return fieldMap.get(COMMENT);
	}

	public void setTextField(String field) {
		fieldMap.put(TEXT, field);
	}

	public String getTextField() {
		return fieldMap.get(TEXT);
	}

	public void setModelField(String field) {
		fieldMap.put(MODEL, field);
	}

	public String getModelField() {
		return fieldMap.get(MODEL);
	}

	public void setFormatField(String field) {
		fieldMap.put(FORMAT, field);
	}

	public String getFormatField() {
		return fieldMap.get(FORMAT);
	}
}
