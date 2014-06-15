/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 */
package com.ttProject.xuggle.test;

import java.nio.ByteBuffer;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.ttProject.util.BufferUtil;
import com.ttProject.util.HexUtil;
import com.xuggle.ferry.IBuffer;
import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IContainer;
import com.xuggle.xuggler.IPacket;
import com.xuggle.xuggler.IPixelFormat;
import com.xuggle.xuggler.IRational;
import com.xuggle.xuggler.IStream;
import com.xuggle.xuggler.IStreamCoder;
import com.xuggle.xuggler.IVideoPicture;
import com.xuggle.xuggler.IVideoResampler;
import com.xuggle.xuggler.IPixelFormat.Type;
import com.xuggle.xuggler.IStreamCoder.Direction;
import com.xuggle.xuggler.IStreamCoder.Flags;

/**
 * sample to make mp4 from raw rgb data.
 * @see https://groups.google.com/forum/#!topic/xuggler-users/Qn3lTdUH-y0
 * @author taktod
 */
public class MakePictureFromRGB24Test {
	/** logger */
	private Logger logger = Logger.getLogger(MakePictureFromRGB24Test.class);
	@Test
	public void makeMp4() {
		IStreamCoder decoder = null;
		IStreamCoder encoder = null;
		IContainer container = null;
		IVideoResampler resampler = null;
		IPacket sourcePacket = IPacket.make();
		IPacket targetPacket = IPacket.make();
		try {
			container = createContainer();
			encoder = createEncoder(container);
			decoder = createDecoder();
			resampler = createResampler(encoder, decoder);
			
			container.writeHeader();
			writeBody(decoder, encoder, container, resampler, sourcePacket, targetPacket);
			container.writeTrailer();
		}
		catch(Exception e) {
			logger.fatal("error end", e);
		}
		finally {
			if(decoder != null) {
				decoder.close();
				decoder = null;
			}
			if(encoder != null) {
				encoder.close();
				encoder = null;
			}
			if(container != null) {
				container.close();
				container = null;
			}
		}
	}
	private void writeBody(IStreamCoder decoder, IStreamCoder encoder, IContainer container, IVideoResampler resampler, IPacket sourcePacket, IPacket targetPacket)
			throws Exception {
		ByteBuffer bmpHeader = HexUtil.makeBuffer("424D36090000000000003600000028000000200000001800000001001800000000000009000000000000000000000000000000000000");
		int dataSize = 3 * 32 * 24; // 24bit * width x height
		for(int i = 0;i < 255;i ++) {
			ByteBuffer rgb = ByteBuffer.allocate(dataSize);
			for(int j = 0;j < dataSize / 3;j ++) {
				rgb.put((byte)0); // b
				rgb.put((byte)0); // g
				rgb.put((byte)i); // r
			}
			rgb.flip();
			ByteBuffer targetBuffer = BufferUtil.connect(bmpHeader, rgb);
			int size = targetBuffer.remaining();
			// make source Packet
			IBuffer bufData = IBuffer.make(null, targetBuffer.array(), 0, size);
			sourcePacket.setData(bufData);
			sourcePacket.setFlags(0);
			sourcePacket.setDts(i * 10);
			sourcePacket.setPts(i * 10);
			sourcePacket.setTimeBase(IRational.make(1, 1000));
			sourcePacket.setComplete(true, size);
			sourcePacket.setKeyPacket(true);
			logger.info(sourcePacket);
			
			decode(decoder, encoder, container, resampler, sourcePacket, targetPacket);
		}
	}
	private void decode(IStreamCoder decoder, IStreamCoder encoder, IContainer container, IVideoResampler resampler, IPacket sourcePacket, IPacket targetPacket)
			throws Exception {
		// make videoPicture
		IVideoPicture picture = IVideoPicture.make(decoder.getPixelType(), decoder.getWidth(), decoder.getHeight());
		int offset = 0;
		while(offset < sourcePacket.getSize()) {
			int bytesDecoded = decoder.decodeVideo(picture, sourcePacket, offset);
			if(bytesDecoded <= 0) {
				throw new Exception("fail to decode");
			}
			offset += bytesDecoded;
			if(picture.isComplete()) {
				picture = resamplePicture(encoder, resampler, picture);
				logger.info(picture);
				encode(encoder, container, picture, targetPacket);
			}
		}
	}
	private IVideoPicture resamplePicture(IStreamCoder encoder, IVideoResampler resampler, IVideoPicture picture) throws Exception {
		if(resampler == null) {
			return picture;
		}
		IVideoPicture resampledPicture = IVideoPicture.make(encoder.getPixelType(), encoder.getWidth(), encoder.getHeight());
		int retval = resampler.resample(resampledPicture, picture);
		if(retval <= 0) {
			throw new Exception("failed to resampled picture");
		}
		return resampledPicture;
	}
	private void encode(IStreamCoder encoder, IContainer container, IVideoPicture picture, IPacket targetPacket) throws Exception {
		if(encoder.encodeVideo(targetPacket, picture, 0) < 0) {
			throw new Exception("failed to encode");
		}
		if(targetPacket.isComplete()) {
			logger.info(targetPacket);
			if(container.writePacket(targetPacket) < 0) {
				throw new Exception("failed to write to container");
			}
		}
	}

