/*
 *Homework 3
 *RequestParams.java
 *Marcos Brenes, Dongdong Li 
 */

package com.example.homework3;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;

public class RequestParams {
	
	String method, baseUrl;
	HashMap<String, String> params = new HashMap<String, String>();
	
	public void addParams(String key, String value) {
		params.put(key, value);
	}

	public RequestParams(String method, String baseUrl) {
		super();
		this.method = method;
		this.baseUrl = baseUrl;
	}
	
	public String getEncodedParams() {
		// loop over keyvalue pairs of the params
		// append to a string builder key=value
		// figure out how to add the &
		// params.php?param1=value1%20value1&param2=value2
		
		StringBuilder sb = new StringBuilder();
		for (String key: params.keySet()) {
			try {
				String value = URLEncoder.encode(params.get(key), "UTF-8");
				
				if (sb.length() > 0) sb.append("&");
				sb.append(key + "=" + value);		
				
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return sb.toString();
	}
	
	public String getEncodedURL() {
		return this.baseUrl + "?" + getEncodedParams();
	}
	
	public HttpURLConnection setupConnection() throws IOException {
		if (method.equals("GET")) {
			URL url = new URL(getEncodedURL());
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			return connection;
			
		} else { // method.equals("POST")
			URL url = new URL(this.baseUrl);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			connection.setDoOutput(true);
			
			OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
			writer.write(getEncodedParams());
			writer.flush();
			
			return connection;
		}
	}
}
