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
package ru.org.sevn.winplay;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import uk.co.caprica.vlcj.binding.internal.libvlc_media_t;
import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerEventListener;

public class ListenEmbeddedMediaPlayerComponent extends EmbeddedMediaPlayerComponent {
	
	private MediaPlayerEventListener mediaPlayerEventListener = new MediaPlayerEventListenerAdapter();
	
    public MediaPlayerEventListener getMediaPlayerEventListener() {
		return mediaPlayerEventListener;
	}

	public void setMediaPlayerEventListener(MediaPlayerEventListener mediaPlayerEventListener) {
		this.mediaPlayerEventListener = mediaPlayerEventListener;
	}

	@Override
    public void mediaChanged(MediaPlayer mediaPlayer, libvlc_media_t media, String mrl) {
    	mediaPlayerEventListener.mediaChanged(mediaPlayer, media, mrl);
    }

    @Override
    public void opening(MediaPlayer mediaPlayer) {
    	mediaPlayerEventListener.opening(mediaPlayer);
    }

    @Override
    public void buffering(MediaPlayer mediaPlayer, float newCache) {
    	mediaPlayerEventListener.buffering(mediaPlayer, newCache);
    }

    @Override
    public void playing(MediaPlayer mediaPlayer) {
    	mediaPlayerEventListener.playing(mediaPlayer);
    }

    @Override
    public void paused(MediaPlayer mediaPlayer) {
    	mediaPlayerEventListener.paused(mediaPlayer);
    }

    @Override
    public void stopped(MediaPlayer mediaPlayer) {
    	mediaPlayerEventListener.stopped(mediaPlayer);
    }

    @Override
    public void forward(MediaPlayer mediaPlayer) {
    	mediaPlayerEventListener.forward(mediaPlayer);
    }

    @Override
    public void backward(MediaPlayer mediaPlayer) {
    	mediaPlayerEventListener.backward(mediaPlayer);
    }

    @Override
    public void finished(MediaPlayer mediaPlayer) {
    	mediaPlayerEventListener.finished(mediaPlayer);
    }

    @Override
    public void timeChanged(MediaPlayer mediaPlayer, long newTime) {
    	mediaPlayerEventListener.timeChanged(mediaPlayer, newTime);
    }

    @Override
    public void positionChanged(MediaPlayer mediaPlayer, float newPosition) {
    	mediaPlayerEventListener.positionChanged(mediaPlayer, newPosition);
    }

    @Override
    public void seekableChanged(MediaPlayer mediaPlayer, int newSeekable) {
    	mediaPlayerEventListener.seekableChanged(mediaPlayer, newSeekable);
    }

    @Override
    public void pausableChanged(MediaPlayer mediaPlayer, int newSeekable) {
    	mediaPlayerEventListener.pausableChanged(mediaPlayer, newSeekable);
    }

    @Override
    public void titleChanged(MediaPlayer mediaPlayer, int newTitle) {
    	mediaPlayerEventListener.timeChanged(mediaPlayer, newTitle);
    }

    @Override
    public void snapshotTaken(MediaPlayer mediaPlayer, String filename) {
    	mediaPlayerEventListener.snapshotTaken(mediaPlayer, filename);
    }

    @Override
    public void lengthChanged(MediaPlayer mediaPlayer, long newLength) {
    	mediaPlayerEventListener.lengthChanged(mediaPlayer, newLength);
    }

    @Override
    public void videoOutput(MediaPlayer mediaPlayer, int newCount) {
    	mediaPlayerEventListener.videoOutput(mediaPlayer, newCount);
    }

    @Override
    public void scrambledChanged(MediaPlayer mediaPlayer, int newScrambled) {
    	mediaPlayerEventListener.scrambledChanged(mediaPlayer, newScrambled);
    }

    @Override
    public void elementaryStreamAdded(MediaPlayer mediaPlayer, int type, int id) {
    	mediaPlayerEventListener.elementaryStreamAdded(mediaPlayer, type, id);
    }

    @Override
    public void elementaryStreamDeleted(MediaPlayer mediaPlayer, int type, int id) {
    	mediaPlayerEventListener.elementaryStreamDeleted(mediaPlayer, type, id);
    }

    @Override
    public void elementaryStreamSelected(MediaPlayer mediaPlayer, int type, int id) {
    	mediaPlayerEventListener.elementaryStreamSelected(mediaPlayer, type, id);
    }

    @Override
    public void corked(MediaPlayer mediaPlayer, boolean corked) {
    	mediaPlayerEventListener.corked(mediaPlayer, corked);
    }

    @Override
    public void muted(MediaPlayer mediaPlayer, boolean muted) {
    	mediaPlayerEventListener.muted(mediaPlayer, muted);
    }

    @Override
    public void volumeChanged(MediaPlayer mediaPlayer, float volume) {
    	mediaPlayerEventListener.volumeChanged(mediaPlayer, volume);
    }

    @Override
    public void audioDeviceChanged(MediaPlayer mediaPlayer, String audioDevice) {
    	mediaPlayerEventListener.audioDeviceChanged(mediaPlayer, audioDevice);
    }

