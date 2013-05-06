package com.ttProject.nio.channels;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class FileReadChannel implements IFileReadChannel {
	private FileChannel channel;
	/**
	 * コンストラクタ
	 * @param target
	 */
	public FileReadChannel(String fileString) throws Exception {
		File file = new File(fileString);
		channel = new FileInputStream(file).getChannel();
	}
	public FileReadChannel(File file) throws Exception {
//		this.file = file;
		channel = new FileInputStream(file).getChannel();
	}
/*	public FileReadChannel(URL url) throws Exception {
		file = new File(url.toURI());
		channel = new FileInputStream(file).getChannel();
	}*/
	public void close() throws IOException {
		channel.close();
	}
	public boolean isOpen() {
		return channel.isOpen();
	}
	public int read(ByteBuffer dst) throws IOException {
		return channel.read(dst);
	}
	public int position() throws Exception {
		if(!isOpen()) {
			throw new IOException("file is closed.");
		}
		return (int)channel.position();
	}
	public FileReadChannel position(int newPosition) throws Exception {
		if(!isOpen()) {
			throw new IOException("file is closed.");
		}
		channel.position(newPosition);
		return this;
	}
	public int size() throws Exception {
		return (int)channel.size();
	}
	public static IFileReadChannel openFileReadChannel(String uri) throws Exception {
		if(uri.startsWith("http")) {
			return new URLFileReadChannel(uri);
		}
		else {
			return new FileReadChannel(uri);
		}
	}
	public static IFileReadChannel openFileReadChannel(URL url) throws Exception {
		try {
			File file = new File(url.toURI());
			return new FileReadChannel(file);
		}
		catch(IllegalArgumentException e) {
			// urlの場合
			return new URLFileReadChannel(url.toString());
		}
	}
}
