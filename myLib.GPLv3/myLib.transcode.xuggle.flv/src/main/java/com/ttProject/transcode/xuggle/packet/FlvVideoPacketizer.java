package com.ttProject.transcode.xuggle.packet;

import java.nio.ByteBuffer;

import com.ttProject.media.Unit;
import com.ttProject.media.flv.tag.VideoTag;
import com.ttProject.media.h264.ConfigData;
import com.ttProject.media.h264.DataNalAnalyzer;
import com.ttProject.media.h264.Frame;
import com.ttProject.media.h264.frame.PictureParameterSet;
import com.ttProject.media.h264.frame.SequenceParameterSet;
import com.ttProject.media.h264.frame.Slice;
import com.ttProject.media.h264.frame.SliceIDR;
import com.ttProject.nio.channels.ByteReadChannel;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.transcode.exception.FormatChangeException;
import com.xuggle.ferry.IBuffer;
import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IPacket;
import com.xuggle.xuggler.IRational;
import com.xuggle.xuggler.IStreamCoder;
import com.xuggle.xuggler.IStreamCoder.Direction;

/**
 * flvTagからIPacketを作る動作
 * @author taktod
 */
public class FlvVideoPacketizer implements IPacketizer {
	/** 最終映像タグ */
	private VideoTag lastVideoTag = null;
	/** h264のsps */
	private SequenceParameterSet sps = null;
	/** h264のpps */
	private PictureParameterSet pps = null;
	/**
	 * データをあらかじめ確認します。
	 */
	@Override
	public boolean check(Unit unit) throws FormatChangeException {
		// 先にデータを確認して、データの整合性を確認します。
		// なおtypeが違う場合は、そのままtrueを返します。
		// falseになる場合の例：中途でコーデックがかわったなど
		if(!(unit instanceof VideoTag)) {
			return false;
		}
		if(lastVideoTag == null) {
			return true;
		}
		VideoTag vTag = (VideoTag) unit;
		if(vTag.getCodec() == lastVideoTag.getCodec()) {
			return true;
		}
		throw new FormatChangeException();
	}
	/**
	 * tagからpacketを取り出します。
	 */
	@Override
	public IPacket getPacket(Unit unit, IPacket packet) throws Exception {
		if(unit instanceof VideoTag) {
			lastVideoTag = (VideoTag) unit;
			switch(lastVideoTag.getCodec()) {
			case JPEG:
				sps = null;
				pps = null;
				throw new RuntimeException("JPEGの変換は未実装です。");
			case H263:
				sps = null;
				pps = null;
				return getH263Packet(lastVideoTag, packet);
			case SCREEN:
				sps = null;
				pps = null;
				throw new RuntimeException("SCREENの変換は未実装です。");
			case ON2VP6:
				sps = null;
				pps = null;
				return getVP6Packet(lastVideoTag, packet);
			case ON2VP6_ALPHA:
				sps = null;
				pps = null;
				throw new RuntimeException("vp6 alphaの変換は未実装です。");
			case SCREEN_V2:
				sps = null;
				pps = null;
				throw new RuntimeException("Screen V2の変換は未実装です。");
			case AVC:
				return getAVCPacket(lastVideoTag, packet);
			default:
				break;
			}
		}
		return null;
	}
	/**
	 * h263用のIPacketを生成します。
	 * @param tag
	 * @return
	 */
	private IPacket getH263Packet(VideoTag tag, IPacket packet) {
		if(packet == null) {
			packet = IPacket.make();
		}
		ByteBuffer buffer = tag.getRawData();
		int size = buffer.remaining();
		IBuffer bufData = IBuffer.make(null, buffer.array(), 0, size);
		packet.setData(bufData);
		packet.setFlags(0);
		packet.setDts(tag.getTimestamp());
		packet.setPts(tag.getTimestamp());
		packet.setTimeBase(IRational.make(1, 1000));
		packet.setComplete(true, size);
		packet.setKeyPacket(tag.isKeyFrame());
		return packet;
	}
	/**
	 * vp6用のIPacketを生成します。
	 * @param tag
	 * @return
	 */
	private IPacket getVP6Packet(VideoTag tag, IPacket packet) {
		if(packet == null) {
			packet = IPacket.make();
		}
		ByteBuffer buffer = tag.getRawData();
		int size = buffer.remaining();
		byte first = buffer.get();
		byte[] data = new byte[size];
		buffer.get(data, 0, size - 1);
		data[data.length - 1] = first;
		IBuffer bufData = IBuffer.make(null, data, 0, size);
		packet.setData(bufData);
		packet.setFlags(0);
		packet.setDts(tag.getTimestamp());
		packet.setPts(tag.getTimestamp());
		packet.setTimeBase(IRational.make(1, 1000));
		packet.setComplete(true, size);
		packet.setKeyPacket(tag.isKeyFrame());
		return packet;
	}
	/**
	 * avc用のIPacketを生成します。
	 * @param tag
	 * @return
	 */
	private IPacket getAVCPacket(VideoTag tag, IPacket packet) throws Exception {
		if(tag.isMediaSequenceHeader()) {
			// spsとppsを抜き出す。
			ConfigData configData = new ConfigData();
			IReadChannel rawData = new ByteReadChannel(tag.getRawData());
			rawData.position(3);
			for(Frame nal : configData.getNals(rawData)) {
				if(nal instanceof SequenceParameterSet) {
					sps = (SequenceParameterSet) nal;
				}
				else if(nal instanceof PictureParameterSet) {
					pps = (PictureParameterSet) nal;
				}
			}
			return null;
		}
		if(tag.isEndOfSequence()) {
			return null;
		}
		if(sps == null || pps == null) {
			throw new RuntimeException("spsもしくはppsが決定していません。");
		}
		// mshではないので、実データである。
		if(packet == null) {
			packet = IPacket.make();
		}
		ByteBuffer rawData = tag.getRawData();
		rawData.position(3); // ３の位置に移動(ここからDataNalAnalyzerにいれるとnalが取り出せる)
		DataNalAnalyzer dataAnalyzer = new DataNalAnalyzer(); // TODO これもglobal化しておいた方が本当はよい。ただし、ここでの動作はNALの解析ではなく、データの取り出しなので、globalでなくても問題ない。
		// まぁnewするコストは無駄だけど。
		IReadChannel rawDataChannel = new ByteReadChannel(rawData);
		ByteBuffer buffer = null;
		Frame avcFrame = null;
		if(tag.isKeyFrame()) {
			while((avcFrame = dataAnalyzer.analyze(rawDataChannel)) != null) {
				if(avcFrame instanceof SliceIDR) {
					break;
				}
			}
			ByteBuffer spsData = sps.getData();
			ByteBuffer ppsData = pps.getData();
			ByteBuffer sliceIDRData = avcFrame.getData();
			buffer = ByteBuffer.allocate(4 + spsData.remaining()
					+ 4 + ppsData.remaining()
					+ 4 + sliceIDRData.remaining());
			buffer.putInt(1);
			buffer.put(spsData);
			buffer.putInt(1);
			buffer.put(ppsData);
			buffer.putInt(1);
			buffer.put(sliceIDRData);
			buffer.flip();
		}
		else {
			while((avcFrame = dataAnalyzer.analyze(rawDataChannel)) != null) {
				if(avcFrame instanceof Slice) {
					break;
				}
			}
			ByteBuffer sliceData =avcFrame.getData();
			buffer = ByteBuffer.allocate(4 + sliceData.remaining());
			buffer.putInt(1);
			buffer.put(sliceData);
			buffer.flip();
		}
		int size = buffer.remaining();
		IBuffer bufData = IBuffer.make(null, buffer.array(), 0, size);
		packet.setData(bufData);
		packet.setFlags(1);
		packet.setDts(tag.getTimestamp());
		packet.setPts(tag.getTimestamp());
		packet.setTimeBase(IRational.make(1, 1000));
		packet.setComplete(true, size);
		packet.setKeyPacket(tag.isKeyFrame());
		return packet;
	}
	/**
	 * 現在動作しているデータに対応するデコーダーを取得します
	 */
	@Override
	public IStreamCoder createDecoder() throws Exception {
		// まだデータがわからない場合はnullを返す
		if(lastVideoTag == null) {
			return null;
		}
		IStreamCoder decoder = null;
		switch(lastVideoTag.getCodec()) {
		case JPEG:
			throw new RuntimeException("JPEGの変換は未実装です。");
		case H263:
			decoder = IStreamCoder.make(Direction.DECODING, ICodec.ID.CODEC_ID_FLV1);
			decoder.setTimeBase(IRational.make(1, 1000));
			break;
		case SCREEN:
			throw new RuntimeException("SCREENの変換は未実装です。");
		case ON2VP6:
			decoder = IStreamCoder.make(Direction.DECODING, ICodec.ID.CODEC_ID_VP6F);
			decoder.setTimeBase(IRational.make(1, 1000));
			break;
		case ON2VP6_ALPHA:
			throw new RuntimeException("vp6 alphaの変換は未実装です。");
		case SCREEN_V2:
			throw new RuntimeException("Screen V2の変換は未実装です。");
		case AVC:
			decoder = IStreamCoder.make(Direction.DECODING, ICodec.ID.CODEC_ID_H264);
			decoder.setTimeBase(IRational.make(1, 1000));
			break;
		default:
			return null;
		}
		return decoder;
	}
	@Override
	public void close() {
		
	}
}
