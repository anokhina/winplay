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

import java.util.Iterator;

import org.apache.tika.parser.mp3.ID3v2Frame.RawTag;
import org.apache.tika.parser.mp3.ID3v2Frame.RawTagIterator;

public class SevnRawTagIteratorWrapper implements Iterator<RawTag> {
	
	private static class MyRawTagIterator extends RawTagIterator {
		public MyRawTagIterator(ID3v2Frame frame,
				int nameLength, int sizeLength, int sizeMultiplier,
                int flagLength) {
	        frame.super(nameLength, sizeLength, sizeMultiplier, flagLength);
	    }
	}

	private Iterator<RawTag> iterator;
	private final int version;
	
	public SevnRawTagIteratorWrapper(final int v, final ID3v2Frame frame) {
		this.version = v;
		switch (v) {
		case 2:
			iterator = new MyRawTagIterator(frame, 3, 3, 1, 0);
		case 3:
			iterator = new MyRawTagIterator(frame, 4, 4, 1, 2);
		case 4:
			iterator = new MyRawTagIterator(frame, 4, 4, 1, 2);
		}
	}
	
	@Override
	public boolean hasNext() {
		return iterator.hasNext();
	}

	@Override
	public RawTag next() {
		return iterator.next();
	}

	public int getVersion() {
		return version;
	}
	
	
}
