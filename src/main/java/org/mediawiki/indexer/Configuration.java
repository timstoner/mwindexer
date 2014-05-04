package org.mediawiki.indexer;

import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class Configuration {

	private static Logger LOG = LoggerFactory.getLogger(Configuration.class);

	private InputStream inputStream;

	private String solrURL;
	
	public Configuration() {
		LOG.debug("Config init");
	}

	@Bean
	public InputStream getInputStream() {
		LOG.debug("getInputStream");

		return inputStream;
	}

	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}

	@Bean
	public String getSolrURL() {
		LOG.debug("getSolrURL");
		return solrURL;
	}

	public void setSolrURL(String url) {
		this.solrURL = url;
	}

}
