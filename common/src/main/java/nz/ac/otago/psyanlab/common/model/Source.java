/*
 * Copyright (C) 2012, 2013, 2014 University of Otago, Tonic Artos <tonic.artos@gmail.com>
 *
 * Otago PsyAn Lab is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 * In accordance with Section 7(b) of the GNU General Public License version 3,
 * all legal notices and author attributions must be preserved.
 */

package nz.ac.otago.psyanlab.common.model;

import com.google.gson.annotations.Expose;

import nz.ac.otago.psyanlab.common.R;
import nz.ac.otago.psyanlab.common.model.chansrc.Field;
import nz.ac.otago.psyanlab.common.model.util.ModelUtils;
import nz.ac.otago.psyanlab.common.model.util.NameResolverFactory;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;

import au.com.bytecode.opencsv.CSVReader;
import nz.ac.otago.psyanlab.common.model.util.Type;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.SortedSet;

public class Source extends ExperimentObject implements Comparable<Source> {

    public static final String FILE_ENDINGS = ".*\\.csv";

    @Expose
    public int colStart = 0;

    @Expose
    public ArrayList<Field> columns;

    @Expose
    public String filename;

    @Expose
    public long filesize;

    @Expose
    public String name;

    @Expose
    public int numRows = 1;

    @Expose
    public String path;

    @Expose
    public int rowStart = 0;

    private boolean mFileCounted = false;

    private boolean mIsExternal = false;

    private int mTotalCols;

    private int mTotalRows;

    public Source() {
        columns = new ArrayList<Field>();
    }

    public static void countRowsAndCols(Source csv, File file) throws IOException {
        CSVReader reader = new CSVReader(new FileReader(file), ',', '\"', 0);
        LineNumberReader lineCounter = new LineNumberReader(new FileReader(file));

        // Read number of columns.
        csv.mTotalCols = reader.readNext().length;

        // Read number of rows.
        while (true) {
            if (lineCounter.readLine() == null) {
                break;
            }
        }
        csv.mTotalRows = lineCounter.getLineNumber();

        lineCounter.close();
        reader.close();

        csv.mFileCounted = true;
    }

    @Override
    public int compareTo(@NonNull Source another) {
        if (name != null) {
            return name.compareToIgnoreCase(another.name);
        }
        return 0;
    }

    public boolean fileCounted() {
        return mFileCounted;
    }

    @Override
    public String getExperimentObjectName(Context context) {
        return name;
    }

    @Override
    public NameResolverFactory getMethodNameFactory() {
        return new MethodNameFactory(this);
    }

    public int getTotalCols() {
        return mTotalCols;
    }

    public int getTotalRows() {
        return mTotalRows;
    }

    public boolean isExternal() {
        return mIsExternal;
    }

    @Override
    public int kind() {
        return ExperimentObject.KIND_SOURCE;
    }

    @Override
    public void loadInMatchingMethods(Context context, int returnType, SortedSet<MethodData> out) {
        for (Field column : columns) {
            if ((returnType & column.type) != 0) {
                MethodData data = new MethodData();
                data.id = column.id;
                data.name = context.getString(R.string.format_source_method_name, column.name);
                data.returnType = column.type;
                out.add(data);
            }
        }
    }

    @Override
    public boolean satisfiesFilter(int filter) {
        for (Field column : columns) {
            if ((filter & column.type) != 0) {
                return true;
            }
        }
        return false;
    }

    public void setExternalFile(File file) {
        filesize = file.length();
        path = file.getPath();
        filename = file.getName();
        name = file.getName();
        mIsExternal = true;
    }

    @Override
    public NameResolverFactory getParameterNameFactory() {
        throw new RuntimeException("Unsupported method");
    }

    @Override
    public ParameterData[] getParameters(Activity activity, int methodId) {
        ParameterData[] parameters = new ParameterData[1];
        ParameterData data = new ParameterData();
        data.id = 0;
        data.type = Type.TYPE_INTEGER;
        data.name = activity.getString(R.string.label_parameter_row);
        parameters[0] = data;
        return parameters;
    }

    protected static class MethodNameFactory extends Asset.MethodNameFactory {
        private final Source mSource;

        public MethodNameFactory(Source source) {
            mSource = source;
        }

        @Override
        public String getName(Context context, int lookup) {
            if (lookup >= mSource.columns.size()) {
                return super.getName(context, lookup);
            }

            return context.getString(R.string.format_source_method_name,
                                     mSource.columns.get(lookup).name);
        }
    }
}
