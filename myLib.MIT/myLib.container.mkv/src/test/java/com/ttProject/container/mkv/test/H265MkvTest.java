package com.ttProject.container.mkv.test;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.ttProject.container.IContainer;
import com.ttProject.container.IReader;
import com.ttProject.container.mkv.MkvTagReader;
import com.ttProject.container.mkv.type.SimpleBlock;
import com.ttProject.frame.AudioFrame;
import com.ttProject.frame.VideoFrame;
import com.ttProject.nio.channels.FileReadChannel;
import com.ttProject.nio.channels.IFileReadChannel;

/**
 * mkvの動作テスト
 * こんなエラーがでるなんだろう。
 * なんで最後の最後にSimpleBlockではなくBlockGroupがきた。
 * なんだこれ？
 * 12:22:23,569 [main] INFO [H265MkvTest] - class com.ttProject.frame.h265.type.TrailN:1920x1080
12:22:23,570 [main] INFO [H265MkvTest] - BlockGroup size:4a*
12:22:23,571 [main] WARN [H265MkvTest] - 例外発生
java.lang.Exception: 未定義の値です。:f0
	at com.ttProject.container.mkv.Type.getType(Type.java:139)
	at com.ttProject.container.mkv.MkvTagSelector.select(MkvTagSelector.java:133)
	at com.ttProject.container.mkv.MkvTagReader.read(MkvTagReader.java:40)
	at com.ttProject.container.mkv.MkvMasterTag.load(MkvMasterTag.java:41)
	at com.ttProject.container.mkv.MkvTagReader.read(MkvTagReader.java:50)
	at com.ttProject.container.mkv.MkvMasterTag.load(MkvMasterTag.java:41)
	at com.ttProject.container.mkv.MkvTagReader.read(MkvTagReader.java:50)
	at com.ttProject.container.mkv.MkvMasterTag.load(MkvMasterTag.java:41)
	at com.ttProject.container.mkv.MkvTagReader.read(MkvTagReader.java:50)
	at com.ttProject.container.mkv.test.H265MkvTest.analyzerTest(H265MkvTest.java:42)
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at sun.reflect.NativeMethodAccessorImpl.invoke(Unknown Source)
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(Unknown Source)
	at java.lang.reflect.Method.invoke(Unknown Source)
	at org.junit.runners.model.FrameworkMethod$1.runReflectiveCall(FrameworkMethod.java:47)
	at org.junit.internal.runners.model.ReflectiveCallable.run(ReflectiveCallable.java:12)
	at org.junit.runners.model.FrameworkMethod.invokeExplosively(FrameworkMethod.java:44)
	at org.junit.internal.runners.statements.InvokeMethod.evaluate(InvokeMethod.java:17)
	at org.junit.runners.ParentRunner.runLeaf(ParentRunner.java:271)
	at org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:70)
	at org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:50)
	at org.junit.runners.ParentRunner$3.run(ParentRunner.java:238)
	at org.junit.runners.ParentRunner$1.schedule(ParentRunner.java:63)
	at org.junit.runners.ParentRunner.runChildren(ParentRunner.java:236)
	at org.junit.runners.ParentRunner.access$000(ParentRunner.java:53)
	at org.junit.runners.ParentRunner$2.evaluate(ParentRunner.java:229)
	at org.junit.runners.ParentRunner.run(ParentRunner.java:309)
	at org.eclipse.jdt.internal.junit4.runner.JUnit4TestReference.run(JUnit4TestReference.java:50)
	at org.eclipse.jdt.internal.junit.runner.TestExecution.run(TestExecution.java:38)
	at org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.runTests(RemoteTestRunner.java:467)
	at org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.runTests(RemoteTestRunner.java:683)
	at org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.run(RemoteTestRunner.java:390)
	at org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.main(RemoteTestRunner.java:197)
12:22:23,573 [main] WARN [H265MkvTest] - エラー発生場所:9d210db

 * @author taktod
 */
public class H265MkvTest {
	/** ロガー */
	private Logger logger = Logger.getLogger(H265MkvTest.class);
	/**
	 * analyzerの動作テスト
	 */
	@Test
	public void analyzerTest() {
		IFileReadChannel source = null;
		int lastPosition = 0;
		try {
			/*
			 * test2 3 4 5 7 8はh264 + aac or mp3
			 * test1 msmpeg4v2
			 */
			source = FileReadChannel.openFileReadChannel(
					"http://trailers.divx.com/hevc/TearsOfSteelFull12min_1080p_24fps_27qp_1474kbps_GPSNR_42.29_HM11.mkv"
			);
			logger.info("ファイルサイズ:" + source.size());
			logger.info("エラー発生場所:" + 0x9d210db);
			Thread.sleep(2000);
			IReader reader = new MkvTagReader();
			IContainer container = null;
			while((container = reader.read(source)) != null) {
				logger.info(container);
				if(container instanceof SimpleBlock) {
					SimpleBlock blockTag = (SimpleBlock) container;
					logger.info("key:" + blockTag.isKeyFrame() + " invisible:" + blockTag.isInvisibleFrame());
					if(blockTag.getFrame() instanceof VideoFrame) {
						VideoFrame vFrame = (VideoFrame)blockTag.getFrame();
						logger.info(vFrame.getClass() + ":" + vFrame.getWidth() + "x" + vFrame.getHeight());
					}
					else if(blockTag.getFrame() instanceof AudioFrame) {
						AudioFrame aFrame = (AudioFrame)blockTag.getFrame();
						logger.info(aFrame.getClass() + ":" + aFrame.getSampleRate() + ":" + aFrame.getChannel());
					}
				}
				lastPosition = source.position();
			}
		}
		catch(Exception e) {
			logger.warn("例外発生", e);
			try {
				logger.warn("エラー発生場所:" + Integer.toHexString(lastPosition));
			}
			catch(Exception ex) {
				
			}
		}
		finally {
			if(source != null) {
				try {
					source.close();
				}
				catch(Exception e) {}
				source = null;
			}
		}
	}
}
