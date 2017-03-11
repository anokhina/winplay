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

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.SwingUtilities;

import ru.org.sevn.audiobookplayer.MediaPlayerProxy;
import ru.org.sevn.audiobookplayer.MediaPlayerProxy.CantPlayException;
import ru.org.sevn.audiobookplayer.MediaPlayerProxy.STATE;
import uk.co.caprica.vlcj.binding.internal.libvlc_media_t;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.player.MediaPlayerEventListener;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
//http://capricasoftware.co.uk/#/projects/vlcj/tutorial/media-info

public class WinMediaPlayer extends MediaPlayerProxy<EmbeddedMediaPlayer, String>  {

	private MediaPlayerEventListener mediaPlayerEventListener;
	
    public void setMediaPlayer(EmbeddedMediaPlayer mediaPlayer) {
    	if (getMediaPlayer() != null) {
    		getMediaPlayer().removeMediaPlayerEventListener(getMediaPlayerEventListener());
    	}
    	super.setMediaPlayer(mediaPlayer);
    	if (getMediaPlayer() != null) {
    		getMediaPlayer().addMediaPlayerEventListener(getMediaPlayerEventListener());
    	}
    }
    
	protected MediaPlayerEventListener getMediaPlayerEventListener() {
		if (mediaPlayerEventListener == null) {
			mediaPlayerEventListener = makeMediaPlayerEventListener();
		}
		return mediaPlayerEventListener;
	}
	
	protected MediaPlayerEventListener makeMediaPlayerEventListener() {
		return new MediaPlayerEventAdapter() {
		    @Override
		    public void mediaChanged(MediaPlayer mediaPlayer, libvlc_media_t media, String mrl) {
		    	synchronized (WinMediaPlayer.this) {
			        prepared = true;
				}
		        fireEvent(STATE.PREPARED);
		    }
		    @Override
		    public void finished(MediaPlayer mediaPlayer) {
		        //if (getDuration() >= 0 && getCurrentPosition()+1000 >= getDuration()) {
		            fireEvent(STATE.COMPLETED);
		        //}
		    }
		};
	}

	private boolean prepared = false;
    
	private ScheduledExecutorService executor;	
    final long delayMillis = 1000;
    private Runnable playingRun = new Runnable() {
        @Override
        public void run() {
            if (isPrepared() && isPlaying()) {
                fireEvent(STATE.PLAYING);
            }
        }		
	};
	
    public WinMediaPlayer(EmbeddedMediaPlayer mp) {
        super(mp);
    }

    @Override
    public synchronized void release() {
    	prepared = false;
        getMediaPlayer().release();
        getMediaPlayer().removeMediaPlayerEventListener(mediaPlayerEventListener);
        if (executor != null) {
        	executor.shutdown();
        	executor = null;
        }
        
        super.release();
    }

    @Override
    public synchronized void reset() {
    	//TODO
        //getMediaPlayer().reset();
        super.reset();
    }

    @Override
    public synchronized void pause() {
        if (isPrepared() && getMediaPlayer().isPlaying()) {
        	SwingUtilities.invokeLater(new Runnable(){
				public void run() {
		            getMediaPlayer().pause();
				}});
            super.pause();
        }
    }

    @Override
    public synchronized void seekTo(int i) {
        if (isPrepared()) {
            getMediaPlayer().setTime(i);
            super.seekTo(i);
        }
    }

    @Override
    public synchronized void setVolume(int i) {
        if (isPrepared()) {
            getMediaPlayer().setVolume(i);
            super.setVolume(i);
        }
    }
    
    @Override
    public synchronized boolean isPlaying() {
        return getMediaPlayer().isPlaying();
    }

    @Override
    public synchronized void start() {
    	start(-1);
    }
    
    @Override
    public synchronized void start(final int seek) {
        if (isPrepared()) {
        	SwingUtilities.invokeLater(new Runnable(){
				public void run() {
					getMediaPlayer().start();
		        	if (seek >= 0) {
		        		getMediaPlayer().setTime(seek);
		        	}
				}});
        	if (seek >= 0) {
        		super.start(seek);
        	} else {
        		super.start();
        	}
            if (executor == null) {
            	executor = Executors.newSingleThreadScheduledExecutor();
            	executor.scheduleAtFixedRate(playingRun, this.delayMillis, this.delayMillis, TimeUnit.MILLISECONDS);
            }
        }
    }

    @Override
    public synchronized void prepare(String dataSource) {
        prepared = false;
        
        EmbeddedMediaPlayer mediaPlayer = getMediaPlayer();
        
        try {
        	System.err.println("******************>>>"+dataSource);
            mediaPlayer.prepareMedia(dataSource);
            super.prepare(dataSource);
        } catch (Exception e) {
            if (dataSource == null) {
                super.prepare(dataSource);
            }
            fireEvent(STATE.ERROR, new CantPlayException(dataSource, e));
        }
    }

    @Override
    public synchronized void seekFromCurrent(int i) {
        if (isPrepared() && getMediaPlayer() != null) {
            seekTo(getCurrentPosition() + i);
        }
    }

    @Override
    public synchronized int getCurrentPosition() {
        if (isPrepared() && getMediaPlayer() != null) {
            return (int)getMediaPlayer().getTime();
        }
        return 0;
    }

    @Override
    public synchronized int getDuration() {
        if (isPrepared() && getMediaPlayer() != null) {
            return (int)getMediaPlayer().getLength();
        }
        return 0;
    }

    @Override
    public synchronized int getVolume() {
        if (isPrepared() && getMediaPlayer() != null) {
            return getMediaPlayer().getVolume();
        }
        return 0;
    }
    
    public synchronized boolean isPrepared() {
        return prepared;
    }

}
