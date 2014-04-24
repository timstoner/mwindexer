package org.mwindexer;

import java.io.InputStream;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class Configuration {

	private InputStream inputStream;
	
	private String solrURL;

	@Bean
	public InputStream getInputStream() {
		return inputStream;
	}

	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}
	
	@Bean
	public String getSolrURL() {
		return solrURL;
	}
	
	public void setSolrURL(String url) {
		this.solrURL = url;
	}

}
