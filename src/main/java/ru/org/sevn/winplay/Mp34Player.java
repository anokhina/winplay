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
package ru.org.sevn.winplay;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import ru.org.sevn.audiobookplayer.MMediaPlayer;
import ru.org.sevn.audiobookplayer.MediaPlayerProxy;
import uk.co.caprica.vlcj.player.MediaMeta;
import uk.co.caprica.vlcj.player.MediaPlayerFactory;

public class Mp34Player extends MMediaPlayer {
	
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
    	
        MediaPlayerFactory factory = mediaPlayerComponent.getMediaPlayerFactory();
        MediaMeta mediaMeta = factory.getMediaMeta(selectedFile.getAbsolutePath(), true);
        if (mediaMeta != null) {
        	String t = mediaMeta.getTitle();
        	if (t == null || t.trim().length() == 0) {} else {
        		title = t;
        	}
	        final BufferedImage artwork = mediaMeta.getArtwork();
	        if (artwork != null) {
	        	ByteArrayOutputStream baos = new ByteArrayOutputStream();
	        	try {
					ImageIO.write( artwork, "png", baos );
		        	baos.flush();
		        	image = baos.toByteArray();
		        	baos.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        }
	
	        mediaMeta.release();
        }
		
		getAppSettings().setTitle(title);
		getAppSettings().setImage(image);
    }
}
