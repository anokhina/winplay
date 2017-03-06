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

import org.json.JSONException;
import org.json.JSONObject;

public class BookInfoSerializer {
	public static BookInfo makeBookInfoFromString(String jobj, BitmapLoader bloader) throws JSONException {
		return makeBookInfo(new JSONObject(jobj), bloader);
	}
	public static BookInfo makeBookInfo(JSONObject jobj, BitmapLoader bloader) throws JSONException {
		String bookDir = null;
		if (jobj.has("bookDir")) {
			bookDir = jobj.getString("bookDir");
		}
		return BookInfo.makeBookInfo(jobj.getString("path"), jobj.getInt("seek"), bookDir, bloader);
	}
	public static JSONObject getJSON(BookInfo bi) throws JSONException {
		JSONObject jobj = new JSONObject();
		jobj.put("seek", bi.getSeek());
		jobj.put("path", bi.getPath());
		if (bi.getDirInfo() != null) {
			if (bi.getDirInfo().getBitmapContainer() != null && bi.getDirInfo().getBitmapContainer().getFile() != null) {
				jobj.put("icon", bi.getDirInfo().getBitmapContainer().getFile().getAbsolutePath());
			}
		}
		if (bi.getDirInfo() != null && bi.getDirInfo().getBookDir() != null) { 
			jobj.put("bookDir", bi.getDirInfo().getBookDir().getAbsolutePath());
		}
		return jobj;
	}
}
