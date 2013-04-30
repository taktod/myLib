package com.ttProject.nio.channels;

import java.io.IOException;
import java.nio.ByteBuffer;

public interface IFileReadChannel {
	public void close() throws IOException;
	public boolean isOpen();
	public int read(ByteBuffer dst) throws IOException;
	public int position() throws Exception;
	public IFileReadChannel position(int newPosition) throws Exception;
	public int size() throws Exception;
}
