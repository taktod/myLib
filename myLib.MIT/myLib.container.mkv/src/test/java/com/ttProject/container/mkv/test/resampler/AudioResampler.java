/*
 * audioResampler rewrite with resample.c from speex-1.2rc1.
/* Copyright (C) 2007-2008 Jean-Marc Valin
   Copyright (C) 2008      Thorvald Natvig
      
   File: resample.c
   Arbitrary resampling code

   Redistribution and use in source and binary forms, with or without
   modification, are permitted provided that the following conditions are
   met:

   1. Redistributions of source code must retain the above copyright notice,
   this list of conditions and the following disclaimer.

   2. Redistributions in binary form must reproduce the above copyright
   notice, this list of conditions and the following disclaimer in the
   documentation and/or other materials provided with the distribution.

   3. The name of the author may not be used to endorse or promote products
   derived from this software without specific prior written permission.

   THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
   IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
   OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
   DISCLAIMED. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT,
   INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
   (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
   SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
   HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
   STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN
   ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
   POSSIBILITY OF SUCH DAMAGE.
* /

/ *
   The design goals of this code are:
      - Very fast algorithm
      - SIMD-friendly algorithm
      - Low memory requirement
      - Good *perceptual* quality (and not best SNR)

   Warning: This resampler is relatively new. Although I think I got rid of 
   all the major bugs and I don't expect the API to change anymore, there
   may be something I've missed. So use with caution.

   This algorithm is based on this original resampling algorithm:
   Smith, Julius O. Digital Audio Resampling Home Page
   Center for Computer Research in Music and Acoustics (CCRMA), 
   Stanford University, 2007.
   Web published at http://www-ccrma.stanford.edu/~jos/resample/.

   There is one main difference, though. This resampler uses cubic 
   interpolation instead of linear interpolation in the above paper. This
   makes the table much smaller and makes it possible to compute that table
   on a per-stream basis. In turn, being able to tweak the table for each 
   stream makes it possible to both reduce complexity on simple ratios 
   (e.g. 2/3), and get rid of the rounding operations in the inner loop. 
   The latter both reduces CPU time and makes the algorithm more SIMD-friendly.
 */
package com.ttProject.container.mkv.test.resampler;

import org.apache.log4j.Logger;

/**
 * 
 * @author taktod
 */
public class AudioResampler implements IAudioResampler {
	private Logger logger = Logger.getLogger(AudioResampler.class);
	private long inRate;
	private long outRate;
	private long numRate;
	private long denRate;
	
	private int   quality;
	private long  nbChannels;
	private long  filtLen;
	private long  memAllocSize;
	private long  bufferSize;
	private int   intAdvance;
	private int   fracAdvance;
	private float cutoff;
	private long  oversample;
	private boolean   initialized;
	private boolean   started;
	
	/** These are per-channel */
	private int[]  lastSample;
	private long[] sampFracNum;
	private long[] magicSamples;
	
	private float[] mem;
	private float[] sincTable;
	private long sincTableLength;

	private ResamplerBasicFunc resamplerPtr;
	
	private long inStride;
	private long outStride;
	
