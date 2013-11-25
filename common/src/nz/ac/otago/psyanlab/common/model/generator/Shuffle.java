
package nz.ac.otago.psyanlab.common.model.generator;

import nz.ac.otago.psyanlab.common.model.Generator;
import nz.ac.otago.psyanlab.common.model.util.NameResolverFactory;

public class Shuffle extends Generator {
    public static NameResolverFactory getMethodNameFactory() {
        return new MethodNameFactory();
    }

    public Shuffle() {
    }

    protected static class MethodNameFactory extends Generator.MethodNameFactory {
        @Override
        public int getResId(int lookup) {
            switch (lookup) {
                default:
                    return super.getResId(lookup);
            }
        }
    }
}
