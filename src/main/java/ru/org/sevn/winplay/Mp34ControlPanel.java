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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.Set;
import java.util.prefs.BackingStoreException;
import java.util.prefs.InvalidPreferencesFormatException;
import java.util.prefs.Preferences;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.basic.BasicSliderUI;

import ru.org.sevn.audiobookplayer.AppSettings;
import ru.org.sevn.audiobookplayer.AppSettings.EditorWrapper;
import ru.org.sevn.audiobookplayer.AppSettings.SharedPreferencesWrapper;
import ru.org.sevn.audiobookplayer.DirInfo;
import ru.org.sevn.audiobookplayer.MediaPlayerProxy;
import ru.org.sevn.audiobookplayer.MediaPlayerProxy.ChangeStateEvent;
import ru.org.sevn.audiobookplayer.MediaPlayerProxy.ChangeStateListener;
import ru.org.sevn.audiobookplayer.Mp34Player;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;

public class Mp34ControlPanel extends JPanel {
	private static class MyJFileChooser extends JFileChooser {
		public MyJFileChooser() {
			try {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			} catch (Exception e) {
				e.printStackTrace();
			}
			SwingUtilities.updateComponentTreeUI(this);
		}
	}
	private static class MyJButton extends JButton {
		public MyJButton(String ttext, Icon i) {
			super(i);
			setBackground(Color.WHITE);
			setMargin(new Insets(2, 3, 2, 3));
			setToolTipText(ttext);
		}
	}
    private JButton fileOpenButton = new MyJButton("File open", Utils.createImageIcon("/drawable/folder_10.png"));
    private JButton playButton = new MyJButton("Play", Utils.createImageIcon("/drawable/play_button_1.png"));
    private JButton pauseButton = new MyJButton("Pause", Utils.createImageIcon("/drawable/pause_1.png"));
    private JButton stopButton = new MyJButton("STOP", Utils.createImageIcon("/drawable/stop.png"));
    private JButton prevButton = new MyJButton("prev", Utils.createImageIcon("/drawable/previous.png"));
    private JButton nextButton = new MyJButton("next", Utils.createImageIcon("/drawable/skip.png"));
    private JButton backButton = new MyJButton("back", Utils.createImageIcon("/drawable/rewind_1.png"));
    private JButton fwrdButton = new MyJButton("fwrd", Utils.createImageIcon("/drawable/fast_forward_1.png"));
    private JButton firstButton = new MyJButton("first", Utils.createImageIcon("/drawable/back.png"));
    private JSlider slider = new JSlider(JSlider.HORIZONTAL, 0, 100, 0);
    private JSlider pbarFiles = new JSlider(JSlider.HORIZONTAL, 0, 100, 0);
    private JSlider volume = new JSlider(JSlider.HORIZONTAL, 0, 200, 0);
    private ImagePreview imagePreview = new ImagePreview();
    
    private JTextArea textArea = new JTextArea(3, 20);
    
    private JFileChooser fileChooser;
    private final Mp34Player mp34Player;
    
    private ChangeStateListener guilistener;
    
	public ListenEmbeddedMediaPlayerComponent getMediaPlayerComponent() {
		return mp34Player.getMediaPlayerComponent();
	}
	
	public Mp34Player getMp34Player() {
		return mp34Player;
	}

