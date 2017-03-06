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

public class DirInfo<T> {
    private File file;
    private String label;
    private BitmapContainer<T> bitmapContainer;
    private File bookDir;
    private boolean remote;
    
    public static final String FILE_NAME_COVER = "Info/Image/Cover";
    public static final String FILE_NAME_BOOK = "Book";
    public static final String FILE_NAME_ICON = ".icon";
    public static final String[] FILE_NAME_ICON_EXT = {"", ".png", ".jpg", ".gif"};
    
    
    private BookInfo bookInfo;
    
    public DirInfo(File f, BitmapLoader<T> bloader) {
        this(f, f.getName(), bloader);
    }
    public DirInfo(File f, String l, BitmapLoader<T> bloader) {
    	bitmapContainer = bloader.getDefaultBitmap();
        this.file = f;
        label = l;
        if (file != null && file.isDirectory()) {
            File bDir = new File(file, FILE_NAME_BOOK);
            init(bDir, bloader);
        } 
    }
    private void init (File bDir, BitmapLoader<T> bloader) {
    	if (file != null && file.exists() && file.isDirectory()) {
        	bitmapContainer = bloader.decodeFile(file);
        	for(String ext : FILE_NAME_ICON_EXT) {
	            File imgfile = new File(file, FILE_NAME_ICON+ext);
	            if (imgfile.exists() && imgfile.canRead()) {
	            	bitmapContainer = bloader.decodeFile(imgfile);
	                break;
	            }
        	}            
            File cover = findCover();
            if (cover != null) {
            	bitmapContainer = bloader.decodeFile(cover); //??????
                if (bDir.exists() && bDir.isDirectory() && bDir.canRead()) {
                    bookDir = bDir;
                }
            }
    		
    	}
    }
    protected DirInfo(String bookDirPath, BitmapLoader<T> bloader) {
    	refresh(bookDirPath, bloader);
    }
    private void refresh(String bookDirPath, BitmapLoader<T> bloader) {
    	remote = false;
    	File bDir = new File(bookDirPath);
    	file = bDir.getParentFile();
    	if (file != null) {
    		label = bDir.getParentFile().getName();
    	}
    	if (!bDir.exists()) {
    		bookDir = bDir;
    		remote = true;
    	} else {
    		init(bDir, bloader);
    	}
    	
    }
    private File findCover() {
    	for (String ext : FILE_NAME_ICON_EXT) {
	        File cover = new File(file, FILE_NAME_COVER + ext);
	        if (cover.exists() && cover.canRead()) {
	        	return cover;
	        }
    	}
        return null;
    }
    public String toString() {
        return label;
    }
    public File getFile() {
        return file;
    }
    public String getLabel() {
        return label;
    }
    public T getBitmap() {
    	if (bitmapContainer == null) {
    		return null;
    	}
        return bitmapContainer.getBitmap();
    }
    public File getBookDir() {
        return bookDir;
    }
//	public DirInfo setBitmap(T bitmap) {
//		bitmapContainer = new BitmapContainer(bitmap);
//		return this;
//	}
	BookInfo getBookInfo() {
		return bookInfo;
	}
	void setBookInfo(BookInfo bookInfo) {
		this.bookInfo = bookInfo;
	}
	public BitmapContainer<T> getBitmapContainer() {
		return bitmapContainer;
	}
	public void setBitmapContainer(BitmapContainer<T> bitmapContainer) {
		this.bitmapContainer = bitmapContainer;
	}
	public boolean isRemote() {
		return remote;
	}
    public void refresh(BitmapLoader<T> bloader) {
    	refresh(bookDir.getAbsolutePath(), bloader);
    }
}