	private Kaiser kaiser12 = new Kaiser(new double[]{
			0.99859849, 1.00000000, 0.99859849, 0.99440475, 0.98745105, 0.97779076,
			0.96549770, 0.95066529, 0.93340547, 0.91384741, 0.89213598, 0.86843014,
			0.84290116, 0.81573067, 0.78710866, 0.75723148, 0.72629970, 0.69451601,
			0.66208321, 0.62920216, 0.59606986, 0.56287762, 0.52980938, 0.49704014,
			0.46473455, 0.43304576, 0.40211431, 0.37206735, 0.34301800, 0.31506490,
			0.28829195, 0.26276832, 0.23854851, 0.21567274, 0.19416736, 0.17404546,
			0.15530766, 0.13794294, 0.12192957, 0.10723616, 0.09382272, 0.08164178,
			0.07063950, 0.06075685, 0.05193064, 0.04409466, 0.03718069, 0.03111947,
			0.02584161, 0.02127838, 0.01736250, 0.01402878, 0.01121463, 0.00886058,
			0.00691064, 0.00531256, 0.00401805, 0.00298291, 0.00216702, 0.00153438,
			0.00105297, 0.00069463, 0.00043489, 0.00025272, 0.00013031, 0.0000527734,
			0.00001000, 0.00000000}, 64);
	private Kaiser kaiser10 = new Kaiser(new double[]{
			0.99537781, 1.00000000, 0.99537781, 0.98162644, 0.95908712, 0.92831446,
			0.89005583, 0.84522401, 0.79486424, 0.74011713, 0.68217934, 0.62226347,
			0.56155915, 0.50119680, 0.44221549, 0.38553619, 0.33194107, 0.28205962,
			0.23636152, 0.19515633, 0.15859932, 0.12670280, 0.09935205, 0.07632451,
			0.05731132, 0.04193980, 0.02979584, 0.02044510, 0.01345224, 0.00839739,
			0.00488951, 0.00257636, 0.00115101, 0.00035515, 0.00000000, 0.00000000}, 32);
	private Kaiser kaiser8 = new Kaiser(new double[]{
			0.99635258, 1.00000000, 0.99635258, 0.98548012, 0.96759014, 0.94302200,
			0.91223751, 0.87580811, 0.83439927, 0.78875245, 0.73966538, 0.68797126,
			0.63451750, 0.58014482, 0.52566725, 0.47185369, 0.41941150, 0.36897272,
			0.32108304, 0.27619388, 0.23465776, 0.19672670, 0.16255380, 0.13219758,
			0.10562887, 0.08273982, 0.06335451, 0.04724088, 0.03412321, 0.02369490,
			0.01563093, 0.00959968, 0.00527363, 0.00233883, 0.00050000, 0.00000000}, 32);
	private Kaiser kaiser6 = new Kaiser(new double[]{
			0.99733006, 1.00000000, 0.99733006, 0.98935595, 0.97618418, 0.95799003,
			0.93501423, 0.90755855, 0.87598009, 0.84068475, 0.80211977, 0.76076565,
			0.71712752, 0.67172623, 0.62508937, 0.57774224, 0.53019925, 0.48295561,
			0.43647969, 0.39120616, 0.34752997, 0.30580127, 0.26632152, 0.22934058,
			0.19505503, 0.16360756, 0.13508755, 0.10953262, 0.08693120, 0.06722600,
			0.05031820, 0.03607231, 0.02432151, 0.01487334, 0.00752000, 0.00000000}, 32);
	private QualityMapping[] qualityMap = new QualityMapping[11];
	{
		qualityMap[0]  = new QualityMapping(  8,  4, 0.830f, 0.860f, kaiser6);
		qualityMap[1]  = new QualityMapping( 16,  4, 0.850f, 0.880f, kaiser6);
		qualityMap[2]  = new QualityMapping( 32,  4, 0.882f, 0.910f, kaiser6);
		qualityMap[3]  = new QualityMapping(  8,  4, 0.830f, 0.860f, kaiser8);
		qualityMap[4]  = new QualityMapping(  8,  4, 0.830f, 0.860f, kaiser8);
		qualityMap[5]  = new QualityMapping(  8,  4, 0.830f, 0.860f, kaiser10);
		qualityMap[6]  = new QualityMapping(  8,  4, 0.830f, 0.860f, kaiser10);
		qualityMap[7]  = new QualityMapping(  8,  4, 0.830f, 0.860f, kaiser10);
		qualityMap[8]  = new QualityMapping(  8,  4, 0.830f, 0.860f, kaiser10);
		qualityMap[9]  = new QualityMapping(  8,  4, 0.830f, 0.860f, kaiser12);
		qualityMap[10] = new QualityMapping(  8,  4, 0.830f, 0.860f, kaiser12);
	}
	/**
	 * constructor
	 * @param channels 
	 * @param inRate
	 * @param outRate
	 * @param quality 0-10
	 */
	public AudioResampler(int channels, long inRate, long outRate, int quality) {
		if(quality > 10 || quality < 0) {
			throw new RuntimeException("ResamplerErrInvalidArg:" + quality);
		}
		this.initialized = false;
		this.started = false;
		this.inRate = 0;
		this.outRate = 0;
		this.numRate = 0;
		this.denRate = 0;
		this.quality = -1;
		this.sincTableLength = 0;
		this.memAllocSize = 0;
		this.filtLen = 0;
		this.mem = null;
		this.resamplerPtr = null;
		this.cutoff = 1.0f;
		this.nbChannels = channels;
		this.inStride = 1;
		this.outStride = 1;
		this.bufferSize = 160;
		this.lastSample = new int[channels];
		this.magicSamples = new long[channels];
		this.sampFracNum = new long[channels];
		for(int i = 0;i < channels;i ++) {
			this.lastSample[i] = 0;
			this.magicSamples[i] = 0;
			this.sampFracNum[i] = 0;
		}
		setQuality(quality);
		setRateFrac(inRate, outRate, inRate, outRate);
		
		updateFilter();
		this.initialized = true;
	}
	/**
	 * constructor
	 * @param channels
	 * @param inRate
	 * @param outRate
	 */
	public AudioResampler(int channels, int inRate, int outRate) {
		this(channels, inRate, outRate, 5);
	}
	@Override
	public void close() {
		// javaは勝手にメモリーが解放されるので、特にやることはない
	}
	@Override
	public int processFloat(long channelIndex, float[] in, long inLen,
			float[] out, long outLen) {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public int processInt(long channelIndex, float[] in, long inLen,
			float[] out, long outLen) {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public int processInterleavedFloat(float[] in, long inLen, float[] out,
			long outLen) {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public int processInterleavedInt(float[] in, long inLen, float[] out,
			long outLen) {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public boolean setRate(long inRate, long outRate) {
		return setRateFrac(inRate, outRate, inRate, outRate);
	}
	@Override
	public long getInRate() {
		return this.inRate;
	}
	@Override
	public long getOutRate() {
		return this.outRate;
	}
	@Override
	public long getRatioDen() {
		return this.numRate;
	}
	@Override
	public long getRatioNum() {
		return this.denRate;
	}
	@Override
	public void setQuality(int quality) {
		if(quality < 0 || quality > 10) {
			throw new RuntimeException("ResampleErrorInvalidArg:" + quality);
		}
		if(this.quality == quality) {
			// すでに同じqualityになっているので、変更する必要なし
			return;
		}
		this.quality = quality;
		if(this.initialized) {
			updateFilter();
		}
	}
	@Override
	public int getQuality() {
		return this.quality;
	}
	@Override
	public void setInputStride(long stride) {
		this.inStride = stride;
	}
	@Override
	public long getInputStride() {
		return this.inStride;
	}
	@Override
	public void setOutputStride(long stride) {
		this.outStride = stride;
	}
	@Override
	public long getOutputStride() {
		return this.outStride;
	}
	@Override
	public long getInputLatency() {
		return this.filtLen / 2;
	}
	@Override
	public long getOutputLatency() {
		return ((this.filtLen / 2) * this.denRate + (this.numRate >> 1)) / this.numRate;
	}
	@Override
	public boolean skipZeros() {
		for(int i = 0;i < this.nbChannels;i ++) {
			this.lastSample[i] = (int)(this.filtLen / 2);
		}
		return true;
	}
	@Override
	public boolean resetMem() {
		for(int i = 0;i < this.nbChannels * (this.filtLen - 1);i ++) {
			this.mem[i] = 0;
		}
		return true;
	}
	private boolean setRateFrac(long ratioNum, long ratioDen, long inRate, long outRate) {
		long fact;
		long oldDen;
		
		if(this.inRate == inRate && this.outRate == outRate && this.numRate == ratioNum && this.denRate == ratioDen) {
			// すでに同じデータになっているので、いまから変更する必要はないです。
			return true;
		}
		oldDen = this.denRate;
		this.inRate = inRate;
		this.outRate = outRate;
		this.numRate = ratioNum;
		this.denRate = ratioDen;
		
		for(fact = 2;fact <= min(this.numRate, this.denRate);fact ++) {
			while((this.numRate % fact == 0)) {
				this.numRate /= fact;
				this.denRate /= fact;
			}
		}
		if(oldDen > 0) {
			for(int i = 0;i < this.nbChannels;i ++) {
				this.sampFracNum[i] = this.sampFracNum[i] * this.denRate / oldDen;
				if(this.sampFracNum[i] >= this.denRate) {
					this.sampFracNum[i] = this.denRate - 1;
				}
			}
		}
		if(this.initialized) {
			updateFilter();
		}
		return true;
	}
	private void updateFilter() {
		logger.info("try update filter");
		long oldLength;
		oldLength = this.filtLen;
		this.oversample = qualityMap[this.quality].overSample;
		this.filtLen = qualityMap[this.quality].baseLength;
		
		if(this.numRate > this.denRate) {
			// downSampling
			this.cutoff = qualityMap[this.quality].downsampleBandwidth * this.denRate / this.numRate;
			this.filtLen = this.filtLen * this.numRate / this.denRate;
			this.filtLen &= (~0x3);
			if(2 * this.denRate < this.numRate) {
				this.oversample >>= 1;
			}
			if(4 * this.denRate < this.numRate) {
				this.oversample >>= 1;
			}
			if(8 * this.denRate < this.numRate) {
				this.oversample >>= 1;
			}
			if(16 * this.denRate < this.numRate) {
				this.oversample >>= 1;
			}
			if(this.oversample < 1) {
				this.oversample = 1;
			}
		}
		else {
			// upSampling
			this.cutoff = qualityMap[this.quality].upsampleBandwidth;
		}
		// resampling typeを選択して、メモリー量を確定する
		if(this.denRate <= this.oversample) {
			if(this.sincTable == null) {
				this.sincTable = new float[(int)(this.filtLen * this.denRate)];
				// オリジナルにはないけど、追加してみた。
				this.sincTableLength = this.filtLen * this.denRate;
			}
			else if(this.sincTableLength < this.filtLen * this.denRate) {
				this.sincTable = new float[(int)(this.filtLen * this.denRate)];
				this.sincTableLength = this.filtLen * this.denRate;
			}
			for(int i = 0;i < this.denRate;i ++) {
				for(int j = 0;j < this.filtLen;j ++) {
					this.sincTable[(int)(i * this.filtLen + j)] = sinc(this.cutoff, ((j - this.filtLen / 2 + 1) - ((float)i)/this.denRate), (int)this.filtLen, qualityMap[this.quality].kaiser);
				}
			}
			if(this.quality > 8) {
				this.resamplerPtr = ResamplerBasicFunc.DirectDouble;
			}
			else {
				this.resamplerPtr = ResamplerBasicFunc.DirectSingle;
			}
		}
		else {
			if(this.sincTable == null) {
				this.sincTable = new float[(int)(this.filtLen * this.oversample + 8)];
				// こっちもオリジナルにはないけど、追加しておいてみる
				this.sincTableLength = this.filtLen * this.oversample + 8;
			}
			else if(this.sincTableLength < this.filtLen * this.oversample + 8) {
				this.sincTable = new float[(int)(this.filtLen * this.oversample + 8)];
				this.sincTableLength = this.filtLen * this.oversample + 8;
			}
			for(int i = -4;i < (this.oversample * this.filtLen + 4);i ++) {
				this.sincTable[i + 4] = sinc(this.cutoff, (i/(float)this.oversample - this.filtLen/2), (int)this.filtLen, qualityMap[this.quality].kaiser);
			}
			if(this.quality > 8) {
				this.resamplerPtr = ResamplerBasicFunc.InterpolateDouble;
			}
			else {
				this.resamplerPtr = ResamplerBasicFunc.InterpolateSingle;
			}
		}
		this.intAdvance = (int)(this.numRate / this.denRate);
		this.fracAdvance = (int)(this.numRate % this.denRate);
		
		if(this.mem == null) {
			this.memAllocSize = this.filtLen - 1 + this.bufferSize;
			this.mem = new float[(int)(this.nbChannels * this.memAllocSize)];
			for(int i = 0;i < this.nbChannels * this.memAllocSize;i ++) {
				this.mem[i] = 0;
			}
		}
		else if(!this.started) {
			this.memAllocSize = this.filtLen - 1 + this.bufferSize;
			this.mem = new float[(int)(this.nbChannels * this.memAllocSize)];
			for(int i = 0;i < this.nbChannels * this.memAllocSize;i ++) {
				this.mem[i] = 0;
			}
		}
		else if(this.filtLen > oldLength) {
			int oldAllocSize = (int)this.memAllocSize;
			if((this.filtLen - 1 + this.bufferSize) > this.memAllocSize) {
				this.memAllocSize = this.filtLen - 1 + this.bufferSize;
				this.mem = new float[(int)(this.nbChannels * this.memAllocSize)];
			}
			for(int i = (int)(this.nbChannels - 1);i >= 0;i --) {
				long olen = oldLength;
				{
					olen = oldLength + 2 * this.magicSamples[i];
					for(int j = (int)(oldLength - 2 + this.magicSamples[i]);j >= 0;j --) {
						this.mem[(int)(i * this.memAllocSize + j + this.magicSamples[i])] = this.mem[i * oldAllocSize + j];
					}
					for(int j = 0;j < this.magicSamples[i];j ++) {
						this.mem[(int)(i * this.memAllocSize + j)] = 0;
					}
					this.magicSamples[i] = 0;
				}
				if(this.filtLen > olen) {
					int j = 0;
					for(j = 0;j < olen - 1;j ++) {
						this.mem[(int)(i * this.memAllocSize + this.filtLen - 2 - j)] = this.mem[(int)(i * this.memAllocSize + (olen - 2 - j))];
					}
					for(;j < this.filtLen - 1;j ++) {
						this.mem[(int)(i * this.memAllocSize + this.filtLen - 2 - j)] = 0;
					}
					this.lastSample[i] += (this.filtLen - olen) / 2;
				}
				else {
					this.magicSamples[i] += (olen - this.filtLen) / 2;
					for(int j = 0;j < this.filtLen - 1 + this.magicSamples[i]; j ++) {
						this.mem[(int)(i * this.memAllocSize + j)] = this.mem[(int)(i * this.memAllocSize + j + this.magicSamples[i])];
					}
				}
			}
		}
		else if(this.filtLen < oldLength) {
			for(int i = 0;i < this.nbChannels;i ++) {
				long oldMagic = this.magicSamples[i];
				this.magicSamples[i] = (oldLength - this.filtLen) / 2;
				for(int j = 0;j < this.filtLen - 1 + this.magicSamples[i] + oldMagic;j ++) {
					this.mem[(int)(i * this.memAllocSize + j)] = this.mem[(int)(i * this.memAllocSize + j + this.magicSamples[i])];
				}
				this.magicSamples[i] += oldMagic;
			}
		}
	}
	private long min(long a, long b) {
		return a < b ? a : b;
	}
	private static double computeFunc(double x, Kaiser func) {
		float y, frac;
		double[] interp = new double[4];
		int ind;
		y = (float) (x * func.oversample);
		ind = (int) Math.floor(y);
		frac = (y - ind);
		/* CSE with handle the repeated powers */
		interp[3] =  -0.1666666667*frac + 0.1666666667*(frac*frac*frac);
		interp[2] = frac + 0.5*(frac*frac) - 0.5*(frac*frac*frac);
		/*interp[2] = 1.f - 0.5f*frac - frac*frac + 0.5f*frac*frac*frac;*/
		interp[0] = -0.3333333333*frac + 0.5*(frac*frac) - 0.1666666667*(frac*frac*frac);
		/* Just to make sure we don't have rounding problems */
		interp[1] = 1.f-interp[3]-interp[2]-interp[0];
		
		/*sum = frac*accum[1] + (1-frac)*accum[2];*/
		return interp[0] * func.table[ind] + interp[1] * func.table[ind + 1] + interp[2] * func.table[ind + 2] + interp[3] * func.table[ind + 3];
	}
	private static float sinc(float cutoff, float x, int N, Kaiser windowFunc) {
		float xx = x * cutoff;
		if(Math.abs(x) < 1e-6) {
			return cutoff;
		}
		else if(Math.abs(x) > .5*N) {
			return 0;
		}
		return (float)(cutoff * Math.sin(Math.PI * xx) / (Math.PI * xx) * computeFunc(Math.abs(2. * x / N), windowFunc));
	}
	private static void cubicCoef(float frac, float[] interp) {
		/* Compute interpolation coefficients. I'm not sure whether this corresponds to cubic interpolation
		but I know it's MMSE-optimal on a sinc */
		interp[0] =  -0.16667f*frac + 0.16667f*frac*frac*frac;
		interp[1] = frac + 0.5f*frac*frac - 0.5f*frac*frac*frac;
		/*interp[2] = 1.f - 0.5f*frac - frac*frac + 0.5f*frac*frac*frac;*/
		interp[3] = -0.33333f*frac + 0.5f*frac*frac - 0.16667f*frac*frac*frac;
		/* Just to make sure we don't have rounding problems */
		interp[2] = (float)(1.-interp[0]-interp[1]-interp[3]);
	}
	private static class QualityMapping {
		private int baseLength;
		private int overSample;
		private float downsampleBandwidth;
		private float upsampleBandwidth;
		private Kaiser kaiser;
		private QualityMapping(int baseLength, int overSample, float downsampleBandwidth, float upsampleBandwidth, Kaiser kaiser) {
			this.baseLength = baseLength;
			this.overSample = overSample;
			this.downsampleBandwidth = downsampleBandwidth;
			this.upsampleBandwidth = upsampleBandwidth;
			this.kaiser = kaiser;
		}
	}
	private static class Kaiser {
		private double[] table;
		int oversample;
		private Kaiser(double[] table, int oversample) {
			this.table = table;
			this.oversample = oversample;
		}
	}
	private enum ResamplerBasicFunc {
		DirectSingle,
		DirectDouble,
		InterpolateSingle,
		InterpolateDouble
	};
	private int resamplerBasicDirectSingle(int channelIndex, float[] in, long inLen, float[] out, long outLen) {
		final long N = this.filtLen;
		int outSample = 0;
		int lastSample = this.lastSample[channelIndex];
		long sampFracNum = this.sampFracNum[channelIndex];
		float[] sincTable = this.sincTable;
		final long outStride = this.outStride;
		final int intAdvance = this.intAdvance;
		final int fracAdvance = this.fracAdvance;
		final long denRate = this.denRate;
		float sum;
		while(!(lastSample >= inLen || outSample >= outLen)) {
			int sincPos = (int)(sampFracNum * N);
			int inPos = lastSample;
//			float sinc[] = sincTable[(int)(sampFracNum * N)];
//			float iptr[] = in[lastSample];
			
			float[] accum = {0,0,0,0};
			for(int j = 0;j < N;j += 4) {
				accum[0] += sincTable[sincPos + j] * in[inPos + j];
				accum[1] += sincTable[sincPos + j + 1] * in[inPos + j + 1];
				accum[2] += sincTable[sincPos + j + 2] * in[inPos + j + 2];
				accum[3] += sincTable[sincPos + j + 3] * in[inPos + j + 3];
			}
		}
		return 0;
	}
}
