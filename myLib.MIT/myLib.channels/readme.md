# myLib.channel

## Overview
myLib.channel is the program to access byte data, which supports file, byte[], ByteBuffer, http file, and any kind ReadableByteChannel data.

## Version
0.0.2 - snapshot

## Author
| name | taktod |
|--------|--------|
| email | poepoemix@hotmail.com|
| twitter | https://twitter.com/taktod/|

## known bugs.
- less than 2GB only, cause use integer.

## Scenario
- You want to deal with the file on internet. However, download is too much. in this case, just to use URLFileReadChannel.

## Read flv
```java
	IReadChannel source = FileReadChannel.openFileReadChannel(
       	"http://somename.com/sample.flv
	);
```

### for examples, to analyze flv on http server.
```java
	IReadChannel source = FileReadChannel.openFileReadChannel(
       	"http://somename.com/sample.flv
	);
	IReader reader = new FlvTagReader();
	IContainer container = null;
	while((container = reader.read(source)) != null) {
       	// you can access containers of sample.flv
	}
```
