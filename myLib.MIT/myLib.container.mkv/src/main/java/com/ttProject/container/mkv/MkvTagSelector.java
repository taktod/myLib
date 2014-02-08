package com.ttProject.container.mkv;

import org.apache.log4j.Logger;

import com.ttProject.container.mkv.type.Audio;
import com.ttProject.container.mkv.type.BitDepth;
import com.ttProject.container.mkv.type.Channels;
import com.ttProject.container.mkv.type.Cluster;
import com.ttProject.container.mkv.type.CodecID;
import com.ttProject.container.mkv.type.CodecPrivate;
import com.ttProject.container.mkv.type.CueClusterPosition;
import com.ttProject.container.mkv.type.CuePoint;
import com.ttProject.container.mkv.type.CueTime;
import com.ttProject.container.mkv.type.CueTrack;
import com.ttProject.container.mkv.type.CueTrackPositions;
import com.ttProject.container.mkv.type.Cues;
import com.ttProject.container.mkv.type.DefaultDuration;
import com.ttProject.container.mkv.type.DocType;
import com.ttProject.container.mkv.type.DocTypeReadVersion;
import com.ttProject.container.mkv.type.DocTypeVersion;
import com.ttProject.container.mkv.type.Duration;
import com.ttProject.container.mkv.type.EBML;
import com.ttProject.container.mkv.type.EBMLMaxIDLength;
import com.ttProject.container.mkv.type.EBMLMaxSizeLength;
import com.ttProject.container.mkv.type.EBMLReadVersion;
import com.ttProject.container.mkv.type.EBMLVersion;
import com.ttProject.container.mkv.type.FlagLacing;
import com.ttProject.container.mkv.type.Info;
import com.ttProject.container.mkv.type.Language;
import com.ttProject.container.mkv.type.MuxingApp;
import com.ttProject.container.mkv.type.PixelHeight;
import com.ttProject.container.mkv.type.PixelWidth;
import com.ttProject.container.mkv.type.SamplingFrequency;
import com.ttProject.container.mkv.type.Seek;
import com.ttProject.container.mkv.type.SeekHead;
import com.ttProject.container.mkv.type.SeekID;
import com.ttProject.container.mkv.type.SeekPosition;
import com.ttProject.container.mkv.type.Segment;
import com.ttProject.container.mkv.type.SegmentUID;
import com.ttProject.container.mkv.type.SimpleBlock;
import com.ttProject.container.mkv.type.SimpleTag;
import com.ttProject.container.mkv.type.Tag;
import com.ttProject.container.mkv.type.TagLanguage;
import com.ttProject.container.mkv.type.TagName;
import com.ttProject.container.mkv.type.TagString;
import com.ttProject.container.mkv.type.Tags;
import com.ttProject.container.mkv.type.Targets;
import com.ttProject.container.mkv.type.Timecode;
import com.ttProject.container.mkv.type.TimecodeScale;
import com.ttProject.container.mkv.type.TrackEntry;
import com.ttProject.container.mkv.type.TrackNumber;
import com.ttProject.container.mkv.type.TrackType;
import com.ttProject.container.mkv.type.TrackUID;
import com.ttProject.container.mkv.type.Tracks;
import com.ttProject.container.mkv.type.Video;
import com.ttProject.container.mkv.type.Void;
import com.ttProject.container.mkv.type.WritingApp;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.ISelector;
import com.ttProject.unit.IUnit;
import com.ttProject.unit.extra.BitLoader;
import com.ttProject.unit.extra.EbmlValue;

/**
 * mkvのelementを解析して取り出すselector
 * @author taktod
 */
