/*
 Copyright (C) 2012, 2013, 2014 University of Otago, Tonic Artos <tonic.artos@gmail.com>

 Otago PsyAn Lab is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program. If not, see <http://www.gnu.org/licenses/>.

 In accordance with Section 7(b) of the GNU General Public License version 3,
 all legal notices and author attributions must be preserved.
 */

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

    public static final String NS_MODEL_TIMER = "nz.ac.otago.psyanlab.common.model.timer.";

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
