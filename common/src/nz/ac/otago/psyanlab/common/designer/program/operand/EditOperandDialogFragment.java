
package nz.ac.otago.psyanlab.common.designer.program.operand;

import com.astuetz.viewpager.extensions.PagerSlidingTabStrip;

import nz.ac.otago.psyanlab.common.R;
import nz.ac.otago.psyanlab.common.designer.util.OperandCallbacks;
import nz.ac.otago.psyanlab.common.model.Operand;
import nz.ac.otago.psyanlab.common.model.operand.CallValue;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;

/**
 * A dialogue that allows the user to configure an operand. Use copy on write so
 * it is easy to roll back changes.
 */
public class EditOperandDialogFragment extends DialogFragment {
    private static final String ARG_OPERAND_ID = "arg_operand_id";

    private static final String ARG_SCENE_ID = "arg_scene_id";

    private static final String ARG_TITLE = "arg_title";

    private static final String ARG_TYPE = "arg_type";

    private static final long INVALID_ID = -1;

    protected OperandCallbacks mCallbacks;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (!(activity instanceof OperandCallbacks)) {
            throw new RuntimeException("Activity must implement operand callbacks.");
        }
        mCallbacks = (OperandCallbacks)activity;
    }

    private static final OnDoneListener sOnDoneDummyListener = new OnDoneListener() {
        @Override
        public void OnEditOperandDialogueDone() {
        }
    };

    /**
     * Create a new dialogue to edit the number of iterations a loop undergoes.
     * 
     * @param sceneId Scene id of scene operand being edited belongs to.
     * @param operandId Operand id of operand to edit.
     * @param title
     * @param typeBoolean type the operand should match.
     */
    public static EditOperandDialogFragment newDialog(long sceneId, long operandId, int type,
            String title) {
        EditOperandDialogFragment f = new EditOperandDialogFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_SCENE_ID, sceneId);
        args.putLong(ARG_OPERAND_ID, operandId);
        args.putInt(ARG_TYPE, type);
        args.putString(ARG_TITLE, title);
        f.setArguments(args);
        return f;
    }

    public OnClickListener mOnDoneClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            mViews.saveOperand();
            mOnDoneListener.OnEditOperandDialogueDone();
            getDialog().dismiss();
        }
    };

    private String mTitle;

    private ViewHolder mViews;

    protected Operand mBackupOperand;

    protected OnDoneListener mOnDoneListener = sOnDoneDummyListener;

    protected long mOperandId;

    protected int mOperandType;

    protected long mSceneId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialogue_edit_operand, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle args = getArguments();
        if (args != null) {
            if (!args.containsKey(ARG_TITLE)) {
                throw new RuntimeException("Expected dialogue title.");
            }
            if (!args.containsKey(ARG_TYPE)) {
                throw new RuntimeException("Expected operand type request.");
            }
            if (!args.containsKey(ARG_SCENE_ID)) {
                throw new RuntimeException("Expected scene id.");
            }
            if (!args.containsKey(ARG_OPERAND_ID)) {
                throw new RuntimeException("Expected operand id.");
            }

            mSceneId = args.getLong(ARG_SCENE_ID, INVALID_ID);
            mOperandId = args.getLong(ARG_OPERAND_ID, INVALID_ID);
            mOperandType = args.getInt(ARG_TYPE);
            mTitle = args.getString(ARG_TITLE);
        }

        if (mOperandId == INVALID_ID) {
            throw new RuntimeException("Invalid operand id given.");
        }

        Dialog dialog = getDialog();
        dialog.setTitle(mTitle);
        dialog.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        mViews = new ViewHolder(view);
        mViews.initViews();

        Operand operand = mCallbacks.getOperand(mOperandId);
        if (operand instanceof CallValue) {
            mViews.pager.setCurrentItem(1);
        } else {
            mViews.pager.setCurrentItem(0);
        }
    }

    public void setOnDoneListener(OnDoneListener listener) {
        mOnDoneListener = listener;
    }

    public interface OnDoneListener {
        void OnEditOperandDialogueDone();
    }

    class ViewHolder {
        private Button done;

        private FragmentStatePagerAdapter mPagerAdapter = new FragmentStatePagerAdapter(
                getChildFragmentManager()) {
            @Override
            public int getCount() {
                return 2;
            }

            @Override
            public Fragment getItem(int position) {
                switch (position) {
                    case 0:
                        return EditLiteralOperandFragment.init(new EditLiteralOperandFragment(),
                                mSceneId, mOperandId, mOperandType);
                    case 1:
                        return EditCallOperandFragment.init(new EditCallOperandFragment(),
                                mSceneId, mOperandId, mOperandType);
                    case 2:
                        return EditAssetOperandFragment.init(new EditAssetOperandFragment(),
                                mSceneId, mOperandId, mOperandType);
                    default:
                        throw new RuntimeException("Invalid fragment position " + position);
                }
            }

            @Override
            public CharSequence getPageTitle(int position) {
                switch (position) {
                    case 0:
                        return "Expression";
                    case 1:
                        return "Call";
                    case 2:
                        return "Asset";
                    default:
                        throw new RuntimeException("Invalid fragment position " + position);
                }
            };
        };

        private ViewPager pager;

        private PagerSlidingTabStrip tabs;

        public ViewHolder(View view) {
            pager = (ViewPager)view.findViewById(R.id.pager);
            tabs = (PagerSlidingTabStrip)view.findViewById(R.id.tabs);
            done = (Button)view.findViewById(R.id.done);
        }

        public void initViews() {
            pager.setAdapter(mPagerAdapter);

            // Bind the widget to the adapter
            tabs.setViewPager(pager);

            done.setOnClickListener(mOnDoneClickListener);
        }

        public void saveOperand() {
            ((AbsOperandFragment)mPagerAdapter.instantiateItem(pager, pager.getCurrentItem()))
                    .saveOperand();
        }
    }
}
