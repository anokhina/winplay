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
import java.util.ArrayList;

public class BookInfo {
	private DirInfo dirInfo;
	private int seek;
	private String fileName;
	public DirInfo getDirInfo() {
		return dirInfo;
	}
	public void setDirInfo(DirInfo dirInfo) {
		this.dirInfo = dirInfo;
		if (this.dirInfo != null) {
			this.dirInfo.setBookInfo(this);
		}
	}
	public int getSeek() {
		return seek;
	}
	public void setSeek(int seek) {
		this.seek = seek;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	public static File findBookDirFile(String path, BitmapLoader bloader) {
		DirInfo di = findBookDir(path, bloader);
		if (di != null && di.getBookDir() != null) {
			return di.getBookDir();
		}
		return null;
	}
	public static DirInfo findBookDir(String path, BitmapLoader bloader) {
		return findBookDir(path, null, bloader);
	}
	
	public static DirInfo findBookDir(String path, String bookDir, BitmapLoader bloader) {
		if (path != null) {
			File fl = new File(path);
			File dir = fl.getParentFile();
			if (dir != null) {
				File parent = dir.getParentFile();
				while (parent != null) {
					DirInfo dirInfo = new DirInfo(parent, bloader);
					if (dirInfo.getBookDir() != null) {
						return dirInfo;
					}
					parent = parent.getParentFile();
				}
			}
		}
		if (bookDir != null) {
			DirInfo dirInfo = new DirInfo(bookDir, bloader);
			if (dirInfo.getBookDir() != null) {
				return dirInfo;
			}
		}
		return null;
	}
	
	public static BookInfo makeBookInfo(String path, int seek, String bookDirPath, BitmapLoader bloader) {
		BookInfo ret = null;
		if (path != null) {
			DirInfo dirInfo = findBookDir(path, bookDirPath, bloader) ;
			File fl = new File(path);
			if (dirInfo != null) {
				ret = new BookInfo();
				ret.setFileName(getRelativeFileName(dirInfo.getBookDir() ,fl));
				ret.setDirInfo(dirInfo);
				ret.setSeek(seek);
			}
		}
		return ret;
	}
	
	public static String getRelativeFileName(String dirPath, File fl) {
		return getRelativeFileName(new File(dirPath), fl);
	}
	
	public static String getRelativeFileName(File dir, File fl) {
		if (dir != null) {
			String dirAPath = dir.getAbsolutePath();
			if (fl.getAbsolutePath().startsWith(dirAPath)) {
				return fl.getAbsolutePath().substring(dirAPath.length()+1);
			}
		}
		return fl.getAbsolutePath();
	}
	
	public static File[] getFileList(File parentDir, FilenameFilter filenameFilter) {
		ArrayList<File> ret = new ArrayList<>();
		getFileList(ret, parentDir, filenameFilter);
		return ret.toArray(new File[ret.size()]);
	}
	public static void getFileList(ArrayList<File> ret, File parentDir, FilenameFilter filenameFilter) {
		File[] lFile = parentDir.listFiles(filenameFilter);
		if (lFile != null) {
			for (File fl : lFile) {
				if (fl.isDirectory()) {
					getFileList(ret, fl, filenameFilter);
				} else {
					ret.add(fl);
				}
			}
		}
	}
	
	public String getPath() {
		if (dirInfo != null && fileName != null && dirInfo.getBookDir() != null) {
			File ret = new File(dirInfo.getBookDir(), fileName);
			return ret.getAbsolutePath();
		}
		return null;
	}
	
//	public String toString() {
//		return getFileName();
//		//return super.toString();
//	}
}
