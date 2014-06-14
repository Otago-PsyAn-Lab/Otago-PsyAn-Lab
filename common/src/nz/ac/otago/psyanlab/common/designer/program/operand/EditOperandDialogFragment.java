
package nz.ac.otago.psyanlab.common.designer.program.operand;

import com.astuetz.viewpager.extensions.PagerSlidingTabStrip;

import nz.ac.otago.psyanlab.common.R;
import nz.ac.otago.psyanlab.common.designer.util.OperandCallbacks;
import nz.ac.otago.psyanlab.common.model.Operand;
import nz.ac.otago.psyanlab.common.model.operand.CallValue;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.InputFilter;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

/**
 * A dialogue that allows the user to configure an operand. Use copy on write so
 * it is easy to roll back changes.
 */
public class EditOperandDialogFragment extends DialogFragment {
    private static final String ARG_EDIT_NAME = "arg_edit_name";

    private static final String ARG_HIDE_NAME = "arg_hide_name";

    private static final String ARG_OPERAND_ID = "arg_operand_id";

    private static final String ARG_CALLER_ID = "arg_caller_id";

    private static final String ARG_CALLER_KIND = "arg_caller_kind";

    private static final String ARG_TITLE = "arg_title";

    private static final String ARG_TYPE = "arg_type";

