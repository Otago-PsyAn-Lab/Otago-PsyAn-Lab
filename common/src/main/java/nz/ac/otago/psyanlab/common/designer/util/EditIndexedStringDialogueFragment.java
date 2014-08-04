
package nz.ac.otago.psyanlab.common.designer.util;

import android.os.Bundle;

public class EditIndexedStringDialogueFragment extends EditStringDialogueFragment {
    public static final String RESULT_INDEX = "result_index";

    protected static final String ARG_INDEX = "arg_index";

    public static EditIndexedStringDialogueFragment init(
            EditIndexedStringDialogueFragment fragment, int titleResId, String defaultValue,
            int hintResId, int requestCode, int index) {
        init(fragment, titleResId, defaultValue, hintResId, requestCode);
        return addIndex(fragment, index);
    }

    public static EditIndexedStringDialogueFragment init(
            EditIndexedStringDialogueFragment fragment, int titleResId, String defaultValue,
            int hintResId, int requestCode, int index, int layoutResId) {
        EditStringDialogueFragment.init(fragment, titleResId, defaultValue, hintResId, requestCode,
                layoutResId);
        return addIndex(fragment, index);
    }

    private static EditIndexedStringDialogueFragment addIndex(
            EditIndexedStringDialogueFragment fragment, int index) {
        Bundle args = fragment.getArguments();
        args.putInt(ARG_INDEX, index);
        args.putBoolean(ARG_DELETE_ENABLED, true);
        return fragment;
    }

    @Override
    protected Bundle getResult() {
        Bundle result = super.getResult();
        result.putInt(RESULT_INDEX, getArguments().getInt(ARG_INDEX));
        return result;
    }

    @Override
    protected Bundle getResultDelete() {
        Bundle result = super.getResultDelete();
        result.putInt(RESULT_INDEX, getArguments().getInt(ARG_INDEX));
        return result;
    }
}