	public Mp34ControlPanel(Mp34Player cmp) {
		super(new BorderLayout());
		JPanel buttons = new JPanel(new FlowLayout());
		this.add(slider, BorderLayout.NORTH);
		JPanel center = new JPanel(new BorderLayout());
		add(center, BorderLayout.CENTER);
		center.add(pbarFiles, BorderLayout.NORTH);
		center.add(textArea, BorderLayout.CENTER);
		center.add(volume, BorderLayout.SOUTH);
		this.add(buttons, BorderLayout.SOUTH);
		mp34Player = cmp;
		
		//saveLastOpened();
		//saveSeek((Integer)evt.getExtra());
		guilistener = new ChangeStateListener(){

			@Override
			public void onChangeStateEvent(ChangeStateEvent evt) {
				switch(evt.getState()) {
				case CHANGE_DS:
					saveLastOpened();
				case START:
				case PREPARED:
				    showMediaInfo();
				    break;
				case SEEK:
				case PAUSE:
				case STOP:
					saveSeek();
			    	commitSettings();
				case PLAYING:
					showVolume();
					saveSeek();
				case COMPLETED:
				    showTrackBar();
				    showInfo();
				    break;
				case ERROR:
//				    Util.toast("ERROR:" + msg.getData().getString("msg"), ho);
//		            CantPlayException e = (CantPlayException)evt.getExtra();
//		            notify(getApplicationContext(), "ERROR", e.getMessage());
				    break;
				case RESET:
				case RELEASE:
					
				}
				
			}
		};
		mp34Player.initialize();
		restoreSettings();
		PropertyChangeListener propertiesChanged = new PropertyChangeListener() {
			
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				AppSettings.saveSettings(prefs, evt.getPropertyName(), mp34Player.getAppSettings());
			}
		}; 
		mp34Player.getAppSettings().addPropertyChangeListener(propertiesChanged); //TODO
		mp34Player.getMediaPlayer().addChangeStateListener(guilistener); //TODO
		
	    textArea.setText("");
	    textArea.setWrapStyleWord(true);
	    textArea.setLineWrap(true);
	    textArea.setOpaque(false);
	    textArea.setEditable(false);
	    textArea.setFocusable(false);
	    textArea.setBackground(UIManager.getColor("Label.background"));
	    textArea.setFont(UIManager.getFont("Label.font"));
	    textArea.setBorder(UIManager.getBorder("Label.border"));
		
		
		buttons.add(fileOpenButton);
		buttons.add(stopButton);
		buttons.add(playButton);
		buttons.add(pauseButton);
		buttons.add(firstButton);
		buttons.add(prevButton);
		buttons.add(backButton);
		buttons.add(fwrdButton);
		buttons.add(nextButton);
		
		stopButton.addActionListener(e -> {mp34Player.stopPlaying();} );
		playButton.addActionListener(e -> {mp34Player.resumePlaying();} );
		pauseButton.addActionListener(e -> {mp34Player.pausePlaying();} );
		prevButton.addActionListener(e -> {mp34Player.playPrev();} );
		nextButton.addActionListener(e -> {mp34Player.playNext();} );
		firstButton.addActionListener(e -> {mp34Player.startPlayingFirst();} );
		backButton.addActionListener(e -> {mp34Player.seekFromCurrent(-3000);} );
		fwrdButton.addActionListener(e -> {mp34Player.seekFromCurrent(3000);} );
		
        fileOpenButton.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				File file2open = null;
		        if (fileChooser == null) {
		            fileChooser = new MyJFileChooser();
		            file2open = new File(".");
		            if (mp34Player.getAppSettings().getLastBook() != null &&
	            		mp34Player.getAppSettings().getLastBook().getDirInfo() != null &&
        				mp34Player.getAppSettings().getLastBook().getDirInfo().getBookDir() != null) {
		            	
		            	File upDir = mp34Player.getAppSettings().getLastBook().getDirInfo().getBookDir().getParentFile();
		            	if (upDir.exists() && upDir.canRead() && upDir.isDirectory()) {
		            		file2open = upDir;
		            	}
		            }
		            fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		            
		            fileChooser.addChoosableFileFilter(new PatternFileChooserFilter("", "Media Books"));
		            fileChooser.setAcceptAllFileFilterUsed(false);
		 
		            fileChooser.setFileView(new ImageFileView());
		            
		            fileChooser.setAccessory(new BookPreview(fileChooser, 
		            		new BookInfoListModel(mp34Player.getAppSettings()) ));
		        } else {
		        	file2open = fileChooser.getSelectedFile();
			        JList lst = ((BookPreview)fileChooser.getAccessory()).getLastOpenedList();
			        lst.setModel(new BookInfoListModel(mp34Player.getAppSettings()));
		        }
	            fileChooser.setSelectedFile(file2open);
	            mp34Player.pausePlaying();
		        int returnVal = fileChooser.showDialog(Mp34ControlPanel.this, "Open");
		 
