
package nz.ac.otago.psyanlab.common.designer.program.operand;

import com.astuetz.viewpager.extensions.PagerSlidingTabStrip;

import nz.ac.otago.psyanlab.common.R;
import nz.ac.otago.psyanlab.common.designer.program.ProgramCallbacks;
import nz.ac.otago.psyanlab.common.model.Operand;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

/**
 * A dialogue that allows the user to configure an operand.
 */
public class EditOperandDialogFragment extends DialogFragment {
    private static final String ARG_ID = "arg_id";

    private static final long INVALID_ID = -1;

    /**
     * Create a new dialogue to edit the number of iterations a loop undergoes.
     */
    public static EditOperandDialogFragment newDialog(long id) {
        EditOperandDialogFragment f = new EditOperandDialogFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_ID, id);
        f.setArguments(args);
        return f;
    }

    private ProgramCallbacks mCallbacks;

    protected long mId;

    private ViewHolder mViews;

    protected Operand mOperand;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (!(activity instanceof ProgramCallbacks)) {
            throw new RuntimeException("Activity must implement fragment callbacks.");
        }
        mCallbacks = (ProgramCallbacks)activity;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();

        Bundle args = getArguments();
        if (args != null) {
            mId = args.getLong(ARG_ID, INVALID_ID);
        }

        if (mId == INVALID_ID) {
            throw new RuntimeException("Invalid loop id given.");
        }

        mOperand = mCallbacks.getOperand(mId);

        View view = inflater.inflate(R.layout.dialogue_number_picker, null);
        mViews = new ViewHolder(view);
        mViews.initViews();
        mViews.setViewValues(mOperand);

        // Build dialogue.
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.title_edit_iterations).setView(view)
                .setPositiveButton(R.string.action_create, new OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mLoop.iterations = mViews.iterations.getValue();
                        mCallbacks.updateLoop(mId, mLoop);
                    }
                }).setNegativeButton(R.string.action_discard, new OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        getDialog().cancel();
                    }
                });

        // Create the AlertDialog object and return it
        Dialog dialog = builder.create();
        dialog.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        return dialog;
    }

    class ViewHolder {
        Button cancel;

        ViewPager pager;

        PagerSlidingTabStrip tabs;

        private FragmentStatePagerAdapter mPagerAdapter = new FragmentStatePagerAdapter(
                getChildFragmentManager()) {
            @Override
            public int getCount() {
                return 0;
            }

            @Override
            public Fragment getItem(int position) {
                switch (position) {
                    case 0:
                        return EditLiteralOperandFragment.newInstance(mOperand);
                    case 1:
                        return EditCallOperandFragment.newInstance(mOperand);
                    case 2:
                        return EditAssetOperandFragment.newInstance(mOperand);
                    default:
                        throw new RuntimeException("Fragment position " + position + " invalid.");
                }
            }
        };

        public ViewHolder(View view) {
            pager = (ViewPager)view.findViewById(R.id.pager);
            tabs = (PagerSlidingTabStrip)view.findViewById(R.id.tabs);
            cancel = (Button)view.findViewById(R.id.action_cancel);
        }

        public void setViewValues(Operand operand) {
            // Determine kind of operand
        }

        public void initViews() {
            pager.setAdapter(mPagerAdapter);

            cancel.setOnClickListener(mOnCancelListener);

            // Bind the widget to the adapter
            tabs.setViewPager(pager);
        }

    }
}