    private static final long INVALID_ID = -1;

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
     * @param title Title of dialogue.
     * @param type Type the operand should match.
     * @return Initialised fragment.
     */
    public static EditOperandDialogFragment newDialog(int callerKind, long callerId,
            long operandId, int type, String title) {
        EditOperandDialogFragment f = new EditOperandDialogFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_CALLER_ID, callerId);
        args.putLong(ARG_OPERAND_ID, operandId);
        args.putInt(ARG_CALLER_KIND, callerKind);
        args.putInt(ARG_TYPE, type);
        args.putString(ARG_TITLE, title);
        f.setArguments(args);
        return f;
    }

    /**
     * Create a new dialogue to edit the number of iterations a loop undergoes.
     * 
     * @param sceneId Scene id of scene operand being edited belongs to.
     * @param operandId Operand id of operand to edit.
     * @param title Title of dialogue.
     * @param type Type the operand should match.
     * @param hideName Whether to hide name field of operand.
     * @param editName Whether to make name editable.
     * @return Initialised fragment.
     */
    public static EditOperandDialogFragment newDialog(int callerKind, long callerId,
            long operandId, int type, String title, boolean hideName, boolean editName) {
        EditOperandDialogFragment f = new EditOperandDialogFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_CALLER_ID, callerId);
        args.putInt(ARG_CALLER_KIND, callerKind);
        args.putLong(ARG_OPERAND_ID, operandId);
        args.putInt(ARG_TYPE, type);
        args.putString(ARG_TITLE, title);
        args.putBoolean(ARG_HIDE_NAME, hideName);
        args.putBoolean(ARG_EDIT_NAME, editName);
        f.setArguments(args);
        return f;
    }

    public OnClickListener mOnDoneClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            mViews.saveOperand();
            mOperand = mCallbacks.getOperand(mOperandId);
            mOperand.name = mViews.name.getText().toString();
            mCallbacks.putOperand(mOperandId, mOperand);
            mOnDoneListener.OnEditOperandDialogueDone();
            getDialog().dismiss();
        }
    };

    private String mTitle;

    private ViewHolder mViews;

    protected Operand mBackupOperand;

    protected OperandCallbacks mCallbacks;

    protected boolean mEditNameEnabled;

    protected boolean mHideName;

    protected OnDoneListener mOnDoneListener = sOnDoneDummyListener;

    protected Operand mOperand;

    protected long mOperandId;

    protected int mOperandType;

    protected long mCallerId;

    protected int mCallerKind;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (!(activity instanceof OperandCallbacks)) {
            throw new RuntimeException("Activity must implement operand callbacks.");
        }
        mCallbacks = (OperandCallbacks)activity;
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
            if (!args.containsKey(ARG_TITLE)) {
                throw new RuntimeException("Expected dialogue title.");
            }
            if (!args.containsKey(ARG_TYPE)) {
                throw new RuntimeException("Expected operand type request.");
            }
            if (!args.containsKey(ARG_CALLER_KIND)) {
                throw new RuntimeException("Expected caller kind.");
            }
            if (!args.containsKey(ARG_CALLER_ID)) {
                throw new RuntimeException("Expected caller id.");
            }
            if (!args.containsKey(ARG_OPERAND_ID)) {
                throw new RuntimeException("Expected operand id.");
            }

            mCallerId = args.getLong(ARG_CALLER_ID, INVALID_ID);
            mCallerKind = args.getInt(ARG_CALLER_KIND);
            mOperandId = args.getLong(ARG_OPERAND_ID, INVALID_ID);
            mOperandType = args.getInt(ARG_TYPE);
            mTitle = args.getString(ARG_TITLE);
            mHideName = args.getBoolean(ARG_HIDE_NAME, true);
            mEditNameEnabled = args.getBoolean(ARG_EDIT_NAME, false);
            mOperand = mCallbacks.getOperand(mOperandId);
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

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);

        mCallbacks.putOperand(mOperandId, mOperand);
    }

    public interface OnDoneListener {
        void OnEditOperandDialogueDone();
    }

    class ViewHolder {
        public Button done;

        public EditText name;

        public View nameContainer;

        public ViewPager pager;

        public PagerSlidingTabStrip tabs;

        private InputFilter mFilter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest,
                    int dstart, int dend) {
                boolean keepOriginal = true;
                StringBuilder sb = new StringBuilder(end - start);

                int offset = 0;
                String s = source.toString();

                while (offset < s.length()) {
                    final int codePoint = s.codePointAt(offset);
                    if (offset == 0 && isAllowedAsFirst(codePoint) || offset > 0
                            && isAllowed(codePoint)) {
                        sb.appendCodePoint(codePoint);
                    } else {
                        keepOriginal = false;
                    }
                    offset += Character.charCount(codePoint);
                }

                if (keepOriginal) {
                    return null;
                } else {
                    if (source instanceof Spanned) {
                        SpannableString sp = new SpannableString(sb);
                        TextUtils.copySpansFrom((Spanned)source, start, sb.length(), null, sp, 0);
                        return sp;
                    } else {
                        return sb;
                    }
                }
            }

            private boolean isAllowed(int codePoint) {
                return Character.isLetterOrDigit(codePoint);
            }

            private boolean isAllowedAsFirst(int codePoint) {
                return Character.isLetter(codePoint);
            }
        };

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
                                mCallerKind, mCallerId, mOperandId, mOperandType);
                    case 1:
                        return EditCallOperandFragment.init(new EditCallOperandFragment(),
                                mCallerKind, mCallerId, mOperandId, mOperandType);
                    case 2:
                        return EditAssetOperandFragment.init(new EditAssetOperandFragment(),
                                mCallerKind, mCallerId, mOperandId, mOperandType);
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

        public ViewHolder(View view) {
            pager = (ViewPager)view.findViewById(R.id.pager);
            tabs = (PagerSlidingTabStrip)view.findViewById(R.id.tabs);
            done = (Button)view.findViewById(R.id.done);
            name = (EditText)view.findViewById(R.id.name);
            nameContainer = view.findViewById(R.id.name_container);
        }

        public void initViews() {
            pager.setAdapter(mPagerAdapter);

            // Bind the widget to the adapter
            tabs.setViewPager(pager);

            done.setOnClickListener(mOnDoneClickListener);

            name.setText(mOperand.name);
            name.setEnabled(mEditNameEnabled);
            name.setFilters(new InputFilter[] {
                mFilter
            });
            if (mHideName) {
                nameContainer.setVisibility(View.GONE);
            } else {
                nameContainer.setVisibility(View.VISIBLE);
            }
        }

        public void saveOperand() {
            ((AbsOperandFragment)mPagerAdapter.instantiateItem(pager, pager.getCurrentItem()))
                    .saveOperand();
        }
    }
}
