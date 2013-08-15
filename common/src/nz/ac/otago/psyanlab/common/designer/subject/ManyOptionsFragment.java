
package nz.ac.otago.psyanlab.common.designer.subject;

import com.mobeta.android.dslv.DragSortListView;
import com.mobeta.android.dslv.DragSortListView.DragSortListener;

import nz.ac.otago.psyanlab.common.R;
import nz.ac.otago.psyanlab.common.designer.subject.SubjectStatisticTypeDialogFragment.OptionsFragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import java.util.ArrayList;

public class ManyOptionsFragment extends Fragment implements OptionsFragment {
    private OptionsAdapter mAdapter;

    private LayoutInflater mInflater;

    private DragSortListView mList;

    private ArrayList<String> mOptions;

    @Override
    public ArrayList<String> getOptions() {
        return mAdapter.getOptions();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mInflater = inflater;
        View view = inflater.inflate(R.layout.fragment_type_many_options, container, false);
        mList = (DragSortListView)view.findViewById(R.id.options);

        mAdapter = new OptionsAdapter(inflater, mOptions);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final View foot = mInflater.inflate(R.layout.item_option_adder, mList, false);
        final ViewHolder holder = new ViewHolder(foot);
        foot.setTag(holder);
        holder.text.setOnEditorActionListener(new OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    mAdapter.addItem(v.getText().toString());
                    v.setText(null);
                    return true;
                }
                return false;
            }
        });
        mList.addFooterView(foot);
        mList.setAdapter(mAdapter);
        mList.setDivider(null);
    }

    @Override
    public void setOptions(ArrayList<String> options) {
        mOptions = options;
    }

    private static class OptionsAdapter extends BaseAdapter implements DragSortListener {
        private LayoutInflater mInflater;

        private ArrayList<String> mOptions;

        public OptionsAdapter(LayoutInflater layoutInflater, ArrayList<String> options) {
            if (options != null) {
                mOptions = options;
            } else {
                mOptions = new ArrayList<String>();
            }
            mInflater = layoutInflater;
        }

        public void addItem(String option) {
            mOptions.add(option);
            notifyDataSetChanged();
        }

        @Override
        public void drag(int from, int to) {
        }

        @Override
        public void drop(int from, int to) {
            String move = mOptions.remove(from);
            mOptions.add(to, move);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mOptions.size();
        }

        @Override
        public String getItem(int position) {
            return mOptions.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public ArrayList<String> getOptions() {
            return mOptions;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.item_option_row, parent, false);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
                holder.text.setFocusableInTouchMode(true);
                // TODO: subclass EditText to intercept backspaces and remove
                // the row on backspacing all text.
                holder.text.setOnFocusChangeListener(new OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(final View v, boolean hasFocus) {
                        if (hasFocus) {
                            v.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (!v.hasFocus()) {
                                        v.requestFocus();
                                    }
                                }
                            }, 400);
                            holder.remove.setVisibility(View.VISIBLE);
                        } else {
                            holder.remove.setVisibility(View.INVISIBLE);
                        }

                    }
                });
                holder.remove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        holder.text.clearFocus();
                        remove(holder.position);
                    }
                });
            } else {
                holder = (ViewHolder)convertView.getTag();
            }

            String option = getItem(position);
            holder.text.setText(option);
            holder.position = position;

            return convertView;
        }

        @Override
        public void remove(int which) {
            mOptions.remove(which);
            notifyDataSetChanged();
        }
    }

    private static class ViewHolder {
        public int position;

        public ImageButton remove;

        public EditText text;

        public ViewHolder(View view) {
            text = (EditText)view.findViewById(R.id.text);
            remove = (ImageButton)view.findViewById(R.id.remove);
        }
    }
}
