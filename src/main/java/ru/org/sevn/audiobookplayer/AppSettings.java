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

import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;

public class AppSettings {
	public static final String const_pref_encoding = "const_pref_encoding";
	public static final String const_pref_media_dir = "const_pref_media_dir";
	public static final String const_pref_loop = "const_pref_loop";
	public static final String const_pref_seek = "const_pref_seek";
	public static final String const_pref_title = "const_pref_title";
	public static final String const_pref_playing_dir = "const_pref_playing_dir";
	public static final String const_pref_playing_name = "const_pref_playing_name";
	public static final String const_pref_last_opened = "const_pref_last_opened";
	public static final String const_pref_volume = "const_pref_volume";
	
    public static void restoreSettings(MediaPlayerSettings settings, SharedPreferencesWrapper prefs, BitmapLoader loader) {
    	settings.setCharsetName(prefs.getString(AppSettings.const_pref_encoding, "UTF-8"));
    	settings.setPlayingDirPath(prefs.getString(AppSettings.const_pref_playing_dir, null));
    	settings.setPlayingName(prefs.getString(AppSettings.const_pref_playing_name, null));
    	settings.setTitle(prefs.getString(AppSettings.const_pref_title, null));
    	settings.setLoop(prefs.getBoolean(AppSettings.const_pref_loop, false));
    	settings.setSeek(prefs.getInt(AppSettings.const_pref_seek, 0));
    	settings.setVolume(prefs.getInt(AppSettings.const_pref_volume, 0));
        try {
//        	System.err.println("RRRRRRRRRRRRRRRRRRRRRRRRR>"+prefs.getString(key(R.string.const_pref_last_opened), ""));
        	restoreLastOpened(settings, new JSONArray(prefs.getString(AppSettings.const_pref_last_opened, "[]")), loader);
        } catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
        }
    	
    }
    public static void restoreLastOpened(MediaPlayerSettings settings, JSONArray lastOpened, BitmapLoader loader) throws JSONException {
    	settings.getLastBooks().clear();
    	
    	for(int i = 0; i < lastOpened.length(); i++) {
    		BookInfo bi = BookInfoSerializer.makeBookInfo(lastOpened.getJSONObject(i), loader);
    		if (bi != null) {
    			settings.getLastBooks().put(bi.getDirInfo().getBookDir().getAbsolutePath(), bi);
    		}
    	}
    }
    private static final String[] props = new String[] {"charsetName", "playingDirPath", "playingName", "title", "loop", "seek", "volume"};
    
    public static void saveSettings(EditorWrapper ed, String prop, MediaPlayerSettings settings) {
        if (prop != null) {
            saveSetting(ed, prop, settings);
        } else {
            for(String p : props) {
                saveSetting(ed, p, settings);
            }
        }
    }
    public static void saveSetting(EditorWrapper ed, String prop, MediaPlayerSettings settings) {
        switch(prop) {
        case "charsetName":
            ed.putString(AppSettings.const_pref_encoding, settings.getCharsetName());
            break;
        case "playingDirPath":
            ed.putString(AppSettings.const_pref_playing_dir, settings.getPlayingDirPath());
            break;
        case "playingName":
            ed.putString(AppSettings.const_pref_playing_name, settings.getPlayingName());
            break;
        case "title":
            ed.putString(AppSettings.const_pref_title, settings.getTitle());
            break;
        case "loop":
            ed.putBoolean(AppSettings.const_pref_loop, settings.isLoop());
            break;
        case "seek":
            ed.putInt(AppSettings.const_pref_seek, settings.getSeek());
            break;
        case "volume":
            ed.putInt(AppSettings.const_pref_volume, settings.getVolume());
            break;
        }
        
    }
    public static void saveLastOpened(EditorWrapper ed, MediaPlayerSettings settings) {
    	JSONArray arr = new JSONArray();
    	for (BookInfo bi : settings.getLastBooks().values()) {
    		try {
    			arr.put(BookInfoSerializer.getJSON(bi));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    	ed.putString(AppSettings.const_pref_last_opened, arr.toString());
//    	System.err.println("SSSSSSSSSSSSSSSSSSSSSSSS>"+arr.toString());
    }
	public static abstract class EditorWrapper<EDITOR> {
		final protected EDITOR wrapped;
		public EditorWrapper(EDITOR w) {
			wrapped = w;
		}
		public EDITOR getWrapped() {
			return wrapped;
		}
		public abstract EditorWrapper<EDITOR> putString(String key, String value);
		public abstract EditorWrapper<EDITOR> putStringSet(String key, Set<String> values);
		public abstract EditorWrapper<EDITOR> putInt(String key, int value);
		public abstract EditorWrapper<EDITOR> putLong(String key, long value);
		public abstract EditorWrapper<EDITOR> putFloat(String key, float value);
		public abstract EditorWrapper<EDITOR> putBoolean(String key, boolean value);
		public abstract EditorWrapper<EDITOR> remove(String key);
		public abstract EditorWrapper<EDITOR> clear();
		public abstract boolean commit();
		public abstract void apply();
	}
	public static abstract class SharedPreferencesWrapper<T> {
		final protected T wrapped;
		public SharedPreferencesWrapper(T w) {
			wrapped = w;
		}
		
		public T getWrapped() {
			return wrapped;
		}

		public abstract Map<String, ?> getAll();
		public abstract String getString(String key, String defValue);
		public abstract Set<String> getStringSet(String key, Set<String> defValues);
		public abstract int getInt(String key, int defValue);
		public abstract long getLong(String key, long defValue);
		public abstract float getFloat(String key, float defValue);
		public abstract boolean getBoolean(String key, boolean defValue);
		public abstract boolean contains(String key);
	}
}
