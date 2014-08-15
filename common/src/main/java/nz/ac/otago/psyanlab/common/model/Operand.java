
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
