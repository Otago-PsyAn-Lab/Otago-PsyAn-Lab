
package nz.ac.otago.psyanlab.common.model.asset;

import com.google.gson.annotations.Expose;

import nz.ac.otago.psyanlab.common.R;
import nz.ac.otago.psyanlab.common.model.Asset;
import au.com.bytecode.opencsv.CSVReader;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Csv extends Asset {
    @Expose
    public int colStart = 1;

    @Expose
    public ArrayList<String> fieldnames;

    @Expose
    public int numRows = 1;

    @Expose
    public int rowStart = 1;

    public Csv() {
        fieldnames = new ArrayList<String>();
        mTypeId = 0x01;
        mHeaderResId = R.string.header_csv_data;
    }

    public static String[][] readAllData(Csv csv, File workingDirectory) throws IOException {
        CSVReader reader = new CSVReader(new FileReader(new File(workingDirectory, csv.filename)),
                ',', '\"', 0);

        // Read CSV from file.
        ArrayList<String[]> rows = new ArrayList<String[]>();
        int maxCols = 0;
        while (true) {
            String[] row = reader.readNext();
            if (row == null) {
                break;
            }
            if (maxCols < row.length) {
                maxCols = row.length;
            }
            rows.add(row);
        }

        // Load all data into mData.
        String[][] data = new String[rows.size()][maxCols];
        for (int i = 0; i < rows.size(); i++) {
            String[] row = rows.get(i);
            for (int j = 0; j < row.length; j++) {
                data[i][j] = row[j];
            }
        }
        reader.close();

        return data;
    }
}
