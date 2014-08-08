
package nz.ac.otago.psyanlab.common.model.generator;

import android.content.Context;

import nz.ac.otago.psyanlab.common.model.Generator;
import nz.ac.otago.psyanlab.common.model.util.NameResolverFactory;

public class Shuffle extends Generator {
    @Override
    public NameResolverFactory getMethodNameFactory() {
        return new MethodNameFactory();
    }

    public Shuffle() {
    }

    protected static class MethodNameFactory extends Generator.MethodNameFactory {
        @Override
        public String getName(Context context, int lookup) {
            switch (lookup) {
                default:
                    return super.getName(context, lookup);
            }
        }
    }
}
