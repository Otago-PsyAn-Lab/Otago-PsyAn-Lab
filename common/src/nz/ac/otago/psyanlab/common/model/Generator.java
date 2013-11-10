
package nz.ac.otago.psyanlab.common.model;

import com.google.gson.annotations.Expose;

import nz.ac.otago.psyanlab.common.R;

import android.content.Context;

public abstract class Generator implements ExperimentControl {
    @Expose
    public int end;

    @Expose
    public String name;

    @Expose
    public int start;

    public Generator() {
        name = "New Generator";
    }

    @Override
    public String getClassName(Context context) {
        return context.getString(R.string.format_generator_class_name, name);
    }
}
