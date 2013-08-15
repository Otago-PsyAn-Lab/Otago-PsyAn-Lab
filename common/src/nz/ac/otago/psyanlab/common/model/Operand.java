
package nz.ac.otago.psyanlab.common.model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

public abstract class Operand {
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
