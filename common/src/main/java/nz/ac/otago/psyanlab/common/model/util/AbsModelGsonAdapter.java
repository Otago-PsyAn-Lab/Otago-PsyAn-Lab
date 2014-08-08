
package nz.ac.otago.psyanlab.common.model.util;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

public class AbsModelGsonAdapter<T> implements JsonSerializer<T>, JsonDeserializer<T> {
    public static final String NS_MODEL = "nz.ac.otago.psyanlab.common.model.";

    public static final String NS_MODEL_PROP = "nz.ac.otago.psyanlab.common.model.prop.";

    public static final String NS_MODEL_OPERAND = "nz.ac.otago.psyanlab.common.model.operand.";

    public static final String NS_MODEL_ASSET = "nz.ac.otago.psyanlab.common.model.asset.";

    public static final String NS_MODEL_SUBJECT = "nz.ac.otago.psyanlab.common.model.subject.";

    public static final String NS_MODEL_GENERATOR = "nz.ac.otago.psyanlab.common.model.generator.";

    public static final String NS_MODEL_VARIABLE = "nz.ac.otago.psyanlab.common.model.variable.";

    private String mNamespace;

    public AbsModelGsonAdapter(String namespace) {
        mNamespace = namespace;
    }

    @Override
    public T deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext jctx)
            throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        String type = jsonObject.get("type").getAsString();
        JsonElement element = jsonObject.get("properties");

        try {
            return jctx.deserialize(element, Class.forName(mNamespace + type));

        } catch (ClassNotFoundException e) {
            throw new JsonParseException("Unknown element type: " + type, e);
        }

    }

    @Override
    public JsonElement serialize(T src, Type typeOfSrc, JsonSerializationContext jctx) {
        JsonObject r = new JsonObject();
        r.add("type", new JsonPrimitive(src.getClass().getSimpleName()));
        r.add("properties", jctx.serialize(src, src.getClass()));
        return r;
    }
}
