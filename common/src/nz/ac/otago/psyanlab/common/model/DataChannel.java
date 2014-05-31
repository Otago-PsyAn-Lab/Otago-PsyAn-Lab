
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
public class DataChannel implements ExperimentObject {
    private static AssetFactory mFactory = new AssetFactory();

    public static AssetFactory getFactory() {
        return mFactory;
    }

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

    /**
     * User notes or description of the data channel.
     */
    @Expose
    public String description;

    @Override
    public String getExperimentObjectName(Context context) {
        return name;
    }

    public DataChannel() {
        fields = new ArrayList<Field>();
    }

    @Override
    public int kind() {
        return ExperimentObjectReference.KIND_DATA_CHANNEL;
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
                default:
                    return R.string.method_missing_string;
            }
        }
    }
}
