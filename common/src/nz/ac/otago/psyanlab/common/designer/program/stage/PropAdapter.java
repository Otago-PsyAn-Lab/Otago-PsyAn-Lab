
package nz.ac.otago.psyanlab.common.designer.program.stage;

import nz.ac.otago.psyanlab.common.R;
import nz.ac.otago.psyanlab.common.model.Prop;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class PropAdapter extends BaseAdapter implements StageView.StageAdapter {
    private final Activity mActivity;

    private ArrayList<Prop> mProps;

    public PropAdapter(Activity activity, ArrayList<Prop> props) {
        mActivity = activity;
        mProps = props;
    }

    public void setProps(ArrayList<Prop> props) {
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

        Prop prop = mProps.get(position);
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