    @Override
    public void chapterChanged(MediaPlayer mediaPlayer, int newChapter) {
    	mediaPlayerEventListener.chapterChanged(mediaPlayer, newChapter);
    }

    @Override
    public void error(MediaPlayer mediaPlayer) {
    	mediaPlayerEventListener.error(mediaPlayer);
    }

    @Override
    public void mediaMetaChanged(MediaPlayer mediaPlayer, int metaType) {
    	mediaPlayerEventListener.mediaMetaChanged(mediaPlayer, metaType);
    }

    @Override
    public void mediaSubItemAdded(MediaPlayer mediaPlayer, libvlc_media_t subItem) {
    	mediaPlayerEventListener.mediaSubItemAdded(mediaPlayer, subItem);
    }

    @Override
    public void mediaDurationChanged(MediaPlayer mediaPlayer, long newDuration) {
    	mediaPlayerEventListener.mediaDurationChanged(mediaPlayer, newDuration);
    }

    @Override
    public void mediaParsedChanged(MediaPlayer mediaPlayer, int newStatus) {
    	mediaPlayerEventListener.mediaParsedChanged(mediaPlayer, newStatus);
    }

    @Override
    public void mediaFreed(MediaPlayer mediaPlayer) {
    	mediaPlayerEventListener.mediaFreed(mediaPlayer);
    }

    @Override
    public void mediaStateChanged(MediaPlayer mediaPlayer, int newState) {
    	mediaPlayerEventListener.mediaStateChanged(mediaPlayer, newState);
    }

    @Override
    public void mediaSubItemTreeAdded(MediaPlayer mediaPlayer, libvlc_media_t item) {
    	mediaPlayerEventListener.mediaSubItemTreeAdded(mediaPlayer, item);
    }

    @Override
    public void newMedia(MediaPlayer mediaPlayer) {
    	mediaPlayerEventListener.newMedia(mediaPlayer);
    }

    @Override
    public void subItemPlayed(MediaPlayer mediaPlayer, int subItemIndex) {
    	mediaPlayerEventListener.subItemPlayed(mediaPlayer, subItemIndex);
    }

    @Override
    public void subItemFinished(MediaPlayer mediaPlayer, int subItemIndex) {
    	mediaPlayerEventListener.subItemFinished(mediaPlayer, subItemIndex);
    }

    @Override
    public void endOfSubItems(MediaPlayer mediaPlayer) {
    	mediaPlayerEventListener.endOfSubItems(mediaPlayer);
    }
	
	public static class MediaPlayerEventListenerAdapter implements MediaPlayerEventListener {

	    @Override
	    public void mediaChanged(MediaPlayer mediaPlayer, libvlc_media_t media, String mrl) {
	    }

	    @Override
	    public void opening(MediaPlayer mediaPlayer) {
	    }

	    @Override
	    public void buffering(MediaPlayer mediaPlayer, float newCache) {
	    }

	    @Override
	    public void playing(MediaPlayer mediaPlayer) {
	    }

	    @Override
	    public void paused(MediaPlayer mediaPlayer) {
	    }

	    @Override
	    public void stopped(MediaPlayer mediaPlayer) {
	    }

	    @Override
	    public void forward(MediaPlayer mediaPlayer) {
	    }

	    @Override
	    public void backward(MediaPlayer mediaPlayer) {
	    }

	    @Override
	    public void finished(MediaPlayer mediaPlayer) {
	    }

	    @Override
	    public void timeChanged(MediaPlayer mediaPlayer, long newTime) {
	    }

	    @Override
	    public void positionChanged(MediaPlayer mediaPlayer, float newPosition) {
	    }

	    @Override
	    public void seekableChanged(MediaPlayer mediaPlayer, int newSeekable) {
	    }

	    @Override
	    public void pausableChanged(MediaPlayer mediaPlayer, int newSeekable) {
	    }

	    @Override
	    public void titleChanged(MediaPlayer mediaPlayer, int newTitle) {
	    }

	    @Override
	    public void snapshotTaken(MediaPlayer mediaPlayer, String filename) {
	    }

	    @Override
	    public void lengthChanged(MediaPlayer mediaPlayer, long newLength) {
	    }

	    @Override
	    public void videoOutput(MediaPlayer mediaPlayer, int newCount) {
	    }

	    @Override
	    public void scrambledChanged(MediaPlayer mediaPlayer, int newScrambled) {
	    }

	    @Override
	    public void elementaryStreamAdded(MediaPlayer mediaPlayer, int type, int id) {
	    }

	    @Override
	    public void elementaryStreamDeleted(MediaPlayer mediaPlayer, int type, int id) {
	    }

	    @Override
	    public void elementaryStreamSelected(MediaPlayer mediaPlayer, int type, int id) {
	    }

	    @Override
	    public void corked(MediaPlayer mediaPlayer, boolean corked) {
	    }

	    @Override
	    public void muted(MediaPlayer mediaPlayer, boolean muted) {
	    }

