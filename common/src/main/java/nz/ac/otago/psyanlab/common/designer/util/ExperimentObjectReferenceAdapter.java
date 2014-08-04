
package nz.ac.otago.psyanlab.common.designer.util;

import nz.ac.otago.psyanlab.common.R;
import nz.ac.otago.psyanlab.common.model.ExperimentObject;
import nz.ac.otago.psyanlab.common.util.TextViewHolder;

import android.content.Context;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ExperimentObjectReferenceAdapter extends BaseAdapter implements ListAdapter {
    private Context mContext;

    private LayoutInflater mInflater;

    private List<Pair<ExperimentObject, Long>> mObjects = new ArrayList<Pair<ExperimentObject, Long>>();

    private final ViewBinder sDefaultBinder = new ViewBinder() {
        @Override
        public View bind(LayoutInflater inflater, Pair<ExperimentObject, Long> item, int position,
                View convertView, ViewGroup parent) {
            TextViewHolder holder;

            if (convertView == null) {
                convertView = inflater.inflate(R.layout.list_item_experiment_object, parent, false);
                holder = new TextViewHolder(12);
                holder.textViews[0] = (TextView)convertView.findViewById(android.R.id.text1);
                holder.textViews[1] = (TextView)convertView.findViewById(android.R.id.text2);
                convertView.setTag(holder);
            } else {
                holder = (TextViewHolder)convertView.getTag();
            }

            holder.textViews[0].setText(item.first.getExperimentObjectName(mContext));
            holder.textViews[1].setText(ExperimentObject.kindToResId(item.first.kind()));

            return convertView;
        }

        @Override
        public int getItemViewType(int position, ExperimentObject item) {
            return 0;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }
    };

    private ViewBinder mViewBinder = sDefaultBinder;

    public ExperimentObjectReferenceAdapter(Context context, List<Pair<ExperimentObject, Long>> objects) {
        mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContext = context;
        mObjects = objects;
    }

    public ExperimentObjectReferenceAdapter(Context context, List<Pair<ExperimentObject, Long>> objects,
            ViewBinder viewBinder) {
        this(context, objects);
        mViewBinder = viewBinder;
    }

    @Override
    public int getCount() {
        return mObjects.size();
    }

    @Override
    public ExperimentObject getItem(int position) {
        return mObjects.get(position).first;
    }

    @Override
    public long getItemId(int position) {
        return mObjects.get(position).second;
    }

    @Override
    public int getItemViewType(int position) {
        return mViewBinder.getItemViewType(position, getItem(position));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return mViewBinder.bind(mInflater, mObjects.get(position), position, convertView, parent);
    }

    @Override
    public int getViewTypeCount() {
        return mViewBinder.getViewTypeCount();
    }

    public void setViewBinder(ViewBinder viewBinder) {
        if (viewBinder == null) {
            mViewBinder = sDefaultBinder;
            return;
        }

        mViewBinder = viewBinder;
    }

    public interface ViewBinder {
        /**
         * Bind data to a view.
         * 
         * @param inflater View inflater.
         * @param object The object data to bind.
         * @param convertView Previous view for this type, can be null.
         * @param parent Parent to which the returned view will be attached.
         * @return View.
         */
        public View bind(LayoutInflater inflater, Pair<ExperimentObject, Long> item, int position,
                View convertView, ViewGroup parent);

        /**
         * Get the view type for the current position.
         * 
         * @param position
         * @param item
         * @return
         */
        public int getItemViewType(int position, ExperimentObject item);

        /**
         * Get the total number of different view types this binder returns.
         * 
         * @return Count of maximum view types returned.
         */
        public int getViewTypeCount();
    }
}