		        if (returnVal == JFileChooser.APPROVE_OPTION) {
		            File file = fileChooser.getSelectedFile();
		            DirInfo<ImageIcon> di = new DirInfo<>(file, WinDirInfoBitmapLoader.getDefaultLoader());
		            
		            if (di.getBookDir() != null) {
	            		mp34Player.setFile(di.getBookDir(), WinDirInfoBitmapLoader.getDefaultLoader(), true);
		            }
		        } else {
		        	//File open cancelled by user.
		        }
		 
		        //Reset the file chooser for the next time it's shown.
		        //fc.setSelectedFile(null);				
			}
		});
        slider.addChangeListener(new ChangeListener() {
            private int tmpVal = -1;
			
			@Override
			public void stateChanged(ChangeEvent e) {
				if (slider.getValueIsAdjusting()) {
					tmpVal = slider.getValue();
				} else {
					if (tmpVal >= 0) {
						int setVal = tmpVal;
						tmpVal = -1;
						mp34Player.getMediaPlayer().seekTo(setVal);
					}
				}
			}
		});
        //playTrack
        pbarFiles.addChangeListener(new ChangeListener() {
            private int tmpVal = -1;
			
			@Override
			public void stateChanged(ChangeEvent e) {
				if (pbarFiles.getValueIsAdjusting()) {
					tmpVal = pbarFiles.getValue();
				} else {
					if (tmpVal >= 0) {
						int setVal = tmpVal;
						tmpVal = -1;
						mp34Player.playTrack(setVal);
					}
				}
			}
		});

        volume.putClientProperty("Slider.paintThumbArrowShape", Boolean.FALSE);
        volume.setUI(new BasicSliderUI(volume) {
        	Font font = new Font("Courier", Font.BOLD, 12);
            protected Dimension getThumbSize() {
                Dimension size = new Dimension();

                if ( slider.getOrientation() == JSlider.VERTICAL ) {
                    size.width = 25;
                    size.height = 23;
                }
                else {
                    size.width = 23;
                    size.height = 25;
                }

                return size;
            }        	

			public void paintThumb(Graphics g) {
				super.paintThumb(g);
				g.setColor(Color.red);
				g.setFont(font);
				String val = Integer.toString(slider.getValue());
				if (slider.getValue() < 100) val = "0"+val;
				if (slider.getValue() < 10) val = "0"+val;
				g.drawString(val, thumbRect.x, thumbRect.y + thumbRect.height - 8);
			}
        });
        volume.addChangeListener(new ChangeListener() {
            private int tmpVal = -1;
			
			@Override
			public void stateChanged(ChangeEvent e) {
				if (volume.getValueIsAdjusting()) {
					tmpVal = volume.getValue();
				} else {
					if (tmpVal >= 0) {
						int setVal = tmpVal;
						tmpVal = -1;
						////////////////////////
						mp34Player.getMediaPlayer().setVolume(setVal);
//			    		EmbeddedMediaPlayer mp = (EmbeddedMediaPlayer)mp34Player.getMediaPlayer().getMediaPlayer();
//			    		mp.setVolume(setVal);
			    		mp34Player.getAppSettings().setVolume(setVal);
			    		commitSettings();
					}
				}
			}
		});
        
        //TODO restore seek
		mp34Player.restore(mp34Player.getAppSettings().getCopy(), WinDirInfoBitmapLoader.getDefaultLoader());

	}
    private void showMediaInfo() {
        showLoopState();
        showTrackBar();
        showTrackSBar();
        showTitle();
        showImage();
        showInfo();
    }
    private void showLoopState() {
//        if (chbLoop != null && getMp3Player() != null && getMp3Player().getAppSettings() != null) {
//            //    T F
//            // T  F T
//            // F  T F
//            boolean checked = chbLoop.isChecked();
//            boolean checkedApp = getMp3Player().getAppSettings().isLoop();
//            if (checked^checkedApp) {
//                chbLoop.setChecked(checkedApp);
//            }
//        }
    }
    private void showTrackSBar() {
    	if (!pbarFiles.getValueIsAdjusting()) {
	    	int m = mp34Player.getFilesLength();
            if (m > 0) { m--; }
	        if (m != pbarFiles.getMaximum()) {
	    		pbarFiles.setMaximum(m);
	    	}
	    	this.pbarFiles.setValue(mp34Player.getPlayingFileIdx());
    	}
    }
    private void showTrackBar() {
    	if (!slider.getValueIsAdjusting()) {
	    	int m = mp34Player.getMediaPlayer().getDuration();
	    	if (m != slider.getMaximum()) {
	    		slider.setMaximum(m);
	    	}
	    	this.slider.setValue(mp34Player.getMediaPlayer().getCurrentPosition());
    	}
    }
    private void showVolume() {
    	if (!volume.getValueIsAdjusting()) {
    		EmbeddedMediaPlayer mp = (EmbeddedMediaPlayer)mp34Player.getMediaPlayer().getMediaPlayer();
	    	int m = mp.getVolume();
	    	if (m >= 0) { 
	    		volume.setValue(m);
	    		mp34Player.getAppSettings().setVolume(m);
	    		commitSettings();
	    	}
    	}
    }
    private void showImage() {
    	this.imagePreview.setImageIcon(null);
    	
    	try {
    		ImageIcon icon = (ImageIcon)mp34Player.getAppSettings().getLastBook().getDirInfo().getBitmapContainer().getBitmap();
    		imagePreview.setImageIcon(icon);
    		//imagePreview.getLabel().setText(mp34Player.getAppSettings().getTitle());
    		imagePreview.repaint();
    	} catch (Exception e) {
    		e.printStackTrace(System.err);
    	}
    }
    
    private void showTitle() {
		imagePreview.getLabel().setText(mp34Player.getAppSettings().getTitle());
//        TextView tv = (TextView)findViewById(R.id.tvTitle);
//        if (tv != null) {
//            String title = getMp3Player().getTitleEncoded();
//            if (title == null) { title = ""; }
//            tv.setText(getMp3Player().getAppSettings().getCharsetName()+": "+title);
//        }
    }
    
    synchronized void showInfo() {
    	textArea.setText(getPlayingMessage(mp34Player.getMediaPlayer(), false));
    }
    public static String getPlayingMessage(MediaPlayerProxy mediaPLayer, boolean isshort) {
        String msg = "";
        if (mediaPLayer != null) {
            if (!isshort && mediaPLayer.isPlaying()) {
                msg += "PLAYING ";
            }
            msg += ("Time: " + getTimeString(mediaPLayer.getCurrentPosition()) + " / "
                    + getTimeString(mediaPLayer.getDuration()) +  " ");
        }
//        if (!isshort && audioManager != null) {
//            msg += ("Volume: " + audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
//        }
        if (!isshort) {
        	msg += ("" + mediaPLayer.getDataSource());
        }
        return msg;
    }
    
    private static String getTimeString(int ms) {
        int s = ms / 1000; 
        int h = s / 60 / 60;
        int m = (s - h * 60 * 60) / 60 ;
        s -= (h * 60 * 60 + m * 60);
        return "" + getTimeUnitString(h) + ":" + getTimeUnitString(m) + ":" + getTimeUnitString(s);
    }
    
    private static String getTimeUnitString(int ms) {
        if (ms < 10) {
            return "0" + ms;
        }
        return "" + ms;
    }
    
    private WinEditorWrapper prefs;    
    public void saveSettings() {
    	AppSettings.saveSettings(prefs, null, mp34Player.getAppSettings());
    	saveLastOpened();
    }
    
    public void saveLastOpened() {
    	AppSettings.saveLastOpened(prefs, mp34Player.getAppSettings());
    	commitSettings();
    }
    
    public synchronized void commitSettings() {
    	try {
			commit(PREF_FILE_NAME, prefs.getWrapped());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BackingStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public void restoreSettings() {
    	prefs = new WinEditorWrapper(Preferences.userRoot().node(this.getClass().getName()));
    	try {
			restore(PREF_FILE_NAME, prefs.getWrapped());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidPreferencesFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	AppSettings.restoreSettings(mp34Player.getAppSettings(), new WinSharedPreferencesWrapper(prefs.getWrapped()), WinDirInfoBitmapLoader.getDefaultLoader());
    }
    
    private static void restore(String fileName, Preferences prefs) throws IOException, InvalidPreferencesFormatException  {
    	InputStream is = new BufferedInputStream(new FileInputStream(fileName));
    	try {
    		prefs.importPreferences(is);
    	} finally {
        	is.close();
		}
    }
    private static void commit(String fileName, Preferences prefs) throws IOException, BackingStoreException {
        OutputStream os = new BufferedOutputStream(new FileOutputStream(fileName));
        try {
        	prefs.exportSubtree(os);
        } finally {
        	os.close();
        }
    }
    
    public static final Class PREF_CLASS = Mp34ControlPanel.class;
    public static final String PREF_FILE_NAME = PREF_CLASS.getPackage().getName() + "." + PREF_CLASS.getName() + "_prefs.xml";
    
    public void saveSeek(int seek) {
    	if (seek >= 0) {
    		mp34Player.getAppSettings().setSeek(seek);
    	}
    	//mp34Player.getMediaPlayer().getCurrentPosition();
    	//getMediaPlayer().setTime(i)
    }
    public void saveSeek() {
    	saveSeek(mp34Player.getMediaPlayer().getCurrentPosition());
    }
    
    public void release() {
    	saveSettings();
    	getMediaPlayerComponent().release();
    }
    
    static class WinEditorWrapper extends EditorWrapper<Preferences> {

		public WinEditorWrapper(Preferences w) {
			super(w);
		}

		@Override
		public EditorWrapper<Preferences> putString(String key, String value) {
			if (value == null) {
				wrapped.remove(key);
			} else {
				wrapped.put(key, value);
			}
			return this;
		}

		@Override
		public EditorWrapper<Preferences> putStringSet(String key, Set<String> values) {
			// TODO Auto-generated method stub
			throw new UnsupportedOperationException("Not implemented yet");
		}

		@Override
		public EditorWrapper<Preferences> putInt(String key, int value) {
			wrapped.putInt(key, value);
			return this;
		}

		@Override
		public EditorWrapper<Preferences> putLong(String key, long value) {
			wrapped.putLong(key, value);
			return this;
		}

		@Override
		public EditorWrapper<Preferences> putFloat(String key, float value) {
			wrapped.putFloat(key, value);
			return this;
		}

		@Override
		public EditorWrapper<Preferences> putBoolean(String key, boolean value) {
			wrapped.putBoolean(key, value);
			return null;
		}

		@Override
		public EditorWrapper<Preferences> remove(String key) {
			wrapped.remove(key);
			return this;
		}

		@Override
		public EditorWrapper<Preferences> clear() {
			try {
				wrapped.clear();
			} catch (BackingStoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return this;
		}

		@Override
		public boolean commit() {
			return true;
		}

		@Override
		public void apply() {
		}
    	
    }
    static class WinSharedPreferencesWrapper extends SharedPreferencesWrapper<Preferences> {

		public WinSharedPreferencesWrapper(Preferences w) {
			super(w);
		}

		@Override
		public Map<String, ?> getAll() {
			// TODO Auto-generated method stub
			throw new UnsupportedOperationException("Not implemented yet");
		}

		@Override
		public String getString(String key, String defValue) {
			return wrapped.get(key, defValue);
		}

		@Override
		public Set<String> getStringSet(String key, Set<String> defValues) {
			// TODO Auto-generated method stub
			throw new UnsupportedOperationException("Not implemented yet");
		}

		@Override
		public int getInt(String key, int defValue) {
			return wrapped.getInt(key, defValue);
		}

		@Override
		public long getLong(String key, long defValue) {
			return wrapped.getLong(key, defValue);
		}

		@Override
		public float getFloat(String key, float defValue) {
			return wrapped.getFloat(key, defValue);
		}

		@Override
		public boolean getBoolean(String key, boolean defValue) {
			return wrapped.getBoolean(key, defValue);
		}

		@Override
		public boolean contains(String key) {
			try {
				return wrapped.nodeExists(key);
			} catch (BackingStoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return false;
		}
    	
    }
	public ImagePreview getImagePreview() {
		return imagePreview;
	}

    
}