	    @Override
	    public void volumeChanged(MediaPlayer mediaPlayer, float volume) {
	    }

	    @Override
	    public void audioDeviceChanged(MediaPlayer mediaPlayer, String audioDevice) {
	    }

	    @Override
	    public void chapterChanged(MediaPlayer mediaPlayer, int newChapter) {
	    }

	    @Override
	    public void error(MediaPlayer mediaPlayer) {
	    }

	    @Override
	    public void mediaMetaChanged(MediaPlayer mediaPlayer, int metaType) {
	    }

	    @Override
	    public void mediaSubItemAdded(MediaPlayer mediaPlayer, libvlc_media_t subItem) {
	    }

	    @Override
	    public void mediaDurationChanged(MediaPlayer mediaPlayer, long newDuration) {
	    }

	    @Override
	    public void mediaParsedChanged(MediaPlayer mediaPlayer, int newStatus) {
	    }

	    @Override
	    public void mediaFreed(MediaPlayer mediaPlayer) {
	    }

	    @Override
	    public void mediaStateChanged(MediaPlayer mediaPlayer, int newState) {
	    }

	    @Override
	    public void mediaSubItemTreeAdded(MediaPlayer mediaPlayer, libvlc_media_t item) {
	    }

	    @Override
	    public void newMedia(MediaPlayer mediaPlayer) {
	    }

	    @Override
	    public void subItemPlayed(MediaPlayer mediaPlayer, int subItemIndex) {
	    }

	    @Override
	    public void subItemFinished(MediaPlayer mediaPlayer, int subItemIndex) {
	    }

	    @Override
	    public void endOfSubItems(MediaPlayer mediaPlayer) {
	    }
	}
	private MouseListener mouseListener = new MouseListenerAdapter();
	
	public MouseListener getMouseListener() {
		return mouseListener;
	}

	public void setMouseListener(MouseListener mouseListener) {
		this.mouseListener = mouseListener;
	}

	public static class MouseListenerAdapter implements MouseListener {
	    @Override
	    public void mouseClicked(MouseEvent e) {
	    }

	    @Override
	    public void mousePressed(MouseEvent e) {
	    }

	    @Override
	    public void mouseReleased(MouseEvent e) {
	    }

	    @Override
	    public void mouseEntered(MouseEvent e) {
	    }

	    @Override
	    public void mouseExited(MouseEvent e) {
	    }
	}
    @Override
    public void mouseClicked(MouseEvent e) {
    	mouseListener.mouseClicked(e);
    }

    @Override
    public void mousePressed(MouseEvent e) {
    	mouseListener.mousePressed(e);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    	mouseListener.mouseReleased(e);
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    	mouseListener.mouseEntered(e);
    }

    @Override
    public void mouseExited(MouseEvent e) {
    	mouseListener.mouseExited(e);
    }
    
	private MouseMotionListener mouseMotionListener = new MouseMotionListenerAdapter();
	
	public MouseMotionListener getMouseMotionListener() {
		return mouseMotionListener;
	}

	public void setMouseMotionListener(MouseMotionListener mouseMotionListener) {
		this.mouseMotionListener = mouseMotionListener;
	}

	public static class MouseMotionListenerAdapter implements MouseMotionListener {
	    @Override
	    public void mouseDragged(MouseEvent e) {
	    }

	    @Override
	    public void mouseMoved(MouseEvent e) {
	    }
	}
    @Override
    public void mouseDragged(MouseEvent e) {
    	mouseMotionListener.mouseDragged(e);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    	mouseMotionListener.mouseMoved(e);
    }
	
	private MouseWheelListener mouseWheelListener = new MouseWheelListenerAdapter(); 
	
	public MouseWheelListener getMouseWheelListener() {
		return mouseWheelListener;
	}

	public void setMouseWheelListener(MouseWheelListener mouseWheelListener) {
		this.mouseWheelListener = mouseWheelListener;
	}

	public static class MouseWheelListenerAdapter implements MouseWheelListener {
	    @Override
	    public void mouseWheelMoved(MouseWheelEvent e) {
	    }
	}
    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
    	mouseWheelListener.mouseWheelMoved(e);
    }
    
	private KeyListener keyListener = new KeyListenerAdapter();
	
	public KeyListener getKeyListener() {
		return keyListener;
	}

	public void setKeyListener(KeyListener keyListener) {
		this.keyListener = keyListener;
	}

	public static class KeyListenerAdapter implements KeyListener {
	    @Override
	    public void keyTyped(KeyEvent e) {
	    }

	    @Override
	    public void keyPressed(KeyEvent e) {
	    }

	    @Override
	    public void keyReleased(KeyEvent e) {
	    }
	}
    @Override
    public void keyTyped(KeyEvent e) {
    	keyListener.keyTyped(e);
    }

    @Override
    public void keyPressed(KeyEvent e) {
    	keyListener.keyPressed(e);
    }

    @Override
    public void keyReleased(KeyEvent e) {
    	keyListener.keyReleased(e);
    }
}
