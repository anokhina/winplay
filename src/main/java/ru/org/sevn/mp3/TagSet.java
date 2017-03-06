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

import java.util.ArrayList;
import java.util.List;

import org.apache.tika.parser.mp3.ID3v1Handler;
import org.apache.tika.parser.mp3.ID3v2Frame;
import org.apache.tika.parser.mp3.SevnExtendedLyricsHandler;
import org.apache.tika.parser.mp3.SevnRawTagIteratorWrapper;
import org.apache.tika.parser.mp3.SevnRawTagWrapper;
import org.apache.tika.parser.mp3.ID3Tags.ID3Comment;

public class TagSet {
	private final int version;
	private final ArrayList<SevnRawTag> tags = new ArrayList<>();
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		try {
			sb.append("version=").append(version).append("\n");
			for (SevnRawTag t : tags) {
				sb.append(t.toString()).append("\n");
			}
			return sb.toString();
		} catch (Exception e) {
			return super.toString();
		}
	}
	/*
header	3	"TAG"
title	30	30 characters of the title
artist	30	30 characters of the artist name
album	30	30 characters of the album name
year	4	A four-digit year
comment	28[4] or 30	The comment.
zero-byte[4]	1	If a track number is stored, this byte contains a binary 0.
track[4]	1	The number of the track on the album, or 0. Invalid, if previous byte is not a binary 0.
genre	1	Index in a list of genres, or 255
	 * 
	 */
	
	public TagSet(ID3v1Handler v1, SevnExtendedLyricsHandler lyrics) {
		version = 1;
		if (lyrics != null) {
			if (v1.getAlbum() != null) {
				tags.add(new SevnSimpleRawTagWrapper(SevnRawTag.TAG1_ALBUM, v1.getAlbum().getBytes()));
			}
			if (v1.getAlbumArtist() != null) {
				tags.add(new SevnSimpleRawTagWrapper(SevnRawTag.TAG1_ALBUM_ARTIST, v1.getAlbumArtist().getBytes()));
			}
			if (v1.getArtist() != null) {
				tags.add(new SevnSimpleRawTagWrapper(SevnRawTag.TAG1_ARTIST, v1.getArtist().getBytes()));
			}
			if (v1.getYear() != null) {
				tags.add(new SevnSimpleRawTagWrapper(SevnRawTag.TAG1_YEAR, v1.getYear().getBytes()));
			}
			if (v1.getTrackNumber() != null) {
				tags.add(new SevnSimpleRawTagWrapper(SevnRawTag.TAG1_TRACK_NUM, v1.getTrackNumber().getBytes()));
			}
			if (v1.getTitle() != null) {
				tags.add(new SevnSimpleRawTagWrapper(SevnRawTag.TAG1_TITLE, v1.getTitle().getBytes()));
			}
			if (v1.getGenre() != null) {
				tags.add(new SevnSimpleRawTagWrapper(SevnRawTag.TAG1_GENRE, v1.getGenre().getBytes()));
			}
			if (v1.getDisc() != null) {
				tags.add(new SevnSimpleRawTagWrapper("disc", SevnRawTag.TAG1_DISC.getBytes()));
			}
			if (v1.getComposer() != null) {
				tags.add(new SevnSimpleRawTagWrapper(SevnRawTag.TAG1_COMPOSER, v1.getComposer().getBytes()));
			}
			if (v1.getCompilation() != null) {
				tags.add(new SevnSimpleRawTagWrapper(SevnRawTag.TAG1_COMPILATION, v1.getCompilation().getBytes()));
			}
			List<ID3Comment> comments = v1.getComments();
			if (comments != null) {
				for (int i = 0; i < comments.size(); i++) {
					ID3Comment comment = comments.get(i);
					if (comment != null && comment.getText() != null) {
						String lng = comment.getLanguage();
						if (lng == null) {
							lng = "";
						} else {
							lng = lng + "_";
						}
						tags.add(new SevnSimpleRawTagWrapper(SevnRawTag.TAG1_COMMENTS+lng, comment.getText().getBytes()));
					}
				}
			}
		}
	}
	public TagSet(ID3v2Frame id3F) {
		version = id3F.getMajorVersion();
		SevnRawTagIteratorWrapper tagIterator = new SevnRawTagIteratorWrapper(id3F.getMajorVersion(), id3F);
		while (tagIterator.hasNext()) {
			this.tags.add(new SevnRawTagWrapper(tagIterator.next()));
		}
	}
	public int getVersion() {
		return version;
	}
	public List<SevnRawTag> getTags() {
		return tags;
	}
}