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
package org.apache.tika.parser.mp3;

import org.apache.tika.parser.mp3.ID3v2Frame.RawTag;

import ru.org.sevn.mp3.SevnRawTag;

public class SevnRawTagWrapper extends SevnRawTag<RawTag> {
	public SevnRawTagWrapper(RawTag t) {
		super(t);
	}
	
	public String getName() {
		return tag.name;
	}
	public int getFlag() {
		return tag.flag;
	}
	public byte[] getData() {
		return tag.data;
	}
	public int getSize() {
		return tag.getSize();
	}
	
	public void setName(String name) {
		tag.name = name;
	}
	
	public void setFlag(int flag) {
		tag.flag = flag;
	}

	public void setData(byte[] data) {
		tag.data = data;
	}
	
	public String getDataString() {
		return SevnUtil.getTagString(getData(), 0, getData().length);
	}
}
