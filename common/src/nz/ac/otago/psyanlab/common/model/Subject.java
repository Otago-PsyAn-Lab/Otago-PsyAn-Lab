
package nz.ac.otago.psyanlab.common.model;

import com.google.gson.annotations.Expose;

import nz.ac.otago.psyanlab.common.R;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

public abstract class Subject {
    @Expose
    public String name;

    @Expose
    public boolean required;

    @Expose
    public String text;

    protected int mTypeId = 0x00;

    protected int mTypeLabelResId;

    private CharSequence getLabelText(Context context) {
        if (required) {
            return text + context.getResources().getString(R.string.symbol_required);
        }
        return text;
    }

    public int getTypeLabelResId() {
        return mTypeLabelResId;
    }

    /**
     * Gets a view for the label of this subject detail UI.
     * 
     * @return Label view component.
     */
    public View getLabelView(Context context) {
        TextView v = new TextView(context);
        v.setText(getLabelText(context));
        return v;
    }

    public int getTypeId() {
        return mTypeId;
    }
}
