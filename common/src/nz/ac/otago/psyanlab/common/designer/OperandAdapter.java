
package nz.ac.otago.psyanlab.common.designer;

import nz.ac.otago.psyanlab.common.designer.ProgramComponentAdapter.ViewBinder;
import nz.ac.otago.psyanlab.common.model.Operand;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.List;

public class OperandAdapter extends ArrayAdapter<Operand> {
    private int mBackgroundResource = -1;

    private ViewBinder<Operand> mViewBinder;

    public OperandAdapter(Context context, int resource, List<Operand> objects,
            ViewBinder<Operand> viewBinder) {
        super(context, resource, objects);
        mViewBinder = viewBinder;
    }

    public void fixItemBackground(int resId) {
        mBackgroundResource = resId;
        notifyDataSetChanged();
    }

    @Override
    public View getView(int pos, View convertView, ViewGroup parent) {
        View view = mViewBinder.bind(getItem(pos), convertView, parent);
        if (mBackgroundResource != -1) {
            view.setBackgroundResource(mBackgroundResource);
        }
        return view;
    }
}
