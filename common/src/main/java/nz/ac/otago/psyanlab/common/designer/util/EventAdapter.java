package nz.ac.otago.psyanlab.common.designer.util;

import nz.ac.otago.psyanlab.common.model.util.EventData;
import nz.ac.otago.psyanlab.common.model.util.NameResolverFactory;
import nz.ac.otago.psyanlab.common.util.TextViewHolder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import java.util.SortedSet;

public class EventAdapter extends BaseAdapter implements SpinnerAdapter, ListAdapter {
    private Context mContext;

    private EventData[] mEvents;

    private LayoutInflater mInflater;

    private NameResolverFactory mNameFactory;

    public EventAdapter(Context context, SortedSet<EventData> events,
                        NameResolverFactory nameFactory) {
        mContext = context;
        mNameFactory = nameFactory;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        mEvents = new EventData[events.size()];
        int i = 0;
        for (EventData event : events) {
            mEvents[i] = event;
            i++;
        }
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public int getCount() {
        return mEvents.length;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        TextViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(android.R.layout.simple_list_item_1, parent, false);
            holder = new TextViewHolder(1);
            holder.textViews[0] = (TextView) convertView.findViewById(android.R.id.text1);
            convertView.setTag(holder);
        } else {
            holder = (TextViewHolder) convertView.getTag();
        }

        holder.textViews[0].setText(mNameFactory.getName(mContext, mEvents[position].id()));

        return convertView;
    }

    @Override
    public Object getItem(int position) {
        return mEvents[position];
    }

    @Override
    public long getItemId(int position) {
        return mEvents[position].id();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(android.R.layout.simple_list_item_1, parent, false);
            holder = new TextViewHolder(1);
            holder.textViews[0] = (TextView) convertView.findViewById(android.R.id.text1);
            convertView.setTag(holder);
        } else {
            holder = (TextViewHolder) convertView.getTag();
        }

        holder.textViews[0].setText(mNameFactory.getName(mContext, mEvents[position].id()));

        return convertView;
    }
}
