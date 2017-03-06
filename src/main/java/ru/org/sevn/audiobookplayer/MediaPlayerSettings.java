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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class MediaPlayerSettings {
    
    private transient ArrayList<PropertyChangeListener> propertyChangeListeners = new ArrayList<>();
    
    private String charsetName = "UTF-8";
    private String playingDirPath;
    private String playingName;
    private String title;
    private int seek;
    private boolean loop;
    private byte [] image;
    private int volume;

    private BookInfo lastBook;
    private LinkedHashMap<String, BookInfo> lastBooks = new LinkedHashMap<>();
    
    public MediaPlayerSettings getCopy() {
    	MediaPlayerSettings ret = new MediaPlayerSettings();
    	ret.charsetName = charsetName;
    	ret.playingDirPath = playingDirPath;
    	ret.playingName = playingName;
    	ret.title = title;
    	ret.seek = seek;
    	ret.loop = loop;
    	ret.volume = volume;
    	for (String k : lastBooks.keySet()) {
    		ret.lastBooks.put(k, lastBooks.get(k));
    	}
    	return ret;
    }
    
    public void lastBook(File fl, BitmapLoader bloader) {
    	BookInfo bi = null;
    	if (fl != null) {
    		bi = BookInfo.makeBookInfo(fl.getAbsolutePath(), 0, null, bloader);
    	}
    	lastBook(bi);
    }
    private void lastBook(BookInfo bi) {
		lastBook = null;
    	if (bi != null) {
	    	String k = bi.getDirInfo().getBookDir().getAbsolutePath();
	    	if (k != null) {
	    		BookInfo fbi = lastBooks.get(k);
	    		if (fbi != null) {
	    			lastBooks.remove(k);
	    			bi = fbi;
	    		}
	    		lastBooks.put(k, bi);
	    		lastBook = bi;
    			int rem2 = lastBooks.size() - 15;
	    		if (rem2 > 0) {
	    			
	    	    	for (String key : new ArrayList<String>(lastBooks.keySet())) {
	    	    		if(rem2>0) {
	    	    			lastBooks.remove(key);
	    	    		} else {
	    	    			break;
	    	    		}
	    	    		rem2--;
	    	    	}
	    		}
	    	}
    	}
    }
    
    public String getCharsetName() {
        return charsetName;
    }
    public void setCharsetName(String charsetName) {
        changeProperty("charsetName", this.charsetName, this.charsetName = charsetName);
    }
    public String getPlayingDirPath() {
        return playingDirPath;
    }
    public void setPlayingDirPath(String playingDirPath) {
        changeProperty("playingDirPath", this.playingDirPath, this.playingDirPath = playingDirPath);
    }
    public String getPlayingName() {
        return playingName;
    }
    public void setPlayingName(String playingName) {
        changeProperty("playingName", this.playingName, this.playingName = playingName);
        if (lastBook != null) {
        	lastBook.setFileName(playingName);
        }
    }
    
    public int getVolume() {
		return volume;
	}

	public void setVolume(int volume) {
        changeProperty("volume", this.volume, this.volume = volume);
	}

	public File getPlayingFile() {
        File fl = null;
        if (playingDirPath != null && playingName != null) {
            fl = new File(playingDirPath, playingName);
        }
        return fl;
    }
    public boolean isLoop() {
        return loop;
    }
    public void setLoop(boolean loop) {
        changeProperty("loop", this.loop, this.loop = loop);
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        changeProperty("title", this.title, this.title = title);
    }
    
    public int getSeek() {
        return seek;
    }
    public void setSeek(int seek) {
        changeProperty("seek", this.seek, this.seek = seek);
        if (lastBook != null) {
        	lastBook.setSeek(seek);
        }
    }

    transient BitmapContainer bitmapImage;
    
    public BitmapContainer getBitmapImage(BitmapLoader bloader) {
        if (bitmapImage == null && image != null) {
            bitmapImage = bloader.decodeByteArray(image, 0, image.length);
        }
        return bitmapImage;
    }
    
    transient BitmapContainer bitmapLargeIcon;
    
    public BitmapContainer getBitmapLargeImage(int w, int h, float f, BitmapLoader bloader) {
        if (bitmapLargeIcon == null && image != null) {
            bitmapLargeIcon = bloader.getScaledImage(getBitmapImage(bloader), w, h, f);
        }
        return bitmapLargeIcon;
    }
    
    public byte[] getImage() {
        return image;
    }
    public void setImage(byte[] image) {
        bitmapImage = null;
        bitmapLargeIcon = null;
        changeProperty("image", this.image, this.image = image);
    }
    
    public void addPropertyChangeListener(PropertyChangeListener l) {
        propertyChangeListeners.add(l);
    }
    public void removePropertyChangeListener(PropertyChangeListener l) {
        propertyChangeListeners.remove(l);
    }
    private void changeProperty(String propertyName, Object oldValue, Object newValue) {
        PropertyChangeEvent evt = null;
        if ( (oldValue != null && newValue != null && !oldValue.equals(newValue)) || oldValue != newValue) {
            evt = new PropertyChangeEvent(this, propertyName, oldValue, newValue);
        }
        if (evt != null) {
            for (PropertyChangeListener l : propertyChangeListeners) {
                l.propertyChange(evt);
            }
        }
    }

	public BookInfo getLastBook() {
		return lastBook;
	}

	public LinkedHashMap<String, BookInfo> getLastBooks() {
		return lastBooks;
	}
}
