package nz.ac.otago.psyanlab.common.model;

import com.google.gson.annotations.Expose;

import nz.ac.otago.psyanlab.common.R;
import nz.ac.otago.psyanlab.common.model.chansrc.Field;
import nz.ac.otago.psyanlab.common.model.util.NameResolverFactory;
import nz.ac.otago.psyanlab.common.model.util.Type;

import android.app.Activity;
import android.content.Context;

import java.util.ArrayList;
import java.util.SortedSet;

/**
 * A DataChannel is the note of a record type the user wants to collect from the execution of
 * the experiment. DataChannels are analogous to a table type data structure.
 */
public class DataChannel extends ExperimentObject implements Comparable<DataChannel> {
    protected static final int METHOD_WRITE = 0x01;

    /**
     * Columns in the table that the DataChannel represents. Index is important.
     */
    @Expose
    public ArrayList<Field> fields;

    /**
     * DataChannel name.
     */
    @Expose
    public String name;

    public DataChannel() {
        fields = new ArrayList<Field>();
    }

    @Override
    public String getExperimentObjectName(Context context) {
        return name;
    }

    @Override
    public NameResolverFactory getMethodNameFactory() {
        return new MethodNameFactory();
    }

    @Override
    public NameResolverFactory getParameterNameFactory() {
        throw new RuntimeException("Unsupported method");
    }

    @Override
    public ParameterData[] getParameters(Activity activity, int methodId) {
        ParameterData[] parameters = new ParameterData[fields.size()];
        for (int i = 0; i < fields.size(); i++) {
            Field field = fields.get(i);
            ParameterData data = new ParameterData();
            data.id = i;
            data.type = field.type;
            data.name = field.name;
            parameters[i] = data;
        }
        return parameters;
    }

    @Override
    public int kind() {
        return ExperimentObject.KIND_CHANNEL;
    }

    @Override
    public void loadInMatchingMethods(Context context, int returnType, SortedSet<MethodData> out) {
        MethodData methodData = new MethodData();
        methodData.id = METHOD_WRITE;
        methodData.name = context.getString(R.string.method_data_channel_write);
        methodData.returnType = Type.TYPE_VOID;
        out.add(methodData);
    }

    @Override
    public boolean satisfiesFilter(int filter) {
        return filter == HAS_SETTERS;
    }

    @Override
    public int compareTo(DataChannel another) {
        if (name == null || another.name == null) {
            return 0;
        }

        return name.compareToIgnoreCase(another.name);
    }

    protected static class MethodNameFactory implements NameResolverFactory {
        @Override
        public String getName(Context context, int lookup) {
            switch (lookup) {
                case METHOD_WRITE:
                    return context.getString(R.string.method_data_channel_write);

                default:
                    return context.getString(R.string.method_missing_string);
            }
        }
    }
}
