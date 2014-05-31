
package nz.ac.otago.psyanlab.common.model.util;

import nz.ac.otago.psyanlab.common.R;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

public class Type {
    public static final int TYPE_BOOLEAN = 0x01;

    public static final int TYPE_FLOAT = 0x02;

    public static final int TYPE_IMAGE = 0x10;

    public static final int TYPE_INTEGER = 0x04;

    public static final int TYPE_NUMBER = TYPE_FLOAT | TYPE_INTEGER;

    public static final int TYPE_SOUND = 0x40;

    public static final int TYPE_STRING = 0x08;

    public static final int TYPE_VIDEO = 0x20;

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

}
