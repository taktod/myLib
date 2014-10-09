package com.ttProject.container.riff.type;

import org.apache.log4j.Logger;

import com.ttProject.container.riff.RiffSizeUnit;
import com.ttProject.container.riff.StrhRiffCodecType;
import com.ttProject.container.riff.Type;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.extra.BitLoader;
import com.ttProject.unit.extra.bit.Bit16;
import com.ttProject.unit.extra.bit.Bit32;
import com.ttProject.util.BufferUtil;

/**
 * strh
 * @see http://msdn.microsoft.com/ja-jp/library/cc352263.aspx
 * @author taktod
 */
public class Strh extends RiffSizeUnit {
	/** logger */
	private Logger logger = Logger.getLogger(Strh.class);
	private FccType fccType;
	private StrhRiffCodecType fccHandler;
	private Bit32 dwFlags = new Bit32();
	private Bit16 wPriority = new Bit16();
	private Bit16 wLanguage = new Bit16();
	private Bit32 dwInitialFrames = new Bit32();
	private Bit32 dwScale = new Bit32();
	private Bit32 dwRate = new Bit32();
	private Bit32 dwStart = new Bit32();
	private Bit32 dwLength = new Bit32();
	private Bit32 dwSuggestedBufferSize = new Bit32();
	private Bit32 dwQuality = new Bit32();
	private Bit32 dwSampleSize = new Bit32();
	private Bit16 left = new Bit16();
	private Bit16 top = new Bit16();;
	private Bit16 right = new Bit16();;
	private Bit16 bottom = new Bit16();;
	public static enum FccType{
		auds,
		mids,
		tets,
		vids
	};
	/**
	 * constructor
	 */
	public Strh() {
		super(Type.strh);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void minimumLoad(IReadChannel channel) throws Exception {
		super.minimumLoad(channel);
		fccType = FccType.valueOf(new String(BufferUtil.safeRead(channel, 4).array()).intern());
		logger.info(fccType);
		fccHandler = StrhRiffCodecType.getValue(BufferUtil.safeRead(channel, 4).getInt());
		logger.info(fccHandler);
		BitLoader loader = new BitLoader(channel);
		loader.setLittleEndianFlg(true);
		loader.load(dwFlags, wPriority, wLanguage, dwInitialFrames, dwScale,
				dwRate, dwStart, dwLength, dwSuggestedBufferSize, dwQuality, dwSampleSize,
				left, top, right, bottom);
		logger.info(dwStart.get());
		logger.info(dwLength.get());
		logger.info(left.get());
		logger.info(top.get());
		logger.info(right.get());
		logger.info(bottom.get());
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void load(IReadChannel channel) throws Exception {
		// TODO Auto-generated method stub
		
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void requestUpdate() throws Exception {
		// TODO Auto-generated method stub
		
	};
	/**
	 * ref the fccType
	 * @return
	 */
	public FccType getFccType() {
		return fccType;
	}
}
