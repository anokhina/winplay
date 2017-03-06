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
package ru.org.sevn.audiobookplayer;

import java.io.File;

public class BitmapContainer<T> {
	private T bitmap;
	private File file;
	public BitmapContainer(T obj, File file) {
		this.bitmap = obj;
	}
	public T getBitmap() {
		return bitmap;
	}
	public void setBitmap(T bitmap) {
		this.bitmap = bitmap;
	}
	public File getFile() {
		return file;
	}
	
}
