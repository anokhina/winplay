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

import java.io.File;

import javax.swing.ImageIcon;

import ru.org.sevn.audiobookplayer.BitmapContainer;
import ru.org.sevn.audiobookplayer.BitmapLoader;

public class WinDirInfoBitmapLoader extends BitmapLoader<ImageIcon>{

	public static final BitmapContainer<ImageIcon> DEFAULT_ICON = 
			new BitmapContainer<ImageIcon>(Utils.createImageIcon("/drawable/picture.png"), null);
	@Override
	public BitmapContainer<ImageIcon> decodeFile(File file) {
        return new BitmapContainer(new ImageIcon(file.getPath()), file);
	}

	//TODO sync-ion
	private final static BitmapLoader<ImageIcon> defaultLoader = new WinDirInfoBitmapLoader();
	public static BitmapLoader<ImageIcon> getDefaultLoader() {
		return defaultLoader;
	}
	@Override
	public BitmapContainer<ImageIcon> decodeByteArray(byte[] image, int offset, int length) {
		return new BitmapContainer<ImageIcon>(new ImageIcon(image), null);
	}
	@Override
	public BitmapContainer<ImageIcon> getScaledImage(BitmapContainer<ImageIcon> image, int w, int h, float multiplier) {
		return new BitmapContainer(Utils.getScaledIcon(image.getBitmap(), w, h, false), image.getFile());
	}
	@Override
	public BitmapContainer<ImageIcon> getDefaultBitmap() {
		return DEFAULT_ICON;
	}
}
