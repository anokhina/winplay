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

import java.util.ArrayList;
import java.util.Collections;

import javax.swing.AbstractListModel;

import ru.org.sevn.audiobookplayer.BookInfo;
import ru.org.sevn.audiobookplayer.MediaPlayerSettings;

public class BookInfoListModel extends AbstractListModel<BookInfo> {
	
	private final MediaPlayerSettings settings;
	private ArrayList<BookInfo> lst;
	
	public BookInfoListModel(MediaPlayerSettings settings) {
		this.settings = settings;
		initList();
	}

	@Override
	public int getSize() {
		return lst.size();
	}

	@Override
	public BookInfo getElementAt(int index) {
		return lst.get(index);
	}
	
	private void initList() {
		lst = new ArrayList<>(settings.getLastBooks().values());
		Collections.reverse(lst);
	}
	
	public int indexOf(String k) {
		BookInfo bi = settings.getLastBooks().get(k);
		if (bi != null && lst != null) {
			int len = settings.getLastBooks().size();
			for(int i = 0; i < len; i++) {
				if (bi.equals(lst.get(i))) {
					return i;
				}
			}
		}
		return -1;
	}
}