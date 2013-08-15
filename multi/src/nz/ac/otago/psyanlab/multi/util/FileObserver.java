/*
 Copyright (C) 2012, 2013 University of Otago, Tonic Artos <tonic.artos@gmail.com>

 Otago PsyAn Lab is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program. If not, see <http://www.gnu.org/licenses/>.

 In accordance with Section 7(b) of the GNU General Public License version 3,
 all legal notices and author attributions must be preserved.
*/

package nz.ac.otago.psyanlab.multi.util;

import java.io.File;

public class FileObserver extends android.os.FileObserver {
    public String absolutePath;
    private OnEventListener dummyListener = new OnEventListener() {
        @Override
        public void onStoppedListening() {
        }

        @Override
        public void onFileRemoved(File f) {
        }

        @Override
        public void onFileModified(File f) {
        }

        @Override
        public void onFileAdded(File f) {
        }
    };
    private OnEventListener listener = dummyListener;

    public FileObserver(String path) {
        super(path, CREATE | DELETE | DELETE_SELF | MODIFY | MOVE_SELF | MOVED_FROM | MOVED_FROM | MOVED_TO);
        absolutePath = path;
    }

    @Override
    public void onEvent(int event, String path) {
        if (path == null) {
            return;
        }

        // The monitored path was deleted.
        if ((DELETE_SELF & event) != 0) {
            listener.onStoppedListening();
            stopWatching();
        }

        // The monitored path was moved.
        if ((MOVE_SELF & event) != 0) {
            listener.onStoppedListening();
            stopWatching();
        }

        File f = new File(path);
        if (f.isFile()) {

            // Data was written to a file.
            if ((MODIFY & event) != 0) {
                listener.onFileModified(f);
            }

            // A new file was created.
            if ((CREATE & event) != 0) {
                listener.onFileAdded(f);
            }

            // A file was deleted.
            if ((DELETE & event) != 0) {
                listener.onFileRemoved(f);
            }

            // A file was moved to the monitored location.
            if ((MOVED_TO & event) != 0) {
                listener.onFileAdded(f);
            }

            // A file was moved away from the monitored location.
            if ((MOVED_FROM & event) != 0) {
                listener.onFileRemoved(f);
            }
        }
    }

    public void setOnEventListener(OnEventListener newListener) {
        listener = newListener;
    }

    public interface OnEventListener {
        public void onFileAdded(File f);

        public void onFileModified(File f);;

        public void onFileRemoved(File f);

        public void onStoppedListening();
    }
}