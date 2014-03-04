
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
    public static final int OPERAND_TYPE_FLOAT = 0x02;

    public static final int OPERAND_TYPE_INTEGER = 0x01;

    public static final int OPERAND_TYPE_STRING = 0x03;

    @Expose
    public String name = "Unset";

    public abstract int type();

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

    public static CharSequence getTypeString(Context context, int type) {
        switch (type) {
            case OPERAND_TYPE_INTEGER:
                return context.getString(R.string.operand_type_integer);
            case OPERAND_TYPE_FLOAT:
                return context.getString(R.string.operand_type_float);
            case OPERAND_TYPE_STRING:
                return context.getString(R.string.operand_type_string);

            default:
                return context.getString(R.string.operand_type_unknown);
        }
    }
}
