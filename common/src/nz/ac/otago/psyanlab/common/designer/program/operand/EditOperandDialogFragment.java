
package nz.ac.otago.psyanlab.common.designer.program.operand;

import com.astuetz.viewpager.extensions.PagerSlidingTabStrip;

import nz.ac.otago.psyanlab.common.R;
import nz.ac.otago.psyanlab.common.designer.program.util.ProgramCallbacks;
import nz.ac.otago.psyanlab.common.designer.util.ActionCallbacks;
import nz.ac.otago.psyanlab.common.model.Operand;

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
    private static final String ARG_ID = "arg_id";

    private static final String ARG_TITLE = "arg_title";

    private static final String ARG_TYPE = "arg_type";

    private static final long INVALID_ID = -1;

    /**
     * Create a new dialogue to edit the number of iterations a loop undergoes.
     * 
     * @param id Operand id of operand to edit.
     * @param title
     * @param typeBoolean type the operand should match.
     */
    public static EditOperandDialogFragment newDialog(long id, int type, String title) {
        EditOperandDialogFragment f = new EditOperandDialogFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_ID, id);
        args.putInt(ARG_TYPE, type);
        args.putString(ARG_TITLE, title);
        f.setArguments(args);
        return f;
    }

    public OnClickListener mOnDoneClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
        }
    };

    private ActionCallbacks mCallbacks;

    private String mTitle;

    private ViewHolder mViews;

    protected Operand mBackupOperand;

    protected long mId;

    protected int mOperandType;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (!(activity instanceof ProgramCallbacks)) {
            throw new RuntimeException("Activity must implement fragment callbacks.");
        }
        mCallbacks = (ActionCallbacks)activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialogue_edit_operand, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle args = getArguments();
        if (args != null) {
            mId = args.getLong(ARG_ID, INVALID_ID);
            mOperandType = args.getInt(ARG_TYPE);
            mTitle = args.getString(ARG_TITLE);
        }

        if (mId == INVALID_ID) {
            throw new RuntimeException("Invalid operand id given.");
        }

        Dialog dialog = getDialog();
        dialog.setTitle(mTitle);
        dialog.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        mViews = new ViewHolder(view);
        mViews.initViews();
    }

    class ViewHolder {
        private Button done;

        private FragmentStatePagerAdapter mPagerAdapter = new FragmentStatePagerAdapter(
                getChildFragmentManager()) {
            @Override
            public int getCount() {
                return 1;
            }

            @Override
            public Fragment getItem(int position) {
                switch (position) {
                    case 0:
                        return EditLiteralOperandFragment.init(new EditLiteralOperandFragment(),
                                mId, mOperandType);
                    case 1:
                        return EditLiteralOperandFragment.init(new EditLiteralOperandFragment(),
                                mId, mOperandType);
                        // return EditCallOperandFragment.init(new
                        // EditCallOperandFragment(), mId, mOperandType);
                    case 2:
                        return EditLiteralOperandFragment.init(new EditLiteralOperandFragment(),
                                mId, mOperandType);
                        // return EditAssetOperandFragment.init(new
                        // EditAssetOperandFragment(), mId, mOperandType);
                    default:
                        throw new RuntimeException("Invalid fragment position " + position);
                }
            }

            @Override
            public CharSequence getPageTitle(int position) {
                switch (position) {
                    case 0:
                        return "Literal";
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
    }
}
