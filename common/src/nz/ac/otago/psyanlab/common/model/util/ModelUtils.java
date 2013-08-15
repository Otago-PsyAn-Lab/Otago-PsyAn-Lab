
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

public class ModelUtils {
    private static Gson mGson;

    public static Gson getDataReaderWriter() {
        if (mGson == null) {
            GsonBuilder gson = new GsonBuilder();
            gson.registerTypeAdapter(Operand.class, new AbsModelGsonAdapter<Operand>(
                    AbsModelGsonAdapter.NS_MODEL_ASSET));
            gson.registerTypeAdapter(Generator.class, new AbsModelGsonAdapter<Generator>(
                    AbsModelGsonAdapter.NS_MODEL_GENERATOR));
            gson.registerTypeAdapter(Subject.class, new AbsModelGsonAdapter<Subject>(
                    AbsModelGsonAdapter.NS_MODEL_OPERAND));
            gson.registerTypeAdapter(Prop.class, new AbsModelGsonAdapter<Prop>(
                    AbsModelGsonAdapter.NS_MODEL_PROP));
            gson.registerTypeAdapter(Asset.class, new AbsModelGsonAdapter<Asset>(
                    AbsModelGsonAdapter.NS_MODEL_SUBJECT));

            mGson = gson.create();
        }

        return mGson;
    }

    public static Experiment readFile(File paleFile) throws FileNotFoundException {
        return ModelUtils.getDataReaderWriter().fromJson(new JsonReader(new FileReader(paleFile)),
                Experiment.class);
    }

    public static Experiment readDefinition(String paleDefinition) {
        return ModelUtils.getDataReaderWriter().fromJson(
                new JsonReader(new StringReader(paleDefinition)), Experiment.class);
    }
}