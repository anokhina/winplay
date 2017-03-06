/*******************************************************************************
 * Copyright 2017 Veronica Anokhina.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package ru.org.sevn.mp3;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.apache.tika.exception.TikaException;
import org.apache.tika.io.TailStream;
import org.apache.tika.parser.mp3.AudioFrame;
import org.apache.tika.parser.mp3.ID3v1Handler;
import org.apache.tika.parser.mp3.ID3v2Frame;
import org.apache.tika.parser.mp3.MP3Frame;
import org.apache.tika.parser.mp3.SevnExtendedLyricsHandler;
import org.apache.tika.parser.mp3.SevnExtendedMpegStream;
import org.xml.sax.SAXException;

public class Mp3Info {
	private ArrayList<TagSet> tags = new ArrayList<>();
	private float duration = 0;
	
	public String toString() {
		try {
			StringBuilder sb = new StringBuilder();
			sb.append("duration").append(":").append(duration).append("\n");
			for (TagSet t : tags) {
				sb.append(t.toString()).append("\n");
			}
			return sb.toString(); 
		} catch (Exception e) {
			return super.toString();
		}
	}
	
	public Mp3Info(InputStream stream) throws IOException, SAXException, TikaException {
		TailStream tailStream = new TailStream(stream, 10240 + 128);
		SevnExtendedMpegStream mpegStream = new SevnExtendedMpegStream(tailStream);

		// several ID3v2 tag blocks
		MP3Frame f;
		while ((f = ID3v2Frame.createFrameIfPresent(mpegStream)) != null) {
			if (f instanceof ID3v2Frame) {
				tags.add(new TagSet((ID3v2Frame) f));
			}
		}
		
		// pass audio frames
		AudioFrame firstAudio = null;
		AudioFrame frame = mpegStream.nextFrame();
		while (frame != null) {
			duration += frame.getDuration();
			if (firstAudio == null) {
				firstAudio = frame;
			}
			mpegStream.skipFrame();
			frame = mpegStream.nextFrame();
		}
		
		// Lyrics
		// ID3v1
		SevnExtendedLyricsHandler lyrics = new SevnExtendedLyricsHandler(tailStream.getTail());
		ID3v1Handler v1 = lyrics.getId3v1(); // tag
		tags.add(new TagSet(v1, lyrics));
	}

	public ArrayList<TagSet> getTags() {
		return tags;
	}

	public float getDuration() {
		return duration;
	}
	
	public SevnRawTag getRawTagOr(String ... names) {
		for (String n : names) {
			n = n.toUpperCase();
			for (TagSet ts : tags) {
				for (SevnRawTag t : ts.getTags()) {
					if (t.getName().equals(n)) {
						return t;
					}
				}
			}
		}
		return null;
	}
	public SevnRawTag getRawTag(String name) {
		return getRawTagOr(name);
	}
}