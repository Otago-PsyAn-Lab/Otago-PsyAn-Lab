
package nz.ac.otago.psyanlab.common.model;

import com.google.gson.annotations.Expose;

import nz.ac.otago.psyanlab.common.model.chansrc.Field;
import nz.ac.otago.psyanlab.common.model.util.NameResolverFactory;

import android.content.Context;

import au.com.bytecode.opencsv.CSVReader;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;

public class Source extends ExperimentObject implements Comparable<Source> {
    public static final String FILE_ENDINGS = ".*\\.csv";

    public static void countRowsAndCols(Source csv, File file) throws IOException {
        CSVReader reader = new CSVReader(new FileReader(file), ',', '\"', 0);
        LineNumberReader lineCounter = new LineNumberReader(new FileReader(file));

        // Read number of columns.
        csv.mTotalCols = reader.readNext().length;

        // Read number of rows.
        while (lineCounter.readLine() != null) {
        }
        csv.mTotalRows = lineCounter.getLineNumber();

        lineCounter.close();
        reader.close();

        csv.mFileCounted = true;
    }

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

    @Override
    public int compareTo(Source another) {
        if (name != null && another != null) {
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
        return new MethodNameFactory();
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
        return ExperimentObject.KIND_ASSET;
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

    protected static class MethodNameFactory extends Asset.MethodNameFactory {
        @Override
        public int getResId(int lookup) {
            switch (lookup) {
                default:
                    return super.getResId(lookup);
            }
        }
    }
}
