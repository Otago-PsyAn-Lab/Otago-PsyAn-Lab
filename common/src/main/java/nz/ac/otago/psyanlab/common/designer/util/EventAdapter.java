/*
 Copyright (C) 2012, 2013, 2014 University of Otago, Tonic Artos <tonic.artos@gmail.com>

 Otago PsyAn Lab is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program. If not, see <http://www.gnu.org/licenses/>.

 In accordance with Section 7(b) of the GNU General Public License version 3,
 all legal notices and author attributions must be preserved.
 */

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
