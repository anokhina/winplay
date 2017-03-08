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
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import ru.org.sevn.audiobookplayer.DirInfo;
import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.discovery.NativeDiscovery;
import uk.co.caprica.vlcj.player.MediaMeta;
import uk.co.caprica.vlcj.player.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.embedded.DefaultAdaptiveRuntimeFullScreenStrategy;

public class VideoExample {

	private final EmbeddedMediaPlayerComponent mediaPlayerComponent;
	//C:\Portable\Portable\PortableApps\VLCPortable\App\vlc\ 
	
	public static String LIB_PATH = "C:\\Program Files\\VideoLAN\\VLC";
	//C:\Program Files\VideoLAN\VLC
	public static String LIB_PATH_LOC = "C:/Portable/progs/vlc64-2.2.4";
	
	private static String newline = "\n";

	public static void runMain() {
//		NativeLibrary.addSearchPath(RuntimeUtil.getLibVlcLibraryName(), LIB_PATH);
//		Native.loadLibrary(RuntimeUtil.getLibVlcLibraryName(), LibVlc.class);
		new NativeDiscovery().discover();
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new VideoExample();
			}
		});
	}
	
	public static void artwork() {
		new NativeDiscovery().discover();

        String fileName = "C:\\pub\\stix1.mp3";

        // Create a media player
        MediaPlayerFactory factory = new MediaPlayerFactory();

        // Get the meta data and dump it out
        MediaMeta mediaMeta = factory.getMediaMeta(fileName, true);

        // Load the artwork into a buffered image (if available)
        final BufferedImage artwork = mediaMeta.getArtwork();
        System.out.println(artwork);

        // Orderly clean-up
        mediaMeta.release();
        factory.release();

        if(artwork != null) {
            JPanel cp = new JPanel() {
                private static final long serialVersionUID = 1L;

                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D)g;
                    g2.setPaint(Color.black);
                    g2.fillRect(0, 0, getWidth(), getHeight());
                    double sx = (double)getWidth() / (double)artwork.getWidth();
                    double sy = (double)getHeight() / (double)artwork.getHeight();
                    sx = Math.min(sx, sy);
                    sy = Math.min(sx, sy);
                    AffineTransform tx = AffineTransform.getScaleInstance(sx, sy);
                    g2.drawImage(artwork, new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR), 0, 0);
                }

                @Override
                public Dimension getPreferredSize() {
                    return new Dimension(artwork.getWidth(), artwork.getHeight());
                }
            };
            JFrame f = new JFrame("vlcj meta artwork");
            f.setIconImage(Utils.createImageIcon("/drawable/ic_launcher.png").getImage());
            f.setContentPane(cp);
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            f.pack();
            f.setVisible(true);
        }		
	}

	private VideoExample() {
		JFrame frame = new JFrame("vlcj Tutorial");
        String fileName = "C:\\pub\\stix.mp4";
		
        frame.setBounds(100, 100, 600, 400);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                mediaPlayerComponent.release();
                System.exit(0);
            }
        });
        
        mediaPlayerComponent = new EmbeddedMediaPlayerComponent() {
            @Override
            public void mouseClicked(MouseEvent e) {
            }

            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
            	if(e.getKeyCode()==KeyEvent.VK_SPACE){
                	if (mediaPlayerComponent.getMediaPlayer().isPlaying()) {
                		mediaPlayerComponent.getMediaPlayer().pause();
                	} else {
                		mediaPlayerComponent.getMediaPlayer().play();
                	}
            	} else if (e.getKeyCode()==KeyEvent.VK_LEFT) {
            		
            		mediaPlayerComponent.getMediaPlayer().skip(-10000);
            	} else if (e.getKeyCode()==KeyEvent.VK_RIGHT) {
            		mediaPlayerComponent.getMediaPlayer().skip(10000);
            	} else if (e.getKeyCode()==KeyEvent.VK_HOME) {
            		mediaPlayerComponent.getMediaPlayer().setPosition(0);
            	} else if (e.getKeyCode()==KeyEvent.VK_END) {
            		mediaPlayerComponent.getMediaPlayer().setPosition(1);
            	} else if (e.getKeyCode()==KeyEvent.VK_F) {
            		mediaPlayerComponent.getMediaPlayer().toggleFullScreen();
            	}
            }        	
        };
        JPanel mainPanel = new JPanel(new BorderLayout());
        frame.setContentPane(mainPanel);
        mainPanel.add(mediaPlayerComponent, BorderLayout.CENTER);
        final JPanel controlPanel = new JPanel(new FlowLayout());
        JButton fileOpenButton = (JButton)controlPanel.add(new JButton("File open"));
        final JTextArea log = new JTextArea();
        mainPanel.add(log, BorderLayout.EAST);
        fileOpenButton.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = null;
		        //Set up the file chooser.
		        if (fc == null) {
		            fc = new JFileChooser();
		            fc.setCurrentDirectory(new File("."));
		            fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		            
			        //Add a custom file filter and disable the default
			        //(Accept All) file filter.
		            fc.addChoosableFileFilter(new PatternFileChooserFilter());
		            fc.setAcceptAllFileFilterUsed(false);
		 
		            //Add custom icons for file types.
		            fc.setFileView(new ImageFileView());
		 
		            //Add the preview pane.
		            fc.setAccessory(new BookPreview(fc));
		        }
		 
		        //Show it.
		        int returnVal = fc.showDialog(controlPanel,
		                                      "Open");
		 
		        //Process the results.
		        if (returnVal == JFileChooser.APPROVE_OPTION) {
		            File file = fc.getSelectedFile();
		            DirInfo<ImageIcon> di = new DirInfo<>(file, WinDirInfoBitmapLoader.getDefaultLoader());
		            if (di.getBookDir() != null) {
		            	
		            }
		            log.append("Attaching file: " + file.getName()
		                       + "." + newline);
		        } else {
		            log.append("Attachment cancelled by user." + newline);
		        }
		        log.setCaretPosition(log.getDocument().getLength());
		 
		        //Reset the file chooser for the next time it's shown.
		        fc.setSelectedFile(null);				
			}
		});
        mainPanel.add(controlPanel, BorderLayout.SOUTH);
        
        mediaPlayerComponent.getMediaPlayer().setFullScreenStrategy(
        	    new DefaultAdaptiveRuntimeFullScreenStrategy(frame){
        	        @Override
        	        protected void beforeEnterFullScreen() {
        	            controlPanel.setVisible(false);
//        	            statusBar.setVisible(false);
        	        }

        	        @Override
        	        protected void afterExitFullScreen() {
        	            controlPanel.setVisible(true);
//        	            statusBar.setVisible(true);
        	        }        	    	
        	    }
        	);        
        frame.setVisible(true);
        mediaPlayerComponent.getVideoSurface().requestFocus();
        //mediaPlayerComponent.getVideoSurface().requestFocusInWindow();
        
        mediaPlayerComponent.getMediaPlayer().playMedia(fileName);		
	}
	
}