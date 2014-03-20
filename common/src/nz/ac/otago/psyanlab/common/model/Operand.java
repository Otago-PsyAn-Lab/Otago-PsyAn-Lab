
package nz.ac.otago.psyanlab.common.model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.annotations.Expose;

import nz.ac.otago.psyanlab.common.R;

import android.content.Context;

import java.lang.reflect.Type;

public abstract class Operand {
    public static final int TYPE_BOOLEAN = 0x01;

    public static final int TYPE_FLOAT = 0x02;

    public static final int TYPE_IMAGE = 0x10;

    public static final int TYPE_INTEGER = 0x04;

    public static final int TYPE_SOUND = 0x40;

    public static final int TYPE_STRING = 0x08;

    public static final int TYPE_VIDEO = 0x20;

    public static final int TYPE_NUMBER = TYPE_FLOAT | TYPE_INTEGER;

    public static final int TYPE_NON_ASSETS = TYPE_BOOLEAN | TYPE_NUMBER | TYPE_STRING;

    public static final int TYPE_ANY = TYPE_NON_ASSETS | TYPE_IMAGE | TYPE_SOUND | TYPE_VIDEO;

    public static CharSequence getTypeString(Context context, int type) {
        if (type == TYPE_INTEGER) {
            return context.getString(R.string.operand_type_integer);
        } else if (type == TYPE_FLOAT) {
            return context.getString(R.string.operand_type_float);
        } else if (type == TYPE_STRING) {
            return context.getString(R.string.operand_type_string);
        } else if (type == TYPE_BOOLEAN) {
            return context.getString(R.string.operand_type_boolean);
        } else if (type == TYPE_IMAGE) {
            return context.getString(R.string.operand_type_image);
        } else if (type == TYPE_SOUND) {
            return context.getString(R.string.operand_type_sound);
        } else if (type == TYPE_VIDEO) {
            return context.getString(R.string.operand_type_video);
        } else if (type == TYPE_NUMBER) {
            return context.getString(R.string.operand_type_number);
        } else if (type == TYPE_NON_ASSETS) {
            return context.getString(R.string.operand_type_any_except_asset);
        } else {
            return context.getString(R.string.operand_type_unknown);
        }
    }

    @Expose
    public String name = "Unset";

    @Expose
    public int type;

    /**
     * Attempt to refine the type of the operand.
     * 
     * @param type Types this operand is wished to be one of.
     * @return True if restriction successful.
     */
    public boolean attemptRestrictType(int type) {
        return false;
    }

    public String getName() {
        return name;
    }

    public int getType() {
        return type;
    }

    public class OperandGsonAdapter implements JsonSerializer<Operand>, JsonDeserializer<Operand> {

        @Override
        public Operand deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext jctx)
                throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();
            String type = jsonObject.get("type").getAsString();
            JsonElement element = jsonObject.get("properties");

            try {
                return jctx.deserialize(element,
                        Class.forName("nz.ac.otago.psyanlab.common.model.operand." + type));

            } catch (ClassNotFoundException e) {
                throw new JsonParseException("Unknown element type: " + type, e);
            }

        }

        @Override
        public JsonElement serialize(Operand src, Type typeOfSrc, JsonSerializationContext jctx) {
            JsonObject r = new JsonObject();
            r.add("type", new JsonPrimitive(src.getClass().getSimpleName()));
            r.add("properties", jctx.serialize(src, src.getClass()));
            return r;
        }
    }
}
