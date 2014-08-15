
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

import android.content.Context;
import android.support.v4.util.LongSparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;
import java.util.SortedSet;
import java.util.TreeSet;

public class LongSparseArrayAdapter<T> extends BaseAdapter implements ListAdapter {
    private LayoutInflater mInflater;

    private LongSparseArray<T> mItems;

    private int mLayoutResId;

    private Long[] mSortedKeys;

    public LongSparseArrayAdapter(Context context, int layoutResId, LongSparseArray<T> items) {
        mLayoutResId = layoutResId;
        mItems = items;
        mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mSortedKeys = sortKeys(context, items);
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mItems.get(mSortedKeys[position]);
    }

    @Override
    public long getItemId(int position) {
        return mSortedKeys[position];
    }

    @Override
    @SuppressWarnings("unchecked")
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = mInflater.inflate(mLayoutResId, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }

        holder.text.setText(mItems.get(mSortedKeys[position]).toString());

        return convertView;
    }

    private Long[] sortKeys(Context context, LongSparseArray<T> items) {
        Locale locale = context.getResources().getConfiguration().locale;
        final Collator collator = Collator.getInstance(locale);
        collator.setStrength(Collator.SECONDARY);

        SortedSet<Long> sortedKeys = new TreeSet<Long>(new Comparator<Long>() {
            @Override
            public int compare(Long lhs, Long rhs) {
                return collator.compare(mItems.get(lhs).toString(), mItems.get(rhs).toString());
            }
        });

        for (int i = 0; i < items.size(); i++) {
            sortedKeys.add(items.keyAt(i));
        }

        return sortedKeys.toArray(new Long[sortedKeys.size()]);
    }

    class ViewHolder {
        TextView text;

        public ViewHolder(View view) {
            text = (TextView)view.findViewById(android.R.id.text1);
        }
    }
}
