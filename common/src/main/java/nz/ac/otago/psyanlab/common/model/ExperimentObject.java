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

import com.google.gson.annotations.Expose;

import nz.ac.otago.psyanlab.common.R;
import nz.ac.otago.psyanlab.common.model.typestub.ImageStub;
import nz.ac.otago.psyanlab.common.model.typestub.SoundStub;
import nz.ac.otago.psyanlab.common.model.typestub.VideoStub;
import nz.ac.otago.psyanlab.common.model.util.EventData;
import nz.ac.otago.psyanlab.common.model.util.MethodId;
import nz.ac.otago.psyanlab.common.model.util.ModelUtils;
import nz.ac.otago.psyanlab.common.model.util.NameResolverFactory;
import nz.ac.otago.psyanlab.common.model.util.ParameterId;
import nz.ac.otago.psyanlab.common.model.util.Type;

import android.app.Activity;
import android.content.Context;

import java.lang.reflect.Method;
import java.util.SortedSet;

public abstract class ExperimentObject {
    /**
     * An object that emits events.
     */
    public static final int EMITS_EVENTS = -0x01;

    /**
     * An object that has methods which set a value (void return type).
     */
    public static final int HAS_SETTERS = 0x00;

    /**
     * An object which is a kind of action.
     */
    public static final int KIND_ACTION = 0x01;

    /**
     * An object which is a kind of asset.
     */
    public static final int KIND_ASSET = 0x02;

    /**
     * An object which describes how to store collected experiment records.
     */
    public static final int KIND_CHANNEL = 0x03;

    /**
     * An virtual object which is a kind of event.
     */
    public static final int KIND_EVENT = 0x04;

    /**
     * An object which is a kind of experiment.
     */
    public static final int KIND_EXPERIMENT = 0x05;

    /**
     * An object which is a kind of generator.
     */
    public static final int KIND_GENERATOR = 0x06;

    /**
     * An object which is a kind of loop.
     */
    public static final int KIND_LOOP = 0x07;

    /**
     * An object which is a king of operand.
     */
    public static final int KIND_OPERAND = 0x08;

    /**
     * An object which is a kind of prop.
     */
    public static final int KIND_PROP = 0x09;

    /**
     * An object which is a kind of rule.
     */
    public static final int KIND_RULE = 0x0a;

    /**
     * An object which is a kind of scene.
     */
    public static final int KIND_SCENE = 0x0b;

    /**
     * An object which acts as a source of data.
     */
    public static final int KIND_SOURCE = 0x0c;

    /**
     * An object which is a kind of timer.
     */
    public static final int KIND_TIMER = 0x0e;

    /**
     * An object which is Kind of a global variable.
     */
    public static final int KIND_VARIABLE = 0x0d;

    public static NameResolverFactory getEventNameFactory() {
        return new EventNameFactory();
    }

    public static int kindToResId(int kind) {
        switch (kind) {
            case KIND_ACTION:
                return R.string.label_action;
            case KIND_ASSET:
                return R.string.label_asset;
            case KIND_CHANNEL:
                return R.string.label_data_channel;
            case KIND_EXPERIMENT:
                return R.string.label_experiment;
            case KIND_GENERATOR:
                return R.string.label_generator;
            case KIND_LOOP:
                return R.string.label_loop;
            case KIND_OPERAND:
                return R.string.label_operand;
            case KIND_PROP:
                return R.string.label_prop;
            case KIND_VARIABLE:
                return R.string.label_variable;
            case KIND_RULE:
                return R.string.label_rule;
            case KIND_SCENE:
                return R.string.label_scene;
            case KIND_SOURCE:
                return R.string.label_data_source;
            case KIND_EVENT:
                return R.string.label_event;
            case KIND_TIMER:
                return R.string.label_timer;

            default:
                throw new RuntimeException("Unknown experiment object kind " + kind);
        }
    }

    public static class EventNameFactory implements NameResolverFactory {
        @Override
        public String getName(Context context, int lookup) {
            return context.getString(R.string.event_missing_string);
        }
    }

    public static class MethodData {
        public int id;

        public String name;

        public int returnType;
    }

    public static class MethodNameFactory implements NameResolverFactory {
        @Override
        public String getName(Context context, int lookup) {
            return context.getString(R.string.method_missing_string);
        }
    }

    public static class ParameterData {
        public int id;

        public String name;

        public int type;
    }

    public static class ParameterNameFactory implements NameResolverFactory {
        @Override
        public String getName(Context context, int lookup) {
            return context.getString(R.string.parameter_missing_string);
        }
    }

    @Expose
    public String note;

    @Expose
    public int tag;

    /**
     * The general kind of object this experiment object is.
     *
     * @return The kind of object.
     */
    abstract public int kind();

    /**
     * A formatted, readable name for the object.
     *
     * @param context Application context to pull i18n strings from.
     * @return I18n sensitive name of the object.
     */
    abstract public String getExperimentObjectName(Context context);

    public abstract int getKindResId();

    public NameResolverFactory getMethodNameFactory() {
        return new MethodNameFactory();
    }

    public NameResolverFactory getParameterNameFactory() {
        return new ParameterNameFactory();
    }

