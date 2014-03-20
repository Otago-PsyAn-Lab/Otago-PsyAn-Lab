
package nz.ac.otago.psyanlab.common.designer.program.operand;

import nz.ac.otago.psyanlab.common.util.TonicFragment;

import android.os.Bundle;

public abstract class AbsOperandFragment extends TonicFragment {
    private static final String ARG_TYPE = "arg_type";

    public static <T extends TonicFragment> T init(T f, long objectId, int type) {
        f = TonicFragment.init(f, objectId);
        Bundle args = f.getArguments();
        args.putInt(ARG_TYPE, type);
        f.setArguments(args);
        return f;
    }

    protected int mOperandType;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            mOperandType = savedInstanceState.getInt(ARG_TYPE);
        } else {
            Bundle args = getArguments();
            if (args != null) {
                mOperandType = args.getInt(ARG_TYPE);
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt(ARG_TYPE, mOperandType);
    }
}
