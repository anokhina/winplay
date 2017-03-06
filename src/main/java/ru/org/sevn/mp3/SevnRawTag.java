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

import java.util.Base64;

public abstract class SevnRawTag<T> {
	protected final T tag;
	
	public SevnRawTag(T t) {
		this.tag = t;
	}
	
    public int getHeaderSize() {
		return getSize() - getData().length;
	}
	public abstract String getName();

	public abstract int getFlag();

	public abstract byte[] getData();

	public abstract int getSize();
	
	public abstract void setName(String name);

	public void setNameData(String name, byte[] data) {
		setName(name);
		setData(data);
	}

	public abstract void setFlag(int flag);

	public abstract void setData(byte[] data);

	public T getTag() {
		return tag;
	}
	
	public String toString() {
		try {
			StringBuilder sb = new StringBuilder();
			sb.append(getName()).append(":").append(getAnyDataString());
			return sb.toString();
		} catch (Exception e) {
			return super.toString();
		}
	}
    
	public String getAnyDataString() {
		String nm = getName().toUpperCase();
		for (String tn : TAGS_STR) {
			if (tn.equals(nm)) {
				return getDataString();
			}
		}
		return getBase64DataString();
	}
	public String getBase64DataString() {
		return Base64.getEncoder().encodeToString(getData());
	}
	public abstract String getDataString();
	
	//http://id3.org/id3v2.3.0
	//http://id3.org/id3v2.4.0-changes
	//http://id3.org/id3v2.4.0-frames
	public static final String TAG34_TITLE = "TIT2";
	public static final String TAG34_ARTIST = "TPE1";
	public static final String TAG34_ALBUM_ARTIST = "TPE2";
	public static final String TAG34_ALBUM = "TALB";
	public static final String TAG34_YEAR = "TYER";
	public static final String TAG4_YEAR1 = "TDRC";
	public static final String TAG34_COMPOSER = "TCOM";
	public static final String TAG34_COMMENTS = "COMM";
	public static final String TAG34_TRACK_NUM = "TRCK";
	public static final String TAG34_DISC = "TPOS";
	public static final String TAG34_COMPILATION = "TCMP";
	public static final String TAG34_GENRE = "TCON";
	
	//http://id3.org/id3v2-00
	public static final String TAG2_TITLE = "TT2";
	public static final String TAG2_ARTIST = "TP1";
	public static final String TAG2_ALBUM_ARTIST = "TP2";
	public static final String TAG2_ALBUM = "TAL";
	public static final String TAG2_YEAR = "TYE";
	public static final String TAG2_COMPOSER = "TCM";
	public static final String TAG2_COMMENTS = "COM";
	public static final String TAG2_TRACK_NUM = "TRK";
	public static final String TAG2_DISC = "TPA";
	public static final String TAG2_GENRE = "TCO";
	
	public static final String TAG2_PICTURE = "PIC";
	public static final String TAG34_PICTURE = "APIC";
	
 	public static final String TAG1_TITLE = "TITLE";
	public static final String TAG1_ARTIST = "ARTIST";
	public static final String TAG1_ALBUM_ARTIST = "ALBUMARTIST";
	public static final String TAG1_ALBUM = "ALBUM";
	public static final String TAG1_YEAR = "YEAR";
	public static final String TAG1_COMPOSER = "СOMPOSER";
	public static final String TAG1_COMMENTS = "COMMENTS";
	public static final String TAG1_TRACK_NUM = "TRACKNUMBER";
	public static final String TAG1_DISC = "DISC";
	public static final String TAG1_COMPILATION = "СOMPILATION";
	public static final String TAG1_GENRE = "GENRE";
	
	public static final String[] TAGS_STR = new String[] {
			TAG34_TITLE, TAG34_ARTIST, TAG34_ALBUM_ARTIST, TAG34_ALBUM, TAG34_YEAR, 
			TAG4_YEAR1, TAG34_COMPOSER, TAG34_COMMENTS, TAG34_TRACK_NUM, TAG34_DISC, TAG34_COMPILATION, TAG34_GENRE,
			TAG2_TITLE, TAG2_ARTIST, TAG2_ALBUM_ARTIST, TAG2_ALBUM, TAG2_YEAR, TAG2_COMPOSER, TAG2_COMMENTS, TAG2_TRACK_NUM, TAG2_DISC, TAG2_GENRE,
			TAG1_TITLE, TAG1_ARTIST, TAG1_ALBUM_ARTIST, TAG1_ALBUM, TAG1_YEAR, TAG1_COMPOSER, TAG1_COMMENTS, TAG1_TRACK_NUM, TAG1_DISC, TAG1_COMPILATION, TAG1_GENRE
	};
}
