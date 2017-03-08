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
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import ru.org.sevn.audiobookplayer.Mp34Player;
import uk.co.caprica.vlcj.discovery.NativeDiscovery;
import uk.co.caprica.vlcj.player.embedded.DefaultAdaptiveRuntimeFullScreenStrategy;

public class Mp34PlayerFrame extends JFrame {
	public static void runMain() {
		new NativeDiscovery().discover();
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				Mp34PlayerFrame frame = new Mp34PlayerFrame("Title", new Mp34ControlPanel(new Mp34Player()));
				frame.showFrame();
			}
		});
		
	}
	
	private ScheduledExecutorService executor;	
    final long delayMillis = 5000;
    private Runnable playingRun = new Runnable() {
        @Override
        public void run() {
        	if (controlPanel != null) {
        		controlPanel.commitSettings();
        	}
        }		
	};	
	
	private Mp34ControlPanel controlPanel;
	private JPanel mainPanel = new JPanel(new BorderLayout());
	private KeyListener keyListener = new MyKeyListener();
	
	public ListenEmbeddedMediaPlayerComponent getMediaPlayerComponent() {
		return controlPanel.getMediaPlayerComponent();
	}
	
	public Mp34PlayerFrame(String title, Mp34ControlPanel controlPanel) {
		super(title);
		this.controlPanel = controlPanel;
		setBounds(100, 100, 600, 400);
		setContentPane(mainPanel);
		mainPanel.add(getMediaPlayerComponent(), BorderLayout.CENTER);		
        mainPanel.add(this.controlPanel, BorderLayout.SOUTH);
        mainPanel.add(this.controlPanel.getImagePreview(), BorderLayout.EAST);
        getMediaPlayerComponent().setKeyListener(keyListener);
        addKeyListener(keyListener);
        
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (executor != null) {
                	executor.shutdown();
                	executor = null;
                }
            	
            	controlPanel.release();
                System.exit(0);
            }
        });
        
        getMediaPlayerComponent().getMediaPlayer().setFullScreenStrategy(
        	    new DefaultAdaptiveRuntimeFullScreenStrategy(Mp34PlayerFrame.this){
        	        @Override
        	        protected void beforeEnterFullScreen() {
        	        	toggleControls(false);
        	        }
        	        
        	        private void toggleControls(boolean vis) {
        	            controlPanel.setVisible(vis);
        	            controlPanel.getImagePreview().setVisible(vis);
//        	            statusBar.setVisible(vis);
        	        	
        	        }

        	        @Override
        	        protected void afterExitFullScreen() {
        	        	toggleControls(true);
        	        }        	    	
        	    }
        	);        
        
    	executor = Executors.newSingleThreadScheduledExecutor();
    	executor.scheduleAtFixedRate(playingRun, this.delayMillis, this.delayMillis, TimeUnit.MILLISECONDS);
    	
    	ImageIcon ii = Utils.createImageIcon("/drawable/ic_launcher.png");
    	if (ii != null) {
    		setIconImage(ii.getImage());
    	}
    	setTitle("Media Book Player");
	}
	
	public void showFrame() {
        setVisible(true);
        getMediaPlayerComponent().getVideoSurface().requestFocus();
	}
	
	private class MyKeyListener extends  ListenEmbeddedMediaPlayerComponent.KeyListenerAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
        	if(e.getKeyCode()==KeyEvent.VK_SPACE){
            	if (getMediaPlayerComponent().getMediaPlayer().isPlaying()) {
            		//getMediaPlayerComponent().getMediaPlayer().pause();
            		controlPanel.getMp34Player().pausePlaying();
            	} else {
            		//getMediaPlayerComponent().getMediaPlayer().play();
            		controlPanel.getMp34Player().resumePlaying();
            	}
        	} else if (e.getKeyCode()==KeyEvent.VK_N) {
        		controlPanel.getMp34Player().playNext();
        	} else if (e.getKeyCode()==KeyEvent.VK_P) {
        		controlPanel.getMp34Player().playPrev();
        	} else if (e.getKeyCode()==KeyEvent.VK_LEFT) {
        		//getMediaPlayerComponent().getMediaPlayer().skip(-10000);
        		controlPanel.seek(false);
        	} else if (e.getKeyCode()==KeyEvent.VK_RIGHT) {
        		//getMediaPlayerComponent().getMediaPlayer().skip(10000);
        		controlPanel.seek(true);
//        	} else if (e.getKeyCode()==KeyEvent.VK_HOME) {
//        		getMediaPlayerComponent().getMediaPlayer().setPosition(0);
//        	} else if (e.getKeyCode()==KeyEvent.VK_END) {
//        		getMediaPlayerComponent().getMediaPlayer().setPosition(1);
        	} else if (e.getKeyCode()==KeyEvent.VK_ESCAPE) {
        		if (getMediaPlayerComponent().getMediaPlayer().isFullScreen()) {
            		getMediaPlayerComponent().getMediaPlayer().toggleFullScreen();
        		}
        	} else if (e.getKeyCode()==KeyEvent.VK_F) {
        		getMediaPlayerComponent().getMediaPlayer().toggleFullScreen();
        	} else if (e.getKeyCode()==KeyEvent.VK_UP) {
        		controlPanel.incrVolume(10);
        	} else if (e.getKeyCode()==KeyEvent.VK_DOWN) {
        		controlPanel.incrVolume(-10);
        	}
        }        	
		
	}
}
