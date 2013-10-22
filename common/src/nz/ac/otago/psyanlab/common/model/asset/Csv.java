
package nz.ac.otago.psyanlab.common.model.asset;

import com.google.gson.annotations.Expose;

import nz.ac.otago.psyanlab.common.R;
import nz.ac.otago.psyanlab.common.model.Asset;
import au.com.bytecode.opencsv.CSVReader;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;

public class Csv extends Asset {
    public static void countRowsAndCols(Csv csv) throws IOException {
        File file = new File(csv.path);
        CSVReader reader = new CSVReader(new FileReader(file), ',', '\"', 0);
        LineNumberReader lineCounter = new LineNumberReader(new FileReader(file));

        // Read number of columns.
        csv.mTotalCols = reader.readNext().length;

        // Read number of rows.
        while ((lineCounter.readLine()) != null) {
        }
        csv.mTotalRows = lineCounter.getLineNumber();

        lineCounter.close();
        reader.close();

        csv.mFileCounted = true;
    }

    @Expose
    public int colStart = 0;

    @Expose
    public ArrayList<String> fieldnames;

    @Expose
    public int numRows = 1;

    @Expose
    public int rowStart = 0;

    private int mTotalCols;

    private int mTotalRows;

    private boolean mFileCounted = false;

    public Csv() {
        fieldnames = new ArrayList<String>();
        mTypeId = 0x01;
        mHeaderResId = R.string.header_csv_data;
    }

    public int getTotalCols() {
        return mTotalCols;
    }

    public int getTotalRows() {
        return mTotalRows;
    }

    public boolean fileCounted() {
        return mFileCounted;
    }
}
