
package nz.ac.otago.psyanlab.common.designer.program.operand;

import nz.ac.otago.psyanlab.common.util.TonicFragment;

import android.os.Bundle;

public abstract class AbsOperandFragment extends TonicFragment {
    protected static final String ARG_TYPE = "arg_type";

    protected static final String ARG_SCENE_ID = "arg_scene_id";

    public static <T extends TonicFragment> T init(T f, long sceneId, long objectId, int type) {
        f = TonicFragment.init(f, objectId);
        Bundle args = f.getArguments();
        args.putInt(ARG_TYPE, type);
        args.putLong(ARG_SCENE_ID, sceneId);
        f.setArguments(args);
        return f;
    }

    protected int mOperandType;

    protected long mSceneId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            mOperandType = savedInstanceState.getInt(ARG_TYPE);
        } else {
            Bundle args = getArguments();
            if (args != null) {
                if (!args.containsKey(ARG_TYPE)) {
                    throw new RuntimeException("Expected operand type requirement.");
                }
                if (!args.containsKey(ARG_SCENE_ID)) {
                    throw new RuntimeException("Expected scene id.");
                }
                mOperandType = args.getInt(ARG_TYPE);
                mSceneId = args.getLong(ARG_SCENE_ID);
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt(ARG_TYPE, mOperandType);
    }

    abstract public void saveOperand();
}