	private IVideoResampler createResampler(IStreamCoder encoder, IStreamCoder decoder) throws Exception {
		if(encoder.getWidth() != decoder.getWidth()
		|| encoder.getHeight() != decoder.getHeight()
		|| encoder.getPixelType() != decoder.getPixelType()) {
			IVideoResampler resampler = IVideoResampler.make(
					encoder.getWidth(), encoder.getHeight(), encoder.getPixelType(), 
					decoder.getWidth(), decoder.getHeight(), decoder.getPixelType());
			return resampler;
		}
		else {
			return null;
		}
	}
	private IContainer createContainer() throws Exception {
		IContainer container = IContainer.make();
		if(container.open("output.mp4", IContainer.Type.WRITE, null) < 0) {
			throw new Exception("failed to open output container.");
		}
		return container;
	}
	private IStreamCoder createDecoder() throws Exception {
		IStreamCoder videoDecoder = IStreamCoder.make(Direction.DECODING, ICodec.ID.CODEC_ID_RAWVIDEO);
		videoDecoder.setTimeBase(IRational.make(1, 1000));
		videoDecoder.setPixelType(Type.BGR24);
		videoDecoder.setWidth(32);
		videoDecoder.setHeight(24);
		if(videoDecoder.open(null, null) < 0) {
			throw new Exception("failed to open decoder");
		}
		return videoDecoder;
	}
	private IStreamCoder createEncoder(IContainer container) throws Exception {
		IStream stream = container.addNewStream(ICodec.ID.CODEC_ID_H264);
		IStreamCoder videoEncoder = stream.getStreamCoder();
		IRational frameRate = IRational.make(15, 1); // 15fps
		videoEncoder.setNumPicturesInGroupOfPictures(5); // gop 5
		
		videoEncoder.setBitRate(650000); // 650kbps
		videoEncoder.setBitRateTolerance(9000);
		videoEncoder.setWidth(320);
		videoEncoder.setHeight(240);
		videoEncoder.setGlobalQuality(10);
		videoEncoder.setFrameRate(frameRate);
		videoEncoder.setTimeBase(IRational.make(1, 1000));
		videoEncoder.setProperty("level", "30");
		videoEncoder.setProperty("coder", "0");
		videoEncoder.setProperty("qmin", "10");
		videoEncoder.setProperty("bf", "0");
		videoEncoder.setProperty("wprefp", "0");
		videoEncoder.setProperty("cmp", "+chroma");
		videoEncoder.setProperty("partitions", "-parti8x8+parti4x4+partp8x8+partp4x4-partb8x8");
		videoEncoder.setProperty("me_method", "hex");
		videoEncoder.setProperty("subq", "5");
		videoEncoder.setProperty("me_range", "16");
		videoEncoder.setProperty("keyint_min", "25");
		videoEncoder.setProperty("sc_threshold", "40");
		videoEncoder.setProperty("i_qfactor", "0.71");
		videoEncoder.setProperty("b_strategy", "0");
		videoEncoder.setProperty("qcomp", "0.6");
		videoEncoder.setProperty("qmax", "30");
		videoEncoder.setProperty("qdiff", "4");
		videoEncoder.setProperty("directpred", "0");
		videoEncoder.setProperty("profile", "main");
		videoEncoder.setProperty("cqp", "0");
		videoEncoder.setFlag(Flags.FLAG_LOOP_FILTER, true);
		videoEncoder.setFlag(Flags.FLAG_CLOSED_GOP, true);

		ICodec codec = videoEncoder.getCodec();
		IPixelFormat.Type findType = null;
		for(IPixelFormat.Type type : codec.getSupportedVideoPixelFormats()) {
			if(findType == null) {
				findType = type;
			}
			if(type == IPixelFormat.Type.YUV420P) {
				findType = type;
				break;
			}
		}
		if(findType == null) {
			throw new Exception("pixelFormat is unknown");
		}
		videoEncoder.setPixelType(findType);
		if(videoEncoder.open(null, null) < 0) {
			throw new Exception("failed to open encoder");
		}
		return videoEncoder;
	}
}
