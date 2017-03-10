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
import java.io.FilenameFilter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import ru.org.sevn.audiobookplayer.MediaPlayerProxy.ChangeStateEvent;
import ru.org.sevn.audiobookplayer.MediaPlayerProxy.ChangeStateListener;
import ru.org.sevn.audiobookplayer.MediaPlayerProxy.STATE;

public abstract class MMediaPlayer implements ChangeStateListener {
    
    //https://developer.android.com/reference/android/media/MediaPlayer.html
    private MediaPlayerProxy mediaPlayer;
    private MediaPlayerSettings appSettings = new MediaPlayerSettings();
    private File[] files;
    private int playingFileIdx = -1;

    private Comparator<File> fileComparator = new FileNameComparator();
    
    private FilenameFilter filenameFilter = new FilenameFilter() {
        @Override
        public boolean accept(File dir, String filename) {
            File nfl = new File(dir, filename);
            if (nfl.isDirectory()) {
            	return (nfl.canRead());
            }
//TODO fix me
            if (filename.matches("(?i).*\\.mp[34]") || filename.matches("(?i).*\\.avi")) {
                if (!nfl.isDirectory()) {
                    return (nfl.canRead());
                }
            }
            return false;
        }
    };
    
    public void setFileNameFilter(FilenameFilter ff) {
    	this.filenameFilter = ff;
    }
    
    public boolean isInitialized() {
        return (mediaPlayer != null);
    }

    public synchronized void releaseMediaPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.removePropertyChangeListener(this);
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
    
