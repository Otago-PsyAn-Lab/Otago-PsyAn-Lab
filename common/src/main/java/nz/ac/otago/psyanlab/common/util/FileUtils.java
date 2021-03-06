/*
 Copyright (c) 2012, 2013 University of Otago, Tonic Artos <tonic.artos@gmail.com>

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

package nz.ac.otago.psyanlab.common.util;

import com.google.gson.stream.JsonReader;

import nz.ac.otago.psyanlab.common.model.Asset;
import nz.ac.otago.psyanlab.common.model.Experiment;
import nz.ac.otago.psyanlab.common.model.Source;
import nz.ac.otago.psyanlab.common.model.util.ModelUtils;

import org.json.JSONException;

import android.text.TextUtils;
import android.text.format.Time;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.Enumeration;
import java.util.Map.Entry;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

//

public class FileUtils {
    /**
     * Compress a working directory to some destination.
     * 
     * @param workingDir Directory whose contents will be archived.
     * @param destFile Destination file which will be written to.
     * @return Archive file written.
     * @throws FileNotFoundException
     * @throws IOException
     */
    @Deprecated
    public static File compress(File workingDir, File destFile) throws FileNotFoundException,
            IOException {
        ZipOutputStream out = new ZipOutputStream(new FileOutputStream(destFile));
        addFiles(workingDir, workingDir, out);
        out.close();

        return destFile;
    }

    public static void compress(File destination, Experiment experiment, File workingDir)
            throws IOException {
        ZipOutputStream out = new ZipOutputStream(new FileOutputStream(destination));

        try {
            writeExperimentDefinitionToArchive(out,
                    copyAssetsToArchive(out, experiment, workingDir));
        } finally {
            out.close();
        }
    }

    /**
     * Inflate a pale file to a given working directory.
     * 
     * @param paleFile .pale file to inflate.
     * @param workingDir Path to extract to.
     * @return Path of the location the file was extracted to.
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static File decompress(File paleFile, File workingDir) throws FileNotFoundException,
            IOException {
        ZipFile archive = new ZipFile(paleFile);

        try {
            // Iterate over elements in zip file and extract them to working
            // directory.
            Enumeration<? extends ZipEntry> entries = archive.entries();
            while (entries.hasMoreElements()) {
                ZipEntry ze = entries.nextElement();
                File newFile = new File(workingDir, ze.getName());

                if (ze.isDirectory()) {
                    if (!newFile.exists()) {
                        newFile.mkdirs();
                    }
                    continue;
                }

                if (!newFile.getParentFile().exists()) {
                    // Dir tree missing for some reason so create it.
                    newFile.getParentFile().mkdirs();
                }

                InputStream in = archive.getInputStream(ze);
                OutputStream out = new BufferedOutputStream(new FileOutputStream(newFile));

                try {
                    copy(in, out);
                } finally {
                    in.close();
                    out.close();
                }
            }
        } finally {
            archive.close();
        }

        return workingDir;
    }

    /**
     * Extract a single named file from an archive.
     * 
     * @param needle Archive relative path of file to extract.
     * @param paleFile Experiment archive file.
     * @throws IOException
     * @throws ZipException
     */
    public static byte[] extractJust(String needle, File paleFile) throws ZipException, IOException {
        ZipFile archive = new ZipFile(paleFile);
        try {
            ZipEntry ze = archive.getEntry(needle);
            if (ze == null) {
                throw new RuntimeException("Could not find experiment json in "
                        + paleFile.getName());
            }
            InputStream in = archive.getInputStream(ze);
            ByteArrayOutputStream out = new ByteArrayOutputStream();

            try {
                copy(in, out);
                return out.toByteArray();
            } finally {
                in.close();
                out.close();
            }
        } finally {
            archive.close();
        }
    }

    /**
     * Format Bytes to a human readable quantity.
     * 
     * @param fileSize File size in bytes.
     * @return Human readable string of bytes. e.g. 1.2 TiB.
     */
    public static String formatBytes(long fileSize) {
        if (fileSize <= 0) {
            return "0 B";
        }
        final String[] units = new String[] {
                "B", "KiB", "MiB", "GiB", "TiB"
        };
        int digitGroups = (int)(Math.log10(fileSize) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(fileSize / Math.pow(1024, digitGroups)) + " "
                + units[digitGroups];
    }

    /**
     * Generates a relatively unique path to use as a temporary directory.
     * 
     * @return
     */
    public static String generateTempPath() {
        Time now = new Time();
        now.setToNow();
        return "tmp-" + now.toMillis(false) + "/";
    }

    /**
     * Load PALE definition from deflated file.
     * 
     * @param paleFile Deflated PALE file.
     * @return Experiment definition.
     * @throws IOException
     * @throws JSONException
     */
    public static Experiment loadExperimentDefinitionFromArchive(File paleFile) throws IOException,
            JSONException {
        String paleDefinition = new String(FileUtils.extractJust("experiment.json", paleFile));

        if (TextUtils.isEmpty(paleDefinition)) {
            throw new IllegalStateException("PALE definition was empty.");
        }

        try {
            return ModelUtils.readDefinition(paleDefinition);
        } catch (RuntimeException e) {
            throw new JSONException("Failed to parse PALE definition.");
        }
    }

    public static Experiment loadExperimentDefinition(File experimentJsonFile)
            throws FileNotFoundException {
        try {
            return ModelUtils.getDataReaderWriter().fromJson(
                    new JsonReader(new InputStreamReader(new FileInputStream(experimentJsonFile),
                            "UTF-16")), Experiment.class);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Unexpected internal error.", e);
        }
    }

    /**
     * Recursively add files in directory to zip archive.
     * 
     * @param dir Directory of files to add to archive.
     * @param baseDir Base directory of archive. For first call should be the
     *            same as dir.
     * @param out Zip archive output stream.
     * @throws IOException
     */
    private static void addFiles(File dir, File baseDir, ZipOutputStream out) throws IOException {
        File[] files = dir.listFiles();

        for (int i = 0; i < files.length; i++) {
            if (files[i].isDirectory()) {
                addFiles(files[i], baseDir, out);
            } else {
                ZipEntry ze = new ZipEntry(files[i].getPath().substring(
                        baseDir.getPath().length() + 1));
                out.putNextEntry(ze);
                InputStream in = new FileInputStream(files[i]);

                copy(in, out);

                in.close();
            }
        }

    }

    /**
     * Copies assets files referenced in experiment to the specified archive.
     * 
     * @param archive Archive to copy assets into.
     * @param experiment The experiment.
     * @return Modified experiment file with updated asset references.
     * @throws IOException
     */
    private static Experiment copyAssetsToArchive(ZipOutputStream out, Experiment experiment,
            File workingDir) throws IOException {

        for (Entry<Long, Asset> entry : experiment.assets.entrySet()) {
            long key = entry.getKey();
            Asset asset = entry.getValue();
            File assetFile;
            if (asset.isExternal()) {
                assetFile = new File(asset.path);
            } else {
                assetFile = new File(workingDir, asset.path);
            }
            InputStream in = new FileInputStream(assetFile);

            // Rename files so as to prevent name collision.
            String newPath = "assets/" + asset.getTypeId() + "_" + key;
            ZipEntry ze = new ZipEntry(newPath);
            out.putNextEntry(ze);

            // Update definition with new asset location.
            asset.path = newPath;
            experiment.assets.put(key, asset);

            try {
                copy(in, out);
                out.closeEntry();
            } finally {
                in.close();
            }
        }

        for (Entry<Long, Source> entry : experiment.sources.entrySet()) {
            long key = entry.getKey();
            Source source = entry.getValue();
            File assetFile;
            if (source.isExternal()) {
                assetFile = new File(source.path);
            } else {
                assetFile = new File(workingDir, source.path);
            }
            InputStream in = new FileInputStream(assetFile);

            // Rename files so as to prevent name collision.
            String newPath = "sources/" + key;
            ZipEntry ze = new ZipEntry(newPath);
            out.putNextEntry(ze);

            // Update definition with new asset location.
            source.path = newPath;
            experiment.sources.put(key, source);

            try {
                copy(in, out);
                out.closeEntry();
            } finally {
                in.close();
            }
        }

        return experiment;
    }

    /**
     * Write the experiment definition to the zip output stream. This should be
     * done as the final operation in compressing the entire experiment as
     * earlier stages may modify this data that is to be written.
     * 
     * @param out Output stream for encoding the data.
     * @param experiment Experiment definition.
     * @throws IOException
     */
    private static void writeExperimentDefinitionToArchive(ZipOutputStream out,
            Experiment experiment) throws IOException {
        String paleDef = ModelUtils.getDataReaderWriter().toJson(experiment);
        InputStream in = new ByteArrayInputStream(paleDef.getBytes("UTF-16"));

        ZipEntry ze = new ZipEntry("experiment.json");
        out.putNextEntry(ze);

        try {
            copy(in, out);
            out.closeEntry();
        } finally {
            in.close();
        }
    }

    protected static void copy(InputStream in, OutputStream out) throws IOException {
        final byte[] buffer = new byte[8192];
        int len = 0;

        while (-1 != (len = in.read(buffer))) {
            out.write(buffer, 0, len);
        }
    }
}
