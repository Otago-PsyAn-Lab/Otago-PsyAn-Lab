
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




import java.lang.reflect.Type;

public abstract class Operand {
    @Expose
    public String name = "Unset";

    @Expose
    public int tag;

    @Expose
    public int type;

    public Operand() {
    }

    public Operand(Operand operand) {
        name = operand.name;
        type = operand.type;
        tag = operand.tag;
    }

    /**
     * Attempt to refine the type of the operand.
     * 
     * @param type Types this operand is wished to be one of.
     * @return True if restriction successful.
     */
    public boolean attemptRestrictType(int type) {
        int intersection = this.type & type;
        if (intersection != 0) {
            this.type = intersection;
            return true;
        }
        return false;
    }

    public String getName() {
        return name;
    }

    public int getTag() {
        return tag;
    }

    public int getType() {
        return type;
    }

    public void setTag(int tag) {
        this.tag = tag;
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
