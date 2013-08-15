
package nz.ac.otago.psyanlab.common.designer.assets;

import nz.ac.otago.psyanlab.common.R;
import nz.ac.otago.psyanlab.common.designer.assets.AssetDetailFragment.DetailFragmentI;
import nz.ac.otago.psyanlab.common.model.asset.Csv;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.NumberPicker.OnValueChangeListener;

import java.io.IOException;
import java.util.ArrayList;

public class CSVDataDetailFragment extends Fragment implements DetailFragmentI {
    private static final String KEY_ASSET_ID = "key_asset_id";

    public static CSVDataDetailFragment newInstance(long assetId) {
        CSVDataDetailFragment f = new CSVDataDetailFragment();
        f.mAssetId = assetId;
        Log.d("CSVDataDetailFragment", "Create for asset id " + assetId);
        return f;
    }

    private long mAssetId;

    private AssetTabFragmentsCallbacks mCallbacks;

    private Csv mCsv;

    private String[][] mCsvContent;

    private ViewHolder mViews;

    protected TextWatcher mNameChangeWatcher = new TextWatcher() {
        @Override
        public void afterTextChanged(Editable s) {
            mCsv.name = s.toString();
            mCallbacks.updateAsset(mAssetId, mCsv);
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }
    };

    protected OnValueChangeListener mRowStartValueChangeListener = new OnValueChangeListener() {
        @Override
        public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
            mViews.numRows.setMaxValue(mCsvContent.length - newVal + 1);
        }
    };

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (!(activity instanceof AssetTabFragmentsCallbacks)) {
            throw new RuntimeException("Activity must implement fragment callbacks.");
        }
        mCallbacks = (AssetTabFragmentsCallbacks)activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_designer_asset_csv_data_detail, container, false);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        saveAsset();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putLong(KEY_ASSET_ID, mAssetId);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mViews = new ViewHolder(view);

        if (savedInstanceState != null) {
            mAssetId = savedInstanceState.getLong(KEY_ASSET_ID);
        }

        mCsv = (Csv)mCallbacks.getAsset(mAssetId);

        try {
            mCsvContent = Csv.readAllData(mCsv, mCallbacks.getWorkingDirectory());
        } catch (IOException e) {
            e.printStackTrace();
        }

        mViews.setViewValues(mCsv, mCsvContent);
        mViews.initViews();
    }

    @Override
    public void saveAsset() {
        mCsv.name = mViews.name.getText().toString();

        ArrayList<String> fields = new ArrayList<String>();
        String columnNames = mViews.colNames.getText().toString();
        String[] cols = TextUtils.split(columnNames, ",");
        for (int i = 0; i < cols.length; i++) {
            fields.add(cols[i]);
        }
        mCsv.fieldnames = fields;

        mCsv.colStart = mViews.colStart.getValue();
        mCsv.rowStart = mViews.rowStart.getValue();
        mCsv.numRows = mViews.numRows.getValue();

        mCallbacks.updateAsset(mAssetId, mCsv);
    }

    private class ViewHolder {
        public EditText name;

        private EditText colNames;

        private NumberPicker colStart;

        private NumberPicker numRows;

        private NumberPicker rowStart;

        public ViewHolder(View view) {
            name = (EditText)view.findViewById(R.id.name);
            colNames = (EditText)view.findViewById(R.id.column_names);
            colStart = (NumberPicker)view.findViewById(R.id.col_start);
            rowStart = (NumberPicker)view.findViewById(R.id.row_start);
            numRows = (NumberPicker)view.findViewById(R.id.num_rows);
        }

        public void initViews() {
            colStart.setWrapSelectorWheel(false);

            rowStart.setWrapSelectorWheel(false);
            rowStart.setOnValueChangedListener(mRowStartValueChangeListener);

            numRows.setWrapSelectorWheel(false);

            name.addTextChangedListener(mNameChangeWatcher);
        }

        public void setViewValues(Csv asset, String[][] csvContent) {
            name.setText(asset.name);

            colNames.setText(TextUtils.join(",", asset.fieldnames));

            colStart.setMaxValue(csvContent[0].length);
            colStart.setMinValue(1);
            colStart.setValue(asset.colStart);

            rowStart.setMaxValue(csvContent.length);
            rowStart.setMinValue(1);
            rowStart.setValue(asset.rowStart);

            numRows.setMaxValue(csvContent.length);
            numRows.setMinValue(1);
            numRows.setValue(asset.numRows);

            numRows.setMaxValue(csvContent.length - rowStart.getValue() + 1);
        }
    }
}
