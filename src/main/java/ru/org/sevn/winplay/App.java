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

//http://download.videolan.org/pub/videolan/vlc/last/win64/

public class App {
    public static void main( String[] args ) throws Exception {
//    	VideoExample.artwork();
        Mp34PlayerFrame.runMain();
//        NativeLibrary.addSearchPath(RuntimeUtil.getLibVlcLibraryName(), LIB_PATH_LOC);
//        System.out.println(LibVlc.INSTANCE.libvlc_get_version());
    }
}
