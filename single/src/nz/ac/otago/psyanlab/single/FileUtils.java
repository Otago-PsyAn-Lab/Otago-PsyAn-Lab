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

package nz.ac.otago.psyanlab.single;

import android.content.Context;
import android.text.format.Time;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class FileUtils extends nz.ac.otago.psyanlab.common.util.FileUtils {
    public static final String PATH_TEMP = "tmp";

    public static final String PATH_EXTERNAL_EXPERIMENTS_DIR = "PsyAn Lab" + File.separator
            + "Experiment Files";

    public static final String PATH_INTERNAL_EXPERIMENTS_DIR = "pales";

    /**
     * Copies a file to internal storage giving it a unique name in the process.
     * 
     * @param context Application context.
     * @param filePath Path of file to copy.
     * @return New file in private internal storage.
     * @throws IOException
     */
    public static File copyToInternalStorage(Context context, String source, String dest)
            throws IOException {
        Time now = new Time();
        now.setToNow();

        File newPaleFile = new File(context.getDir(PATH_INTERNAL_EXPERIMENTS_DIR,
                Context.MODE_PRIVATE), dest);
        newPaleFile.createNewFile();

        FileInputStream in = new FileInputStream(source);
        FileOutputStream out = new FileOutputStream(newPaleFile);

        copy(in, out);

        in.close();
        out.close();

        return newPaleFile;
    }

    public static String generateNewFileName(String file) throws IOException,
            NoSuchAlgorithmException {
        MessageDigest digester = MessageDigest.getInstance("MD5");

        byte[] bytes = new byte[8192];
        FileInputStream in = new FileInputStream(file);

        int byteCount;
        while ((byteCount = in.read(bytes)) > 0) {
            digester.update(bytes, 0, byteCount);
        }
        in.close();

        byte[] digest = digester.digest();
        return URLEncoder.encode(new String(digest), "UTF-8") + ".pale";
    }
}