    public synchronized void resetMediaPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.reset();
        }
    }
    
    public synchronized void restore(MediaPlayerSettings appSet, BitmapLoader bloader) {
        if (appSet != null && appSet.getPlayingDirPath() != null) {
            final int seek = appSet.getSeek();
            final int vol = appSet.getVolume();
            File lastFile = new File(appSet.getPlayingDirPath()); 
            if (lastFile.exists() && lastFile.canRead()) {
                restore(lastFile, appSet.getPlayingName(), new Runnable() {
                    public void run() {
                        mediaPlayer.seekTo(seek);
                        mediaPlayer.setVolume(vol);
                        toRunOnComplete = completePlayRun;
                    }
                }, bloader);
            } else {
            	ArrayList<BookInfo> books = new ArrayList<>(appSet.getLastBooks().values());
            	Collections.reverse(books);
            	for (BookInfo bi : books) {
            		if (bi.getDirInfo() != null && !bi.getDirInfo().isRemote() && bi.getDirInfo().getBookDir() != null) {
	            		setFile(bi.getDirInfo().getBookDir(), bloader, true, new Runnable() {
							
							@Override
							public void run() {
								autoPlayRun.run();
		                        mediaPlayer.setVolume(vol);
							}
						});
	            		break;
            		}
            	}
            }
        }
    }
    
    public static class ComplexRunnable implements Runnable {
    	private final Runnable[] runnables;

		@Override
		public void run() {
			for(Runnable r : runnables) {
				if (r != null) {
					r.run();
				}
			}
		}
		
		public ComplexRunnable(Runnable[] runnables) {
			this.runnables = runnables;
		}
    }

    private Runnable noPlayRun = new Runnable() {
        
        @Override
        public void run() {
            toRunOnComplete = completePlayRun;
        }
    };
    
    private void restore(File parentDir, String name, Runnable runnable, BitmapLoader bloader) {
    	
    	System.err.println("--------------"+parentDir+":"+name+":"+runnable);
    	if (parentDir != null) {
	        File[] fileList = BookInfo.getFileList(parentDir, filenameFilter);
	        Arrays.sort(fileList, fileComparator);
	        int i = 0;
	        if (name != null) {
	        	i = -1;
		        for (File fl : fileList) {
		            i++;
		            if (fl.getAbsolutePath().endsWith(name)) {
		                break;
		            }
		        }
	        }

	        if (fileList.length > 0) {
	        	if (i>=0 && i < fileList.length) {
	            	getAppSettings().lastBook(fileList[i], bloader);
	        	} else {
	            	getAppSettings().lastBook(null, bloader);
	        	}
	        }
	        
	        BookInfo bi = getAppSettings().getLastBook();
	        if (bi != null && bi.getDirInfo() != null) {
	        	bi.getDirInfo().refresh(bloader);
	        }
	        final int seek;
	        if ( bi != null && i>=0) {
	        	seek = bi.getSeek();
	        } else {
	        	seek = 0;
	        }
	        ComplexRunnable seekAndRun = new ComplexRunnable(new Runnable[]{
	        		new Runnable() {
	        			public void run() {
	        				mediaPlayer.start(seek);
	        			}
	        		},
	        		runnable});
	        		
	        startPlaying(fileList, parentDir.getAbsolutePath(), i, seekAndRun);
    	}
    }
    
    //public synchronized void setFile(Uri uri, BitmapLoader bloader) {
    public synchronized void setFile(File selectedFile, BitmapLoader bloader) {
    	setFile(selectedFile, bloader, false);
    }
    public synchronized void setFile(File selectedFile, BitmapLoader bloader, boolean inLastOpened) {
    	setFile(selectedFile, bloader, inLastOpened, autoPlayRun);
    }
    public synchronized void setFile(File selectedFile, BitmapLoader bloader, boolean inLastOpened, Runnable r) {
        File parentDir = selectedFile;
        String file2play = null;
        if (!selectedFile.isDirectory()) {
            parentDir = BookInfo.findBookDirFile(selectedFile.getAbsolutePath(), bloader);
        	file2play = BookInfo.getRelativeFileName(parentDir, selectedFile);
        } else {
            parentDir = BookInfo.findBookDirFile(new File(selectedFile, "fake").getAbsolutePath(), bloader);
        	if (inLastOpened && parentDir != null) {
        		BookInfo bi = appSettings.getLastBooks().get(parentDir.getAbsolutePath());
        		if (bi != null) {
        			file2play = bi.getFileName();
        		}
        	}
        }
        restore(parentDir, file2play, r, bloader);
    }
    
    private void startPlaying(File[] fileList, String playingDir, int idx, Runnable aPlay) {
        stopPlaying();
        files = fileList;
        playingFileIdx = idx - 1;
        appSettings.setPlayingName(null);
        appSettings.setPlayingDirPath(playingDir);
        startPlaying(aPlay);
    }
    
    public synchronized void stopPlaying() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
    }
    public synchronized void seekFromCurrent(int i) {
        if (mediaPlayer != null) {
            mediaPlayer.seekFromCurrent(i);
        }
    }
    
    public int getFilesLength() {
        if (files != null) {
            return files.length;
        }
        return 0;
    }
    
    public synchronized boolean startPlaying() {
        return startPlaying(autoPlayRun);
    }
    private boolean startPlaying(Runnable aPlay) {
        if (getFilesLength() > 0) {
            return playNext(aPlay);
        }
        return false;        
    }
    
    private Runnable getAutoPlayRun() {
        if (getMediaPlayer()!=null && !getMediaPlayer().isPlaying()) {
            return noPlayRun;
        }
        return autoPlayRun;
    }
    public synchronized boolean playNext() {
        return playNext(getAutoPlayRun());
    }
    protected boolean playNext(Runnable aPlay) {
        playingFileIdx++; 
        if (playingFileIdx >= getFilesLength()) {
            if (appSettings.isLoop()) {
                playingFileIdx = 0;
            } else {
                playingFileIdx = getFilesLength();
            }
        }
        return playCurrent(aPlay);
    }
    
    public synchronized boolean playPrev() {
        return playPrev(getAutoPlayRun());
    }
    protected boolean playPrev(Runnable aPlay) {
        playingFileIdx--; 
        if (playingFileIdx < 0) {
            if (appSettings.isLoop()) {
                playingFileIdx = getFilesLength() - 1;
            } else {
                playingFileIdx = -1;
            }
        }
        return playCurrent(aPlay);
    }
    
    public synchronized boolean playTrack(int idx) {
        return playTrack(idx, getAutoPlayRun());
    }
    protected synchronized boolean playTrack(int idx, Runnable aPlay) {
        playingFileIdx = idx - 1;
        return playNext(aPlay);
    }
    
    public synchronized boolean playCurrent() {
        return playCurrent(getAutoPlayRun());
    }
    protected boolean playCurrent(Runnable aPlay) {
        toRunOnComplete = null;
        toRunOnPrepare = null;
        appSettings.setPlayingName(null);
        if (playingFileIdx >=0 && playingFileIdx < getFilesLength()) {
            return play(files[playingFileIdx], aPlay);
        }
        return false;
    }
    
    public synchronized void initialize() {
        if (mediaPlayer == null) {
            mediaPlayer = createMediaPlayer();
            mediaPlayer.addChangeStateListener(this);
        }
    }
    
	protected abstract MediaPlayerProxy createMediaPlayer();
    
    private Runnable toRunOnPrepare;
    private Runnable toRunOnComplete;
    
    private Runnable autoPlayRun = new Runnable() {
        public void run() {
            resumePlaying();
            toRunOnComplete = completePlayRun;
        }
    };
    private Runnable completePlayRun = new Runnable() {
        public void run() {
            playNext(autoPlayRun);
        }
    };

    protected abstract void retrieveMediaInfo(File selectedFile);
    /*
    protected void retrieveMediaInfo(File selectedFile) {
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(selectedFile.getAbsolutePath());
        getAppSettings().setImage(mmr.getEmbeddedPicture());
        //DONT USE. raises fatal error in VM for wrong file tags
        //getAppSettings().setTitle(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE));
    }
    */
    private boolean play(File selectedFile, final Runnable aPlay) {
    	System.err.println(">>>>>>>>>>>>>>>>>"+selectedFile);
        resetMediaPlayer();
        initialize();
        if (selectedFile != null && selectedFile.exists() && selectedFile.canRead()) {
            try {
            	retrieveMediaInfo(selectedFile);
	            getAppSettings().setPlayingName(BookInfo.getRelativeFileName(getAppSettings().getPlayingDirPath(), selectedFile));
	            
	            String titlePrefix = "";
	            if (getAppSettings().getPlayingDirPath() != null) {
	            	File fl = new File(getAppSettings().getPlayingDirPath());
	            	titlePrefix = fl.getName();
	            	if("book".equals(titlePrefix.toLowerCase()) && fl.getParentFile() != null) {
	            		titlePrefix = fl.getParentFile().getName();
	            	}
	            }
	            getAppSettings().setTitle(titlePrefix + ": " + selectedFile.getName());
	            
                toRunOnPrepare = aPlay;
                mediaPlayer.prepare(selectedFile.getAbsolutePath());
                return true;
            } catch (Exception e) {
            }
        }
        return false;
    }
    
    public synchronized boolean resumePlaying() {
        if (mediaPlayer != null) {
            if (!mediaPlayer.isPlaying()) {
                mediaPlayer.start(mediaPlayer.getCurrentPosition());
                return true;
            }
        }
        return false;
    }
    
    public synchronized boolean pausePlaying() {
        if (mediaPlayer != null) {
        	if (mediaPlayer.isPlaying()) {
        		mediaPlayer.pause();
        	}
            return true;
        }
        return false;
    }
    
    public void setTitleEncoding(String enc) {
        appSettings.setCharsetName(enc);
    }
    
    public String getTitleEncoded() {
        return getTitleEncoded(appSettings.getTitle(), appSettings.getCharsetName());
    }
    
    private String getTitleEncoded(String msg, String encoding) {
        if (msg != null) {
            String encW = "";
            try {
                for (char ch : msg.toCharArray() ) {
                    if (ch < 256) {
                        byte bt[] = new byte[1];
                        bt[0] = (byte)ch;
                        encW += new String(bt, encoding);
                    } else {
                        encW += ch;
                    }
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return encW;
        }
        return msg;
    }

    public MediaPlayerProxy getMediaPlayer() {
        return mediaPlayer;
    }

    public synchronized void startPlayingFirst() {
        startPlaying(files, appSettings.getPlayingDirPath(), 0, autoPlayRun);
    }
    
    public MediaPlayerSettings getAppSettings() {
        return appSettings;
    }

    public int getPlayingFileIdx() {
        return playingFileIdx;
    }

    @Override
    public void onChangeStateEvent(ChangeStateEvent evt) {
        Runnable toRun = null;
        if (evt.getState() == STATE.COMPLETED && mediaPlayer != null) {
                toRun = toRunOnComplete;
                toRunOnComplete = null;
        } else
        if (evt.getState() == STATE.PREPARED && mediaPlayer != null) {
            toRun = toRunOnPrepare;
            toRunOnPrepare = null;
        }
        if (toRun != null) {
            toRun.run();
        }
    }

//    private final BroadcastReceiver audioOutputChangedEventReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            if(intent == null)
//                return;
//
//            String action = intent.getAction();
//            if(TextUtils.isEmpty(action))
//                return;
//
//            if(AudioManager.ACTION_AUDIO_BECOMING_NOISY.equals(action)) {
//                onReceiveActionAudioBecomingNoisy(intent);
//            } else if(Intent.ACTION_HEADSET_PLUG.equals(action)) {
//                onReceiveActionHeadsetPlug(intent);
//            } else if(
//                BluetoothDevice.ACTION_ACL_CONNECTED.equals(action) ||
//                BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
//                onReceiveActionAclConnection(intent);
//            }
//        }
//
//        private void onReceiveActionAudioBecomingNoisy(Intent intent) {
//            pauseTrack();
//        }
//
//        private void onReceiveActionHeadsetPlug(Intent intent) {
//            int state = intent.getIntExtra("state", -1);
//            if(state == -1) {
//                Log.d(TAG, "Unknown headset plug event parameter.");
//                return;
//            }
//
//            if(state == 0) {    // Disconnected
//                showNotification(getCurrentAudioPathOtherThanWired());
//            } else {            // Connected
//                showNotification(AUDIO_PATH_WIRED);
//            }
//        }
//
//        private void onReceiveActionAclConnection(Intent intent) {
//            Log.d(TAG, "onReceiveActionAclConnection action: " + intent.getAction());
//
//            String action = intent.getAction();
//            if(BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
//                showNotification(AUDIO_PATH_A2DP);
//            } else {
//                showNotification(getCurrentAudioPathOtherThanA2dp());
//            }
//        }
//    };
}
