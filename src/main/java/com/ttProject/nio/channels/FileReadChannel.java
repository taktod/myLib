package com.ttProject.nio.channels;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class FileReadChannel implements IFileReadChannel {
	private final File file;
	private FileChannel channel;
	/**
	 * コンストラクタ
	 * @param target
	 */
	public FileReadChannel(String fileString) throws Exception {
		file = new File(fileString);
		channel = new FileInputStream(file).getChannel();
	}
	public FileReadChannel(URL url) throws Exception {
		file = new File(url.toURI());
		channel = new FileInputStream(file).getChannel();
	}
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
}
