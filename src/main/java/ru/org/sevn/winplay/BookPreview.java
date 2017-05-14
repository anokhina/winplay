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
import java.awt.Component;
import java.awt.Dimension;
import java.io.File;

import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import ru.org.sevn.audiobookplayer.BookInfo;
import ru.org.sevn.audiobookplayer.DirInfo;
import ru.org.sevn.utilwt.ImageUtil;

public class BookPreview extends FileChooserImagePreview {

	private DirInfo<ImageIcon> dirInfo;
	
	public BookPreview(final JFileChooser fc) {
		this(fc, null);
	}
	static class BookInfoListCellRenderer extends DefaultListCellRenderer {
	    private JLabel label = new JLabel("");
	    private Color textSelectionColor = Color.BLACK;
	    private Color backgroundSelectionColor = Color.CYAN;
	    private Color textNonSelectionColor = Color.BLACK;
	    private Color backgroundNonSelectionColor = Color.WHITE;
	    
	    public BookInfoListCellRenderer() {
	    	label.setOpaque(true);
	    }
	    
	    @Override
	    public Component getListCellRendererComponent(
	            JList list,
	            Object value,
	            int index,
	            boolean selected,
	            boolean expanded) {

	    	BookInfo bi = (BookInfo)value;
	    	label.setIcon(null);
	    	label.setToolTipText(null);
	    	label.setText(null);
	    	
	    	try {
	    		int wh = 64;
	    		label.setMinimumSize(new Dimension(wh, wh));
		    	label.setText(bi.getDirInfo().getBookDir().getParentFile().getName());
		    	label.setToolTipText(bi.getDirInfo().getBookDir().getAbsolutePath());
	    		label.setIcon(ImageUtil.getStretchedImageIcon(getAnyIcon((ImageIcon)bi.getDirInfo().getBitmap()), wh, wh, true));
	    	} catch (Exception e) {
	    		e.printStackTrace(System.err);
	    	}
	    	
	        if (selected) {
	            label.setBackground(backgroundSelectionColor);
	            label.setForeground(textSelectionColor);
	        } else {
	            label.setBackground(backgroundNonSelectionColor);
	            label.setForeground(textNonSelectionColor);
	        }

	        return label;
	    }
	    private ImageIcon getAnyIcon(ImageIcon ii) {
	    	if (ii == null) {
	    		return WinDirInfoBitmapLoader.DEFAULT_ICON.getBitmap();
	    	}
	    	return ii;
	    }
	}
	
	private File lastOpened; 
	private JList lastOpenedList; 
	public BookPreview(final JFileChooser fc, BookInfoListModel dataModel) {
		super(fc);
		if (dataModel != null) {
			JScrollPane scrollPane = new JScrollPane();
			final JList lst = new JList(dataModel);
			scrollPane.setViewportView(lst);			
			lastOpenedList = lst;
			lst.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			lst.setLayoutOrientation(JList.VERTICAL);		
			lst.addListSelectionListener(e -> {
				if (e.getValueIsAdjusting() == false) {
					BookInfo bi = (BookInfo)lst.getSelectedValue(); 
					if (bi != null) {
						try {
							lastOpened = bi.getDirInfo().getBookDir().getParentFile();
							fc.setSelectedFile(lastOpened);
						} catch (Exception ex) {
							ex.printStackTrace(System.err);
						}
					}
				}
			});
			lst.setCellRenderer(new BookInfoListCellRenderer());
			add(scrollPane, BorderLayout.EAST);
		}
	}
	private void removeSelection() {
		if (lastOpenedList != null) {
			int i = lastOpenedList.getSelectedIndex();
			lastOpenedList.removeSelectionInterval(i, i);
			lastOpened = null;
		}
	}
	@Override
	public void setFile(File file) {
		super.setFile(file);
		System.out.println("-------------->"+file+":"+lastOpened);
		if (file == null) {
			dirInfo = null;
			removeSelection();
		} else if (file.isDirectory()) {
			if (lastOpened == null || !file.getAbsolutePath().equals(lastOpened.getAbsolutePath())) {
				removeSelection();
			}
			dirInfo = new DirInfo<ImageIcon>(file, WinDirInfoBitmapLoader.getDefaultLoader());
		}
		if (lastOpenedList != null) {
			BookInfoListModel lmodel = (BookInfoListModel)lastOpenedList.getModel();
			int idx = lmodel.indexOf(new File(file, "Book").getAbsolutePath());
			System.out.println("-------------->"+idx);
			if (idx >= 0 && lastOpened == null) {
				lastOpenedList.setSelectedIndex(idx);
			}
		}
	}
	@Override
	public String getPreviewText() {
		return super.getPreviewText();
	}
	@Override
	public String getImageFilePath() {
		return null;
	}
	@Override
	protected ImageIcon getFileIcon() {
		if (dirInfo != null) {
			return dirInfo.getBitmap();
		}
		return null;
	}
	public JList getLastOpenedList() {
		return lastOpenedList;
	}
	
}
