
package nz.ac.otago.psyanlab.common.model;

import com.google.gson.annotations.Expose;

import nz.ac.otago.psyanlab.common.R;
import nz.ac.otago.psyanlab.common.model.Asset.AssetFactory;
import nz.ac.otago.psyanlab.common.model.channel.Field;
import nz.ac.otago.psyanlab.common.model.util.NameResolverFactory;

import android.content.Context;

import java.util.ArrayList;

/**
 * A DataChannel is the description of a record type the user wants to collect
 * from the execution of the experiment. DataChannels are analogous to a table
 * type data structure.
 */
public class DataChannel extends ExperimentObject {
    private static AssetFactory mFactory = new AssetFactory();

    protected static final int METHOD_WRITE = 0x01;

    public static AssetFactory getFactory() {
        return mFactory;
    }

    public static NameResolverFactory getMethodNameFactory() {
        return new MethodNameFactory();
    }

    /**
     * User notes or description of the data channel.
     */
    @Expose
    public String description;

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
    public int kind() {
        return ExperimentObject.KIND_DATA_CHANNEL;
    }

    public static class Comparator implements java.util.Comparator<DataChannel> {
        @Override
        public int compare(DataChannel lhs, DataChannel rhs) {
            if (lhs.name == null || rhs.name == null) {
                return 0;
            }

            return lhs.name.compareToIgnoreCase(rhs.name);
        }
    }

    protected static class MethodNameFactory implements NameResolverFactory {

        @Override
        public int getResId(int lookup) {
            switch (lookup) {
                case METHOD_WRITE:
                    return R.string.method_data_channel_write;

                default:
                    return R.string.method_missing_string;
            }
        }
    }
}
