
package nz.ac.otago.psyanlab.common.model.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;

import nz.ac.otago.psyanlab.common.model.Asset;
import nz.ac.otago.psyanlab.common.model.Experiment;
import nz.ac.otago.psyanlab.common.model.Generator;
import nz.ac.otago.psyanlab.common.model.Operand;
import nz.ac.otago.psyanlab.common.model.Prop;
import nz.ac.otago.psyanlab.common.model.Subject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ModelUtils {
    private static Gson mGson;

    public static Gson getDataReaderWriter() {
        if (mGson == null) {
            GsonBuilder gson = new GsonBuilder();

            // Type loopArrayType = new TypeToken<LongSparseArray<Loop>>() {
            // }.getType();
            // gson.registerTypeAdapter(loopArrayType,
            // new LongSparseArrayGsonAdapter<Loop>(Loop.class));

            gson.registerTypeAdapter(Operand.class, new AbsModelGsonAdapter<Operand>(
                    AbsModelGsonAdapter.NS_MODEL_OPERAND));
            gson.registerTypeAdapter(Generator.class, new AbsModelGsonAdapter<Generator>(
                    AbsModelGsonAdapter.NS_MODEL_GENERATOR));
            gson.registerTypeAdapter(Subject.class, new AbsModelGsonAdapter<Subject>(
                    AbsModelGsonAdapter.NS_MODEL_SUBJECT));
            gson.registerTypeAdapter(Prop.class, new AbsModelGsonAdapter<Prop>(
                    AbsModelGsonAdapter.NS_MODEL_PROP));
            gson.registerTypeAdapter(Asset.class, new AbsModelGsonAdapter<Asset>(
                    AbsModelGsonAdapter.NS_MODEL_ASSET));

            mGson = gson.create();
        }

        return mGson;
    }

    public static NameResolverFactory getEventNameFactory(final Class<?> clazz) {
        Method m;
        try {
            m = clazz.getMethod("getEventNameFactory", (Class<?>[])null);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Error getting event name factory for " + clazz, e);
        }

        final NameResolverFactory nameFactory;
        try {
            nameFactory = (NameResolverFactory)m.invoke(null, (Object[])null);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Error getting event name factory for " + clazz, e);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Error getting event name factory for " + clazz, e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException("Error getting event name factory for " + clazz, e);
        }
        return nameFactory;
    }

    public static NameResolverFactory getMethodNameFactory(final Class<?> clazz) {
        Method m;
        try {
            m = clazz.getMethod("getMethodNameFactory", (Class<?>[])null);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Error getting method name factory for " + clazz, e);
        }

        final NameResolverFactory nameFactory;
        try {
            nameFactory = (NameResolverFactory)m.invoke(null, (Object[])null);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Error getting method name factory for " + clazz, e);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Error getting method name factory for " + clazz, e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException("Error getting method name factory for " + clazz, e);
        }
        return nameFactory;
    }

    public static NameResolverFactory getParameterNameFactory(final Class<?> clazz) {
        Method m;
        try {
            m = clazz.getMethod("getParameterNameFactory", (Class<?>[])null);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Error getting parameter name factory for " + clazz, e);
        }

        final NameResolverFactory nameFactory;
        try {
            nameFactory = (NameResolverFactory)m.invoke(null, (Object[])null);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Error getting parameter name factory for " + clazz, e);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Error getting parameter name factory for " + clazz, e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException("Error getting parameter name factory for " + clazz, e);
        }
        return nameFactory;
    }

    public static Experiment readDefinition(String paleDefinition) {
        return ModelUtils.getDataReaderWriter().fromJson(
                new JsonReader(new StringReader(paleDefinition)), Experiment.class);
    }

    public static Experiment readFile(File paleFile) throws FileNotFoundException {
        return ModelUtils.getDataReaderWriter().fromJson(new JsonReader(new FileReader(paleFile)),
                Experiment.class);
    }
}
