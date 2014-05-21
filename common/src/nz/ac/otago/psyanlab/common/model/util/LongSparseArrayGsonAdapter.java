
package nz.ac.otago.psyanlab.common.model.util;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import android.support.v4.util.LongSparseArray;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * Coerces long sparse array items to specified type. However, is not compatible
 * with abstract types.
 * 
 * @param <T> Type to coerce items to.
 */
public class LongSparseArrayGsonAdapter<T> extends TypeAdapter<LongSparseArray<T>> {
    private final Gson mGson = new Gson();

    private final Class<T> mTClazz;

    private final Type mTypeOfLongSparseArrayOfT = new TypeToken<LongSparseArray<T>>() {
    }.getType();

    private final Type mTypeOfLongSparseArrayOfObject = new TypeToken<LongSparseArray<Object>>() {
    }.getType();

    public LongSparseArrayGsonAdapter(Class<T> tClazz) {
        mTClazz = tClazz;
    }

    @Override
    public void write(JsonWriter out, LongSparseArray<T> value) throws IOException {
        if (value == null) {
            out.nullValue();
            return;
        }
        mGson.toJson(mGson.toJsonTree(value, mTypeOfLongSparseArrayOfT), out);
    }

    @Override
    public LongSparseArray<T> read(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return null;
        }
        LongSparseArray<Object> temp = mGson.fromJson(in, mTypeOfLongSparseArrayOfObject);
        LongSparseArray<T> result = new LongSparseArray<T>(temp.size());
        long key;
        JsonElement tElement;
        for (int i = 0; i < temp.size(); i++) {
            key = temp.keyAt(i);
            tElement = mGson.toJsonTree(temp.get(key), new TypeToken<T>() {
            }.getType());
            result.put(key, mGson.fromJson(tElement, mTClazz));
        }
        return result;
    }
}
