/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.nio.channels;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import org.apache.log4j.Logger;

/**
 * file Channel for remote file via http.
 * requires range request(206)
 * @author taktod
 */
public class URLFileReadChannel implements IFileReadChannel {
	/** logger */
	private static final Logger logger = Logger.getLogger(URLFileReadChannel.class);
	/** target URI */
	private final URL url;
	/** connection */
	private HttpURLConnection conn;
	/** size information */
	private final int size;
	/** range request start position */
	private int startPos;
	/** read data size */
	private int readSize;
	/** open flag */
	private boolean isOpen;
	/** target channel */
	private ReadableByteChannel channel;
	/**
	 * constructor
	 * @param urlString
	 * @throws IOException
	 */
	public URLFileReadChannel(String urlString) throws IOException {
		this(urlString, 0);
	}
	/**
	 * constructor with position
	 * if position is out of range request, throws Exception
	 * @param urlString
	 * @param position
	 * @throws IOException
	 */
	public URLFileReadChannel(String urlString, int position) throws IOException {
		url = new URL(urlString);
		openConnection(position);
		// try to take contentLength
		int size = conn.getContentLength();
		if(size < 0) {
			// in the case of over 2G file.
			// try to take from header field.
			long lsize = Long.parseLong(conn.getHeaderField("Content-Length"));
			if(lsize > Integer.MAX_VALUE) {
				size = Integer.MAX_VALUE;
			}
		}
		this.size = size;
	}
	/**
	 * open connection
	 * @param position
	 * @throws IOException
	 */
	private void openConnection(int position) throws IOException {
		if(size == 0 || position < size) {
			URLConnection urlConn = url.openConnection();
			if(!(urlConn instanceof HttpURLConnection)) {
				logger.error("connection is not via http");
				throw new IOException("connection is not http");
			}
			conn = (HttpURLConnection)urlConn;
			conn.setRequestMethod("GET");
			conn.setAllowUserInteraction(false);
			conn.setInstanceFollowRedirects(true);
			// if position is not 0, use range request.
			if(position != 0) {
				conn.addRequestProperty("Range", "bytes=" + position + "-");
			}
			// access as iPhone
//			conn.setRequestProperty("User-Agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 6_1 like Mac OS X; ja-jp) AppleWebKit/536.26 (KHTML, like Gecko) CriOS/23.0.1271.100 Mobile/10B142 Safari/8536.25");
			// access as chrome
			conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/28.0.1500.63 Safari/537.36");
			// let the server know proxy access.
			conn.setRequestProperty("Via", "1.1(jetty)");
			conn.connect();
			isOpen = true;
			channel = Channels.newChannel(conn.getInputStream());
		}
		else {
			isOpen = false;
			if(position > size) {
				logger.error("out of range for http.");
				throw new IOException("out of range for http");
			}
		}
		// update information
		startPos = position;
		readSize = 0;
	}
	/**
	 * close connection
	 */
	private void closeConnection() {
		conn.disconnect();
		isOpen = false;
	}
	/**
	 * {@inheritDoc}
	 */
	public boolean isOpen() {
		return isOpen;
	}
	/**
	 * {@inheritDoc}
	 */
	public int read(ByteBuffer dst) throws IOException {
		int startPos = dst.position();
		channel.read(dst);
		int readSize = dst.position() - startPos;
		this.readSize += readSize;
		return readSize;
	}
	/**
	 * response contents type.
	 * @return
	 */
	public String getContentType() {
		return conn.getContentType();
	}
	/**
	 * {@inheritDoc}
	 */
	public void close() throws IOException {
		closeConnection();
	}
	/**
	 * {@inheritDoc}
	 */
	public int position() {
		return startPos + readSize;
	}
	/**
	 * {@inheritDoc}
	 */
	public URLFileReadChannel position(int newPosition) throws IOException {
		if(newPosition == size) {
			readSize = newPosition - startPos;
		}
		if(!isOpen) {
			return this;
		}
		// calcurate skip size.
		long skipSize = newPosition - startPos - readSize;
		if(skipSize == 0) { // noskip
			return this;
		}
		// skip size is already downloaded.
		if(skipSize > 0 && conn.getInputStream().available() > skipSize) {
			readSize += skipSize;
			conn.getInputStream().skip(skipSize);
		}
		else { // skip size is not downloaded yet or rewind. use range connection to get data.
			logger.info("to change position, need to re-connect http.");
			closeConnection();
			openConnection(newPosition);
		}
		return this;
	}
	/**
	 * {@inheritDoc}
	 * MEMO For dynamic connection(like php site.). there is no size response. in this case size is -1.  
	 */
	public int size() {
		return size;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getUri() {
		return url.toString();
	}
}
