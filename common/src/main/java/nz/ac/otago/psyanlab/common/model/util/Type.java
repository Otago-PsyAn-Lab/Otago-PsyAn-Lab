
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

package nz.ac.otago.psyanlab.common.model.util;

import nz.ac.otago.psyanlab.common.R;
import nz.ac.otago.psyanlab.common.util.TextViewHolder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class Type {

    public static final int TYPE_BOOLEAN = 0x01;

    public static final int TYPE_FLOAT = 0x02;

    public static final int TYPE_IMAGE = 0x10;

    public static final int TYPE_INTEGER = 0x04;

    public static final int TYPE_SOUND = 0x40;

    public static final int TYPE_STRING = 0x08;

    public static final int TYPE_VIDEO = 0x20;

    public static final int TYPE_VOID = 0;

    public static final int TYPE_NUMBER = TYPE_FLOAT | TYPE_INTEGER;

    public static final int TYPE_NON_ASSETS = TYPE_BOOLEAN | TYPE_NUMBER | TYPE_STRING;

    public static final int TYPE_ANY = TYPE_NON_ASSETS | TYPE_IMAGE | TYPE_SOUND | TYPE_VIDEO;

    public static CharSequence getTypeString(Context context, int type) {
        if (type == Type.TYPE_INTEGER) {
            return context.getString(R.string.operand_type_integer);
        } else if (type == Type.TYPE_FLOAT) {
            return context.getString(R.string.operand_type_float);
        } else if (type == Type.TYPE_STRING) {
            return context.getString(R.string.operand_type_string);
        } else if (type == Type.TYPE_BOOLEAN) {
            return context.getString(R.string.operand_type_boolean);
        } else if (type == Type.TYPE_IMAGE) {
            return context.getString(R.string.operand_type_image);
        } else if (type == Type.TYPE_SOUND) {
            return context.getString(R.string.operand_type_sound);
        } else if (type == Type.TYPE_VIDEO) {
            return context.getString(R.string.operand_type_video);
        } else if (type == Type.TYPE_NUMBER) {
            return context.getString(R.string.operand_type_number);
        } else if (type == Type.TYPE_NON_ASSETS) {
            return context.getString(R.string.operand_type_any_except_asset);
        } else {
            return context.getString(R.string.operand_type_unknown);
        }
    }

    public static CharSequence getTypeString(int type) {
        if (type == Type.TYPE_INTEGER) {
            return "integer";
        } else if (type == Type.TYPE_FLOAT) {
            return "float";
        } else if (type == Type.TYPE_STRING) {
            return "string";
        } else if (type == Type.TYPE_BOOLEAN) {
            return "boolean";
        } else if (type == Type.TYPE_IMAGE) {
            return "image";
        } else if (type == Type.TYPE_SOUND) {
            return "sound";
        } else if (type == Type.TYPE_VIDEO) {
            return "video";
        } else if (type == Type.TYPE_NUMBER) {
            return "number";
        } else if (type == Type.TYPE_NON_ASSETS) {
            return "non asset";
        } else {
            return "unknown";
        }
    }

    public static List<String> typeToStringArray(Context context, int type) {
        ArrayList<String> types = new ArrayList<String>();
        if ((type & Type.TYPE_BOOLEAN) != 0) {
            types.add((String)getTypeString(context, Type.TYPE_BOOLEAN));
        }
        if ((type & Type.TYPE_FLOAT) != 0) {
            types.add((String)getTypeString(context, Type.TYPE_FLOAT));
        }
        if ((type & Type.TYPE_IMAGE) != 0) {
            types.add((String)getTypeString(context, Type.TYPE_IMAGE));
        }
        if ((type & Type.TYPE_INTEGER) != 0) {
            types.add((String)getTypeString(context, Type.TYPE_INTEGER));
        }
        if ((type & Type.TYPE_SOUND) != 0) {
            types.add((String)getTypeString(context, Type.TYPE_SOUND));
        }
        if ((type & Type.TYPE_STRING) != 0) {
            types.add((String)getTypeString(context, Type.TYPE_STRING));
        }
        if ((type & Type.TYPE_VIDEO) != 0) {
            types.add((String)getTypeString(context, Type.TYPE_VIDEO));
        }

        return types;
    }

    public static class TypeAdapter extends BaseAdapter implements ListAdapter, SpinnerAdapter {
        final static private int[] sTypes = new int[] {
                Type.TYPE_BOOLEAN, Type.TYPE_INTEGER, Type.TYPE_NUMBER, Type.TYPE_FLOAT,
                Type.TYPE_STRING
        };

        public static int positionOf(int type) {
            for (int i = 0; i < sTypes.length; i++) {
                if (sTypes[i] == type) {
                    return i;
                }
            }
            throw new RuntimeException("Unknown or unsupported field type " + type
                    + " for data channels.");
        }

        private Context mContext;

        private LayoutInflater mInflater;

        public TypeAdapter(Context context) {
            mContext = context;
            mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return sTypes.length;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            TextViewHolder holder;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.list_item, parent, false);
                holder = new TextViewHolder(1);
                holder.textViews[0] = (TextView)convertView.findViewById(android.R.id.text1);
                convertView.setTag(holder);
            } else {
                holder = (TextViewHolder)convertView.getTag();
            }

            CharSequence typeString = getTypeString(mContext, sTypes[position]);

            holder.textViews[0].setText(typeString);

            return convertView;
        }

        @Override
        public Integer getItem(int position) {
            return sTypes[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextViewHolder holder;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.list_item, parent, false);
                holder = new TextViewHolder(1);
                holder.textViews[0] = (TextView)convertView.findViewById(android.R.id.text1);
                convertView.setTag(holder);
            } else {
                holder = (TextViewHolder)convertView.getTag();
            }

            holder.textViews[0].setText(getTypeString(mContext, sTypes[position]));

            return convertView;
        }
    }
}
