
package nz.ac.otago.psyanlab.common.designer.assets;

import nz.ac.otago.psyanlab.common.R;
import nz.ac.otago.psyanlab.common.designer.util.ExperimentUtils;
import nz.ac.otago.psyanlab.common.designer.util.NumberPickerDialogFragment;
import nz.ac.otago.psyanlab.common.designer.util.NumberPickerDialogFragment.OnConfirmedValueListener;
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
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import java.io.IOException;
import java.util.ArrayList;

public class CSVDataDetailFragment extends Fragment {
    private static final String KEY_ASSET_ID = "key_asset_id";

    public static CSVDataDetailFragment newInstance(long assetId) {
        CSVDataDetailFragment f = new CSVDataDetailFragment();
        f.mAssetId = assetId;
        Log.d("CSVDataDetailFragment", "Create for asset id " + assetId);
        return f;
    }

    public TextWatcher mColNameChangeWatcher = new TextWatcher() {
        @Override
        public void afterTextChanged(Editable s) {
            ArrayList<String> fields = new ArrayList<String>();
            String columnNames = mViews.colNames.getText().toString();
            String[] cols = TextUtils.split(columnNames, ",");
            for (int i = 0; i < cols.length; i++) {
                fields.add(cols[i]);
            }
            mCsv.fieldnames = fields;
            mCallbacks.updateAsset(mAssetId, mCsv);
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }
    };

    private long mAssetId;

    private AssetTabFragmentsCallbacks mCallbacks;

    private OnClickListener mColStartClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            showEditStartColumnDialogue();
        }
    };

    private Csv mCsv;

    private String[][] mCsvContent;

    private OnClickListener mNumRowsClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            showEditNumRowsDialogue();
        }
    };

    private OnClickListener mRowStartClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            showEditStartRowDialogue();
        }
    };

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

    private void showEditNumRowsDialogue() {
        NumberPickerDialogFragment dialog = NumberPickerDialogFragment.newDialog(mCsv.numRows, 1,
                mCsvContent.length - mCsv.rowStart);
        dialog.setOnConfirmedValueListener(new OnConfirmedValueListener() {
            @Override
            public void onConfirmedValue(int value) {
                mCsv.numRows = value;
                mViews.numRows.setText(String.valueOf(mCsv.numRows));
                mCallbacks.updateAsset(mAssetId, mCsv);
            }
        });
        dialog.show(getChildFragmentManager(), "dialog_edit_col_start");
    }

    private void showEditStartColumnDialogue() {
        NumberPickerDialogFragment dialog = NumberPickerDialogFragment.newDialog(
                ExperimentUtils.zeroBasedToUserValue(mCsv.colStart), 1, mCsvContent[0].length);
        dialog.setOnConfirmedValueListener(new OnConfirmedValueListener() {
            @Override
            public void onConfirmedValue(int value) {
                mCsv.colStart = ExperimentUtils.userValueToZeroBased(value);
                mViews.colStart.setText(String.valueOf(ExperimentUtils
                        .zeroBasedToUserValue(mCsv.colStart)));
                mCallbacks.updateAsset(mAssetId, mCsv);
            }
        });
        dialog.show(getChildFragmentManager(), "dialog_edit_col_start");
    }

    private void showEditStartRowDialogue() {
        NumberPickerDialogFragment dialog = NumberPickerDialogFragment.newDialog(
                ExperimentUtils.zeroBasedToUserValue(mCsv.rowStart), 1, mCsvContent.length);
        dialog.setOnConfirmedValueListener(new OnConfirmedValueListener() {
            @Override
            public void onConfirmedValue(int value) {
                mCsv.rowStart = ExperimentUtils.userValueToZeroBased(value);
                if (mCsv.rowStart + mCsv.numRows > mCsvContent.length) {
                    mCsv.numRows = mCsvContent.length - mCsv.rowStart;
                }
                mViews.rowStart.setText(String.valueOf(ExperimentUtils
                        .zeroBasedToUserValue(mCsv.rowStart)));
                mViews.numRows.setText(String.valueOf(mCsv.numRows));
                mCallbacks.updateAsset(mAssetId, mCsv);
            }
        });
        dialog.show(getChildFragmentManager(), "dialog_edit_col_start");
    }

    private class ViewHolder {
        public EditText name;

        private EditText colNames;

        private Button colStart;

        private Button numRows;

        private Button rowStart;

        public ViewHolder(View view) {
            name = (EditText)view.findViewById(R.id.name);
            colNames = (EditText)view.findViewById(R.id.column_names);
            colStart = (Button)view.findViewById(R.id.col_start);
            rowStart = (Button)view.findViewById(R.id.row_start);
            numRows = (Button)view.findViewById(R.id.num_rows);
        }

        public void initViews() {
            colStart.setOnClickListener(mColStartClickListener);
            rowStart.setOnClickListener(mRowStartClickListener);
            numRows.setOnClickListener(mNumRowsClickListener);

            name.addTextChangedListener(mNameChangeWatcher);
            colNames.addTextChangedListener(mColNameChangeWatcher);
        }

        public void setViewValues(Csv asset, String[][] csvContent) {
            name.setText(asset.name);

            colNames.setText(TextUtils.join(",", asset.fieldnames));

            colStart.setText(String.valueOf(ExperimentUtils.zeroBasedToUserValue(mCsv.colStart)));
            rowStart.setText(String.valueOf(ExperimentUtils.zeroBasedToUserValue(mCsv.rowStart)));
            numRows.setText(String.valueOf(mCsv.numRows));
        }
    }
}
