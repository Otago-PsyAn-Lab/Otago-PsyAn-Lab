
package nz.ac.otago.psyanlab.common.model.prop;

import android.content.Context;

import nz.ac.otago.psyanlab.common.model.Prop;
import nz.ac.otago.psyanlab.common.model.util.NameResolverFactory;

public class Seekbar extends Prop {
    public static NameResolverFactory getEventNameFactory() {
        return new EventNameFactory();
    }

    @Override
    public NameResolverFactory getMethodNameFactory() {
        return new MethodNameFactory();
    }

    @Override
    public NameResolverFactory getParameterNameFactory() {
        return new ParameterNameFactory();
    }

    protected static class EventNameFactory extends Prop.EventNameFactory {
        @Override
        public String getName(Context context, int lookup) {
            switch (lookup) {
                default:
                    return super.getName(context, lookup);
            }
        }
    }

    protected static class MethodNameFactory extends Prop.MethodNameFactory {
        @Override
        public String getName(Context context, int lookup) {
            switch (lookup) {
                default:
                    return super.getName(context, lookup);
            }
        }
    }

    protected static class ParameterNameFactory extends Prop.ParameterNameFactory {
        @Override
        public String getName(Context context, int lookup) {
            switch (lookup) {
                default:
                    return super.getName(context, lookup);
            }
        }
    }
}
