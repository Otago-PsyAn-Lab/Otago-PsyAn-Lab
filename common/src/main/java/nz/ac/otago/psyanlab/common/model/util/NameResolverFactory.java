
package nz.ac.otago.psyanlab.common.model.util;

import android.content.Context;

public interface NameResolverFactory {
    String getName(Context context, int lookup);
}