public class MkvTagSelector implements ISelector {
	/** ロガー */
	@SuppressWarnings("unused")
	private Logger logger = Logger.getLogger(MkvTagSelector.class);
	/**
	 * {@inheritDoc}
	 */
	@Override
	public IUnit select(IReadChannel channel) throws Exception {
		if(channel.position() == channel.size()) {
			// データがもうない
			return null;
		}
		EbmlValue tag  = new EbmlValue();
		EbmlValue size = new EbmlValue();
		BitLoader loader = new BitLoader(channel);
		loader.load(tag, size);
		MkvTag mkvTag = null;
		switch(Type.getType(tag.getEbmlValue())) {
		case EBML:
			mkvTag = new EBML(size);
			break;
		case EBMLVersion:
			mkvTag = new EBMLVersion(size);
			break;
		case EBMLReadVersion:
			mkvTag = new EBMLReadVersion(size);
			break;
		case EBMLMaxIDLength:
			mkvTag = new EBMLMaxIDLength(size);
			break;
		case EBMLMaxSizeLength:
			mkvTag = new EBMLMaxSizeLength(size);
			break;
		case DocType:
			mkvTag = new DocType(size);
			break;
		case DocTypeVersion:
			mkvTag = new DocTypeVersion(size);
			break;
		case DocTypeReadVersion:
			mkvTag = new DocTypeReadVersion(size);
			break;
		case Segment:
			mkvTag = new Segment(size);
			break;
		case SeekHead:
			mkvTag = new SeekHead(size);
			break;
		case Seek:
			mkvTag = new Seek(size);
			break;
		case SeekID:
			mkvTag = new SeekID(size);
			break;
		case SeekPosition:
			mkvTag = new SeekPosition(size);
			break;
		case Void:
			mkvTag = new Void(size);
			break;
		case Info:
			mkvTag = new Info(size);
			break;
		case TimecodeScale:
			mkvTag = new TimecodeScale(size);
			break;
		case MuxingApp:
			mkvTag = new MuxingApp(size);
			break;
		case WritingApp:
			mkvTag = new WritingApp(size);
			break;
		case SegmentUID:
			mkvTag = new SegmentUID(size);
			break;
		case Duration:
			mkvTag = new Duration(size);
			break;
		case Tracks:
			mkvTag = new Tracks(size);
			break;
		case TrackEntry:
			mkvTag = new TrackEntry(size);
			break;
		case TrackNumber:
			mkvTag = new TrackNumber(size);
			break;
		case TrackUID:
			mkvTag = new TrackUID(size);
			break;
		case FlagLacing:
			mkvTag = new FlagLacing(size);
			break;
		case Language:
			mkvTag = new Language(size);
			break;
		case CodecID:
			mkvTag = new CodecID(size);
			break;
		case TrackType:
			mkvTag = new TrackType(size);
			break;
		case DefaultDuration:
			mkvTag = new DefaultDuration(size);
			break;
		case Video:
			mkvTag = new Video(size);
			break;
		case PixelWidth:
			mkvTag = new PixelWidth(size);
			break;
		case PixelHeight:
			mkvTag = new PixelHeight(size);
			break;
		case CodecPrivate:
			mkvTag = new CodecPrivate(size);
			break;
		case Audio:
			mkvTag = new Audio(size);
			break;
		case Channels:
			mkvTag = new Channels(size);
			break;
		case SamplingFrequency:
			mkvTag = new SamplingFrequency(size);
			break;
		case BitDepth:
			mkvTag = new BitDepth(size);
			break;
		case Tags:
			mkvTag = new Tags(size);
			break;
		case Tag:
			mkvTag = new Tag(size);
			break;
		case Targets:
			mkvTag = new Targets(size);
			break;
		case SimpleTag:
			mkvTag = new SimpleTag(size);
			break;
		case TagName:
			mkvTag = new TagName(size);
			break;
		case TagString:
			mkvTag = new TagString(size);
			break;
		case Cluster:
			mkvTag = new Cluster(size);
			break;
		case Timecode:
			mkvTag = new Timecode(size);
			break;
		case SimpleBlock:
			mkvTag = new SimpleBlock(size);
			break;
		case Cues:
			mkvTag = new Cues(size);
			break;
		case CuePoint:
			mkvTag = new CuePoint(size);
			break;
		case CueTime:
			mkvTag = new CueTime(size);
			break;
		case CueTrackPositions:
			mkvTag = new CueTrackPositions(size);
			break;
		case CueTrack:
			mkvTag = new CueTrack(size);
			break;
		case CueClusterPosition:
			mkvTag = new CueClusterPosition(size);
			break;
		case TagLanguage:
			mkvTag = new TagLanguage(size);
//			break;
		default:
			throw new Exception("未実装のTypeデータが応答されました。" + Type.getType(tag.getEbmlValue()));
		}
		mkvTag.minimumLoad(channel);
		return mkvTag;
	}
}
