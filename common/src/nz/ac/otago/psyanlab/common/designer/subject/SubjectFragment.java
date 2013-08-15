
package nz.ac.otago.psyanlab.common.designer.subject;

import com.mobeta.android.dslv.DragSortListView.DragSortListener;

import nz.ac.otago.psyanlab.common.R;
import nz.ac.otago.psyanlab.common.designer.ExperimentDesignerActivity.LandingPageDataChangeListener;
import nz.ac.otago.psyanlab.common.model.LandingPage;
import nz.ac.otago.psyanlab.common.model.Subject;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class SubjectFragment extends Fragment implements LandingPageDataChangeListener {
    private SubjectRowAdapter mAdapter;

    private Callbacks mCallbacks;

    private LandingPage mLandingPage;

    private FragmentViewHolder mViews;

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
        return inflater.inflate(R.layout.fragment_designer_subject, container, false);
    }

    @Override
    public void onDetach() {
        mLandingPage.title = mViews.title.getText().toString();
        mLandingPage.introduction = mViews.introduction.getText().toString();

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
        mAdapter = new SubjectRowAdapter(SubjectFragment.this);
        mAdapter.setRows(mLandingPage.subjectDetails);

        mViews = new FragmentViewHolder(view);
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
        LandingPage getLandingPage();

        void addLandingPageDataChangeListener(LandingPageDataChangeListener listener);

        void storeLandingPage(LandingPage landingPage);
    }

    private class FragmentViewHolder {
        public EditText introduction;

        public ListView list;

        public EditText title;

        private ListItemViewHolder mFooterHolder;

        public FragmentViewHolder(View view) {
            title = (EditText)view.findViewById(R.id.title);
            introduction = (EditText)view.findViewById(R.id.subtitle);
            list = (ListView)view.findViewById(R.id.list);
        }

        public void initViews(ListAdapter adapter) {
            LayoutInflater inflater = getActivity().getLayoutInflater();
            final View foot = inflater.inflate(R.layout.item_subject_adder, list, false);
            mFooterHolder = new ListItemViewHolder(foot);
            foot.setTag(mFooterHolder);
            mFooterHolder.type.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showEditTypeDialogue(-1, R.string.title_set_type);
                }
            });
            list.addFooterView(foot);
            list.setAdapter(adapter);
            list.setDivider(null);
        }

        public void setViewValues(LandingPage landingPage) {
            title.setText(landingPage.title);
            introduction.setText(landingPage.introduction);
        }
    }

    private static class SubjectRowAdapter extends BaseAdapter implements DragSortListener {
        private SubjectFragment mFragment;

        private ArrayList<Subject> mRows;

        public SubjectRowAdapter(SubjectFragment fragment) {
            mRows = new ArrayList<Subject>();
            mFragment = fragment;
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
            if (convertView == null) {
                convertView = mFragment.getActivity().getLayoutInflater()
                        .inflate(R.layout.item_subject_row, parent, false);
                holder = new ListItemViewHolder(convertView);
                holder.row = convertView;
                convertView.setTag(holder);
                holder.text.setOnFocusChangeListener(new OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(final View v, boolean hasFocus) {
                        // Only show the remove icon when the row is selected
                        // (text is focused).

                        if (hasFocus) {
                            // Ask for another focus in a short time because the
                            // keyboard may steal away our focus.
                            v.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (!v.hasFocus()) {
                                        v.requestFocus();
                                    }
                                }
                            }, 200);
                            holder.remove.setVisibility(View.VISIBLE);
                        } else {
                            holder.remove.setVisibility(View.INVISIBLE);
                        }
                    }
                });
            } else {
                holder = (ListItemViewHolder)convertView.getTag();
            }

            // Set a new anon callback every time the row is recycled so we
            // always have the correct position inside the listener.
            holder.type.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mFragment.showEditTypeDialogue(position, R.string.title_set_type);
                }
            });
            holder.remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mRows.remove(position);
                    notifyDataSetChanged();
                }
            });

            // Initialise views.
            Subject detail = getItem(position);
            holder.setItem(detail);

            holder.text.setText(detail.text);
            holder.type.setText(detail.getTypeLabelResId());

            holder.position = position;

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

    protected static class TextUpdater implements TextWatcher {
        public TextUpdater() {
            // TODO Auto-generated constructor stub
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    }

    protected static class ListItemViewHolder {
        /**
         * Button to add a new row.
         */
        public ImageButton add;

        /**
         * Handle to drag-sort row by.
         */
        public ImageView handle;

        public int position = -1;

        /**
         * Button to remove the row.
         */
        public ImageButton remove;

        /**
         * Subject statistic row.
         */
        public View row;

        /**
         * Text describing the statistic being collected.
         */
        public EditText text;

        /**
         * Button to call a dialogue to set the type of subject detail this is.
         * Also holds the detail object this row is working on.
         */
        public Button type;

        private TextWatcher mTextWatcher;

        private Subject mDetail;

        public ListItemViewHolder(View view) {
            row = view;
            handle = (ImageView)view.findViewById(R.id.handle);
            remove = (ImageButton)view.findViewById(R.id.remove);
            text = (EditText)view.findViewById(R.id.text);
            type = (Button)view.findViewById(R.id.type);
            add = (ImageButton)view.findViewById(R.id.add);

            if (text != null) {
                mTextWatcher = new TextWatcher() {
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }

                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        mDetail.text = s.toString();
                    }
                };
                text.addTextChangedListener(mTextWatcher);
            }
        }

        public void setItem(Subject detail) {
            mDetail = detail;
        }
    }
}
