
package nz.ac.otago.psyanlab.common.designer.util;

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

import java.lang.reflect.Method;
import java.util.SortedSet;

public class MethodAdapter extends BaseAdapter implements SpinnerAdapter, ListAdapter {
    private Context mContext;

    private LayoutInflater mInflater;

    private MethodData[] mMethods;

    private NameResolverFactory mNameFactory;

    public MethodAdapter(Context context, SortedSet<MethodData> methods,
            NameResolverFactory nameFactory) {
        mContext = context;
        mNameFactory = nameFactory;
        mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        mMethods = new MethodData[methods.size()];
        int i = 0;
        for (MethodData method : methods) {
            mMethods[i] = method;
            i++;
        }
    }

    @Override
    public String toString() {
        String s = "";
        for (MethodData md : mMethods) {
            s += md.method + " ";
        }
        return s;
    }

    @Override
    public int getCount() {
        return mMethods.length;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        TextViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(android.R.layout.simple_list_item_1, parent, false);
            holder = new TextViewHolder(1);
            holder.textViews[0] = (TextView)convertView.findViewById(android.R.id.text1);
            convertView.setTag(holder);
        } else {
            holder = (TextViewHolder)convertView.getTag();
        }

        holder.textViews[0]
                .setText(mContext.getString(mNameFactory.getResId(mMethods[position].id)));

        return convertView;
    }

    @Override
    public MethodData getItem(int position) {
        return mMethods[position];
    }

    @Override
    public long getItemId(int position) {
        return mMethods[position].id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(android.R.layout.simple_list_item_1, parent, false);
            holder = new TextViewHolder(1);
            holder.textViews[0] = (TextView)convertView.findViewById(android.R.id.text1);
            convertView.setTag(holder);
        } else {
            holder = (TextViewHolder)convertView.getTag();
        }

        holder.textViews[0]
                .setText(mContext.getString(mNameFactory.getResId(mMethods[position].id)));

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    public static class MethodData {
        public int id;

        public Method method;
    }
}
