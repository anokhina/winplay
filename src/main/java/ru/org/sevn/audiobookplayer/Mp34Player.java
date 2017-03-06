/*
 * Copyright 2016, 2017 Veronica Anokhina.
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
 */
package ru.org.sevn.audiobookplayer;

import java.io.File;
import java.io.FileInputStream;

import org.apache.tika.Tika;

import ru.org.sevn.mp3.Mp3Info;
import ru.org.sevn.mp3.SevnRawTag;
import ru.org.sevn.winplay.ListenEmbeddedMediaPlayerComponent;
import ru.org.sevn.winplay.WinMediaPlayer;

public class Mp34Player extends MMediaPlayer {
	
	private Tika tika = new Tika();
	
	private ListenEmbeddedMediaPlayerComponent mediaPlayerComponent = new ListenEmbeddedMediaPlayerComponent();
	
    public ListenEmbeddedMediaPlayerComponent getMediaPlayerComponent() {
		return mediaPlayerComponent;
	}
    
    @Override
	protected MediaPlayerProxy createMediaPlayer() {
        return new WinMediaPlayer(mediaPlayerComponent.getMediaPlayer());
    }
//    public synchronized void setFile(Uri uri, BitmapLoader bloader) {
//        if (uri == null) return;
//        File selectedFile = new File(uri.getPath());
//        setFile(new File(uri.getPath()), bloader);
//    }    
    protected void retrieveMediaInfo(File selectedFile) {
    	String title = selectedFile.getName();
    	byte[] image = null;
		try {
			String tp = tika.detect(selectedFile); 
			if (tp.startsWith("audio/mpeg")) {
				FileInputStream s = new FileInputStream(selectedFile);
				
				Mp3Info info = new Mp3Info(s);
				SevnRawTag ttitle = info.getRawTagOr(SevnRawTag.TAG34_TITLE, SevnRawTag.TAG2_TITLE);
				if (ttitle != null) {
					title = ttitle.getDataString();
				}
				
				SevnRawTag timage = info.getRawTagOr(SevnRawTag.TAG34_PICTURE, SevnRawTag.TAG2_PICTURE);
				if (timage != null) {
					image = timage.getData();
				}
			}
		} catch (Exception e) {}
		getAppSettings().setTitle(title);
		getAppSettings().setImage(image);
    }
}
