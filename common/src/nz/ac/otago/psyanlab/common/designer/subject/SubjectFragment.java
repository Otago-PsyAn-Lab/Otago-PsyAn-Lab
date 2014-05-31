
package nz.ac.otago.psyanlab.common.designer.subject;

import com.mobeta.android.dslv.DragSortListView.DragSortListener;
import com.mobeta.android.dslv.DragSortListView.RemoveListener;

import nz.ac.otago.psyanlab.common.R;
import nz.ac.otago.psyanlab.common.designer.ExperimentDesignerActivity.LandingPageDataChangeListener;
import nz.ac.otago.psyanlab.common.designer.subject.SubjectFragment.ListItemViewHolder.OnEditDetailClickListener;
import nz.ac.otago.psyanlab.common.designer.subject.SubjectFragment.ListItemViewHolder.TextFocusedListener;
import nz.ac.otago.psyanlab.common.model.LandingPage;
import nz.ac.otago.psyanlab.common.model.Subject;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class SubjectFragment extends Fragment implements LandingPageDataChangeListener {
    private SubjectRowAdapter mAdapter;

    private Callbacks mCallbacks;

    private LandingPage mLandingPage;

    private OnEditDetailClickListener mOnEditDetailClickListener = new OnEditDetailClickListener() {
        @Override
        public void onEditDetailClick(int position) {
            showEditTypeDialogue(position, R.string.title_set_type);
        }
    };

    private ViewHolder mViews;

    protected OnClickListener mOnAddSubjectQuestionClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            showEditTypeDialogue(-1, R.string.title_set_type);
        }
    };

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (!(activity instanceof Callbacks)) {
            throw new RuntimeException("Activity must implement fragment callbacks.");
        }
        mCallbacks = (Callbacks)activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_designer_subject, container, false);
        ListView list = (ListView)view.findViewById(R.id.list);
        list.addHeaderView(inflater.inflate(R.layout.header_subject_details, list, false));
        return view;
    }

    @Override
    public void onDetach() {
        mLandingPage.title = mViews.mTitle.getText().toString();
        mLandingPage.introduction = mViews.mIntroduction.getText().toString();

        mCallbacks.storeLandingPage(mLandingPage);
        super.onDetach();
    }

    @Override
    public void onLandingPageDataChange() {
        mLandingPage = mCallbacks.getLandingPage();
        mViews.setViewValues(mLandingPage);
        mAdapter.setRows(mLandingPage.subjectDetails);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mLandingPage = mCallbacks.getLandingPage();
        mCallbacks.addLandingPageDataChangeListener(this);

        mViews = new ViewHolder(view);

        mAdapter = new SubjectRowAdapter(getActivity(), mViews.mList, mOnEditDetailClickListener);
        mAdapter.setRows(mLandingPage.subjectDetails);

        mViews.initViews(mAdapter);
        mViews.setViewValues(mLandingPage);
    }

    protected void showEditTypeDialogue(final int rowId, final int titleResId) {
        // Clear any existing dialog.
        FragmentManager fm = getChildFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Fragment prev = fm.findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        // Create and show authentication dialog.
        DialogFragment dialog = SubjectStatisticTypeDialogFragment.newDialog(rowId, getResources()
                .getString(titleResId));
        dialog.show(ft, "dialog");
    }

    public static interface Callbacks {
        void addLandingPageDataChangeListener(LandingPageDataChangeListener listener);

        LandingPage getLandingPage();

        void storeLandingPage(LandingPage landingPage);
    }

    private static class SubjectRowAdapter extends BaseAdapter implements DragSortListener {
        private OnEditDetailClickListener mEditDetailDialogueCallback;

        private LayoutInflater mInflater;

        private ListView mList;

        private ArrayList<Subject> mRows;

        /**
         * Hack around edit text losing focus inside a list view.
         */
        private TextFocusedListener mTextFocusedListener = new TextFocusedListener() {
            @Override
            public void onTextFocused(int position) {
                mList.setSelection(position);

                ViewGroup dragSortListItem = (ViewGroup)mList.getChildAt(position
                        - mList.getFirstVisiblePosition() + mList.getHeaderViewsCount());
                if (dragSortListItem == null) {
                    return;
                }

                View listItem = dragSortListItem.getChildAt(0);
                ListItemViewHolder viewHolder = (ListItemViewHolder)listItem.getTag();
                viewHolder.focusText();
            }
        };

        public SubjectRowAdapter(Context context, ListView list,
                OnEditDetailClickListener editDetailDialogueCallback) {
            mList = list;
            mRows = new ArrayList<Subject>();
            mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mEditDetailDialogueCallback = editDetailDialogueCallback;
        }

        @Override
        public void drag(int from, int to) {
        }

        @Override
        public void drop(int from, int to) {
            Subject move = mRows.remove(from);
            mRows.add(to, move);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mRows.size();
        }

        @Override
        public Subject getItem(int position) {
            return mRows.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ListItemViewHolder holder;

            // View reuse seems to play havoc with interaction between edit text
            // views and the IME. So for now a new view is created everytime.
            // if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_item_subject_question, parent, false);
            holder = new ListItemViewHolder(convertView);
            holder.initViews(this, mTextFocusedListener, mEditDetailDialogueCallback);
            convertView.setTag(holder);
            // } else {
            // holder = (ListItemViewHolder)convertView.getTag();
            // }

            holder.setViewValues(getItem(position), position);

            return convertView;
        }

        @Override
        public void remove(int which) {
            mRows.remove(which);
            notifyDataSetChanged();
        }

        public void setRows(ArrayList<Subject> rows) {
            mRows = rows;
            notifyDataSetChanged();
        }
    }

    private class ViewHolder {
        private View mAdd;

        private EditText mIntroduction;

        private ListView mList;

        private EditText mTitle;

        public ViewHolder(View view) {
            mTitle = (EditText)view.findViewById(R.id.title);
            mIntroduction = (EditText)view.findViewById(R.id.description);
            mList = (ListView)view.findViewById(R.id.list);
            mAdd = view.findViewById(R.id.add);
        }

        public void initViews(ListAdapter adapter) {
            mList.setAdapter(adapter);
            mAdd.setOnClickListener(mOnAddSubjectQuestionClickListener);
        }

        public void setViewValues(LandingPage landingPage) {
            mTitle.setText(landingPage.title);
            mIntroduction.setText(landingPage.introduction);
        }
    }

    protected static class ListItemViewHolder {
        protected static Handler mHandler;

        protected static EnsureFocus sEnsureFocus = new EnsureFocus();

        private Subject mDetail;

        /**
         * Button to call a dialogue to set the type of subject detail this is.
         * Also holds the detail object this row is working on.
         */
        private Button mEditDetail;

        private OnEditDetailClickListener mEditDetailDialogueCallback;

        private OnClickListener mOnEditClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditDetailDialogueCallback.onEditDetailClick(mPosition);
            }
        };

        private OnFocusChangeListener mOnFocusChangeListener = new OnFocusChangeListener() {
            @Override
            public void onFocusChange(final View v, boolean hasFocus) {
                // Only show the remove icon when the row is selected.
                if (hasFocus) {
                    // Use the ugly hack around edit text problems in list
                    // views.
                    if (mHandler == null) {
                        mHandler = new Handler();
                    }

                    mHandler.removeCallbacks(sEnsureFocus);
                    sEnsureFocus.setTextFocusedListener(mTextFocusedListener);
                    sEnsureFocus.setPosition(mPosition);
                    mHandler.postDelayed(sEnsureFocus, 200);

                    mRemove.setVisibility(View.VISIBLE);
                } else {
                    mRemove.setVisibility(View.INVISIBLE);
                }
            }
        };

        private OnClickListener mRemoveClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPosition != ListView.INVALID_POSITION) {
                    mRemoveListener.remove(mPosition);
                }
            }
        };

        private RemoveListener mRemoveListener;

        /**
         * Text describing the statistic being collected.
         */
        private EditText mText;

        private TextFocusedListener mTextFocusedListener;

        private TextWatcher mTextWatcher = new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                mDetail.text = s.toString();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        };

        protected int mPosition;

        /**
         * Button to remove the row.
         */
        protected ImageButton mRemove;

        public ListItemViewHolder(View view) {
            mRemove = (ImageButton)view.findViewById(R.id.remove);
            mText = (EditText)view.findViewById(R.id.text);
            mEditDetail = (Button)view.findViewById(R.id.type);

            mText.addTextChangedListener(mTextWatcher);
            mText.setOnFocusChangeListener(mOnFocusChangeListener);
        }

        public void focusText() {
            mText.requestFocus();
        }

        public void initViews(RemoveListener removeListener,
                TextFocusedListener textFocusedListener,
                OnEditDetailClickListener editDetailDialogueCallback) {
            mRemoveListener = removeListener;
            mTextFocusedListener = textFocusedListener;
            mEditDetailDialogueCallback = editDetailDialogueCallback;

            mEditDetail.setOnClickListener(mOnEditClickListener);
            mRemove.setOnClickListener(mRemoveClickListener);
        }

        public void setViewValues(Subject detail, int position) {
            mDetail = detail;
            mPosition = position;
            mText.setText(detail.text);
            mEditDetail.setText(detail.getTypeLabelResId());
        }

        public interface OnEditDetailClickListener {
            void onEditDetailClick(int position);
        }

        public interface TextFocusedListener {
            void onTextFocused(int position);
        }

        static class EnsureFocus implements Runnable {
            private int mPosition;

            private TextFocusedListener mTextFocusedListener;

            @Override
            public void run() {
                mTextFocusedListener.onTextFocused(mPosition);
            }

            public void setPosition(int position) {
                mPosition = position;
            }

            public void setTextFocusedListener(TextFocusedListener listener) {
                mTextFocusedListener = listener;
            }
        }
    }

}
