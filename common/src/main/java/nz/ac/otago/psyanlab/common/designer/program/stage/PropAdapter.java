
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

package nz.ac.otago.psyanlab.common.designer.program.stage;

import nz.ac.otago.psyanlab.common.R;
import nz.ac.otago.psyanlab.common.designer.util.PropIdPair;
import nz.ac.otago.psyanlab.common.model.Prop;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class PropAdapter extends BaseAdapter implements StageView.StageAdapter {
    private final Activity mActivity;

    private ArrayList<PropIdPair> mProps;

    public PropAdapter(Activity activity, ArrayList<PropIdPair> props) {
        mActivity = activity;
        mProps = props;
    }

    public void setProps(ArrayList<PropIdPair> props) {
        mProps = props;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mProps.size();
    }

    @Override
    public Object getItem(int position) {
        return mProps.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mActivity.getLayoutInflater().inflate(R.layout.stage_prop, parent, false);
        }

        Prop prop = mProps.get(position).getProp();
        ((TextView)convertView).setText(prop.name);
        convertView.setLayoutParams(new StageView.LayoutParams(prop.xPos, prop.yPos, prop.width,
                prop.height));

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }
}
