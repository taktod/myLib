/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.nio.channels;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;

/**
 * read channel for local file system.
 * @author taktod
 */
public class FileReadChannel implements IFileReadChannel {
	/** path */
	private final String path;
	/** file stream */
	private FileInputStream stream = null;
	/**
	 * constructor
	 * @param target
	 */
	public FileReadChannel(String fileString) throws IOException {
		this(fileString, 0);
	}
	/**
	 * constructor with position
	 * @param fileString
	 * @param position
	 * @throws Exception
	 */
	public FileReadChannel(String fileString, int position) throws IOException {
		path = fileString;
		File file = new File(fileString);
		stream = new FileInputStream(file);
		position(position);
	}
	/**
	 * constructor with File
	 * @param file
	 * @throws Exception
	 */
	public FileReadChannel(File file) throws IOException {
		this(file, 0);
	}
	/**
	 * constructor width File and position
	 * @param file
	 * @param position
	 * @throws FileNotFoundException 
	 * @throws Exception
	 */
	public FileReadChannel(File file, int position) throws IOException {
		path = file.getAbsolutePath();
		stream = new FileInputStream(file);
		position(position);
	}
	/**
	 * {@inheritDoc}
	 */
	public void close() throws IOException {
		stream.close();
	}
	/**
	 * {@inheritDoc}
	 */
	public boolean isOpen() {
		return stream.getChannel().isOpen();
	}
	/**
	 * {@inheritDoc}
	 */
	public int read(ByteBuffer dst) throws IOException {
		return stream.getChannel().read(dst);
	}
	/**
	 * {@inheritDoc}
	 */
	public int position() throws IOException {
		if(!isOpen()) {
			throw new IOException("file is closed.");
		}
		return (int)stream.getChannel().position();
	}
	/**
	 * {@inheritDoc}
	 */
	public FileReadChannel position(int newPosition) throws IOException {
		if(!isOpen()) {
			throw new IOException("file is closed.");
		}
		stream.getChannel().position(newPosition);
		return this;
	}
	/**
	 * {@inheritDoc}
	 */
	public int size() throws IOException {
		if(stream.getChannel().size() > Integer.MAX_VALUE) {
			return Integer.MAX_VALUE;
		}
		return (int)stream.getChannel().size();
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getUri() {
		return path;
	}
	/**
	 * get read channel from uri string
	 * @param uri
	 * @return
	 * @throws Exception
	 */
	public static IFileReadChannel openFileReadChannel(String uri) throws Exception {
		if(uri.startsWith("http")) {
			return new URLFileReadChannel(uri);
		}
		else {
			return new FileReadChannel(uri);
		}
	}
	/**
	 * get read channel from uri string and position
	 * @param uri
	 * @param position
	 * @return
	 * @throws Exception
	 */
	public static IFileReadChannel openFileReadChannel(String uri, int position) throws Exception {
		if(uri.startsWith("http")) {
			return new URLFileReadChannel(uri, position);
		}
		else {
			return new FileReadChannel(uri, position);
		}
	}
	/**
	 * get read channel from url object
	 * @param url
	 * @return
	 * @throws Exception
	 */
	public static IFileReadChannel openFileReadChannel(URL url) throws Exception {
		try {
			File file = new File(url.toURI());
			return new FileReadChannel(file);
		}
		catch(IllegalArgumentException e) {
			return new URLFileReadChannel(url.toString());
		}
	}
	/**
	 * get read channel from url object and position
	 * @param url
	 * @param position
	 * @return
	 * @throws Exception
	 */
	public static IFileReadChannel openFileReadChannel(URL url, int position) throws Exception {
		try {
			File file = new File(url.toURI());
			return new FileReadChannel(file, position);
		}
		catch(IllegalArgumentException e) {
			return new URLFileReadChannel(url.toString(), position);
		}
	}
}
