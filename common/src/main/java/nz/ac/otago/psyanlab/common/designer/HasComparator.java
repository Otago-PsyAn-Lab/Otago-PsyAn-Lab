
package nz.ac.otago.psyanlab.common.designer;

import java.util.Comparator;

public interface HasComparator<T> {
    Comparator<T> getComparator();
}