    public ParameterData[] getParameters(Activity activity, int methodId) {
        final Method method = getMethod(methodId);

        // Get name factory to generate localised parameter names later.
        final NameResolverFactory nameFactory = getParameterNameFactory();

        final Class<?>[] parameterReflections = method.getParameterTypes();
        final ParameterData[] parameters = new ParameterData[parameterReflections.length];

        // Load in all parameters.
        for (int i = 0; i < parameterReflections.length; i++) {
            // Expect annotation is present for parameter.
            final ParameterId paramAnnotation = ModelUtils.getParameterIdAnnotation(i, method);
            if (paramAnnotation == null) {
                throw new RuntimeException(
                        "Missing annotation for parameter " + i + " of method " + methodId +
                        " on class " + getClass().getName());
            }

            ParameterData parameter = new ParameterData();
            parameter.id = paramAnnotation.value();
            parameter.name = nameFactory.getName(activity, parameter.id);

            Class<?> parameterReflection = parameterReflections[i];
            if (parameterReflection.isAssignableFrom(float.class)) {
                parameter.type = Type.TYPE_FLOAT;
            } else if (parameterReflection.isAssignableFrom(int.class)) {
                parameter.type = Type.TYPE_INTEGER;
            } else if (parameterReflection.isAssignableFrom(String.class)) {
                parameter.type = Type.TYPE_STRING;
            } else if (parameterReflection.isAssignableFrom(boolean.class)) {
                parameter.type = Type.TYPE_BOOLEAN;
            } else if (parameterReflection.isAssignableFrom(ImageStub.class)) {
                parameter.type = Type.TYPE_IMAGE;
            } else if (parameterReflection.isAssignableFrom(SoundStub.class)) {
                parameter.type = Type.TYPE_SOUND;
            } else if (parameterReflection.isAssignableFrom(VideoStub.class)) {
                parameter.type = Type.TYPE_VIDEO;
            } else {
                throw new RuntimeException(
                        "Unknown type for parameter " + parameter.id + " on method " + methodId +
                        " for class " + getClass().getName());
            }

            parameters[i] = parameter;
        }
        return parameters;
    }

    public int getTag() {
        return tag;
    }

    public void loadInMatchingMethods(Context context, int returnType, SortedSet<MethodData> out) {
        Class<? extends ExperimentObject> clazz = getClass();
        Method[] methods = clazz.getMethods();

        NameResolverFactory nameFactory = getMethodNameFactory();

        // Filter methods for those which register listeners for events.
        for (int i = 0; i < methods.length; i++) {
            Method method = methods[i];
            MethodId annotation = method.getAnnotation(MethodId.class);
            if (annotation != null && ModelUtils.returnTypeIntersects(method, returnType)) {
                MethodData data = new MethodData();
                data.id = annotation.value();
                data.name = nameFactory.getName(context, data.id);

                Class<?> rt = method.getReturnType();
                if (rt.equals(Void.TYPE)) {
                    data.returnType = Type.TYPE_VOID;
                } else if (rt.equals(Boolean.TYPE)) {
                    data.returnType = Type.TYPE_BOOLEAN;
                } else if (rt.equals(Integer.TYPE)) {
                    data.returnType = Type.TYPE_INTEGER;
                } else if (rt.equals(Float.TYPE)) {
                    data.returnType = Type.TYPE_FLOAT;
                } else if (rt.equals(String.class)) {
                    data.returnType = Type.TYPE_STRING;
                } else if (rt.equals(ImageStub.class)) {
                    data.returnType = Type.TYPE_IMAGE;
                } else if (rt.equals(SoundStub.class)) {
                    data.returnType = Type.TYPE_SOUND;
                } else if (rt.equals(VideoStub.class)) {
                    data.returnType = Type.TYPE_VIDEO;
                } else {
                    throw new RuntimeException(
                            "Unknown return type for method " + data.id + " for class " +
                            getClass().getName());
                }

                out.add(data);
            }
        }
    }

    public boolean satisfiesFilter(int filter) {
        switch (filter) {
            case HAS_SETTERS: {
                Method[] methods = getClass().getMethods();
                for (int i = 0; i < methods.length; i++) {
                    Method method = methods[i];
                    if (method.getReturnType().equals(Void.TYPE) &&
                        method.isAnnotationPresent(MethodId.class)) {
                        return true;
                    }
                }
                return false;
            }
            case EMITS_EVENTS: {
                Method[] methods = getClass().getMethods();
                for (int i = 0; i < methods.length; i++) {
                    Method method = methods[i];
                    if (method.isAnnotationPresent(EventData.class)) {
                        return true;
                    }
                }
                return false;
            }
            default: {
                Method[] methods = getClass().getMethods();
                for (int i = 0; i < methods.length; i++) {
                    Method method = methods[i];
                    if (method.isAnnotationPresent(MethodId.class)) {
                        if ((filter & Type.TYPE_BOOLEAN) != 0 &&
                            method.getReturnType().equals(Boolean.TYPE)) {
                            return true;
                        }
                        if ((filter & Type.TYPE_STRING) != 0 &&
                            method.getReturnType().equals(String.class)) {
                            return true;
                        }
                        if ((filter & Type.TYPE_INTEGER) != 0 &&
                            method.getReturnType().equals(Integer.TYPE)) {
                            return true;
                        }
                        if ((filter & Type.TYPE_FLOAT) != 0 &&
                            method.getReturnType().equals(Float.TYPE)) {
                            return true;
                        }
                    }
                }
                return false;
            }
        }
    }

    private Method getMethod(int methodId) {
        Method method = null;

        Method[] methods = getClass().getMethods();
        for (Method m : methods) {
            MethodId annoData = m.getAnnotation(MethodId.class);
            if (annoData != null && annoData.value() == methodId) {
                method = m;
                break;
            }
        }

        if (method == null) {
            throw new RuntimeException("Unknown method id " + methodId);
        }
        return method;
    }
}
