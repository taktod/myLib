# myLib.container.flv

## Overview
myLib.container.flv is the program to analyze flv container, to get the media frame, and to write flv file with media frame.

## Version
0.0.2 - snapshot

## Author
| name | taktod |
|--------|--------|
| email | poepoemix@hotmail.com|
| twitter | https://twitter.com/taktod/|

## Scenario
- You got a broken flv from rtmp recording. Make little test code to analyze flvTag. You will find the audio codec change on the flv file. You can make program to split the flv file, and both flv can play.

## Read flv
```java
	IReadChannel source = FileReadChannel.openFileReadChannel(
       	"test.flv"
	);
	IReader reader = new FlvTagReader();
	IContainer container = null;
	while((container = reader.read(source)) != null) {
       	// do something for container.
	}
```

## Write flv
```java
	IWriter writer = new FlvTagWriter("output.flv");
	writer.prepareHeader(CodecType.H264, CodecType.AAC);
		// somehow get frame.
		writer.addFrame(frame);
	writer.prepareTailer();
```

### in the case of scenario
analyze flv file.
```java
	IReadChannel source = FileReadChannel.openFileReadChannel(
    	"targetFile.flv"
    );
    IReader reader = new FlvTagReader();
    IContainer container = null;
    while((container = reader.read(source)) != null) {
    	logger.info(container);
    }
```
then get the pts for split. in this case, 163000. then write the split program.
```java
	IReadChannel source = FileReadChannel.openFileReadChannel(
    	"targetFile.flv"
    );
    IWriter firstWriter = new FlvTagWriter("first.flv");
    firstWriter.prepareHeader(CodecType.H264, CodecType.MP3);
    IWRiter secondWriter = new FlvTagWriter("second.flv");
    secondWriter.prepareHeader(CodecType.H264, CodecType.AAC);
    IReader reader = new FlvTagReader();
    IWriter writer = null;
    IContainer container = null;
    while((container = reader.read(source)) != null) {
    	if(container.getPts() < 163000) {
        	writer = firstWriter;
        }
        else {
        	writer = secondWriter;
        }
        if(container instanceof AudioTag) {
        	writer.addFrame(((AudioTag)container).getFrame());
        }
        if(container instanceof VideoTag) {
        	writer.addFrame(((VideoTag)container).getFrame());
        }
    }
    source.close();
```
you will get the two flvs. "first.flv" and "second.flv".
