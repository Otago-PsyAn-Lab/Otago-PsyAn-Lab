
package nz.ac.otago.psyanlab.common.util;

import android.widget.TextView;

/**
 * A simple holder for any number of TextViews. The easiest method of use is to
 * use constants to index the TextView array with.
 */
public class TextViewHolder {
    public TextViewHolder(int numTextViews) {
        textViews = new TextView[numTextViews];
    }

    public TextView[] textViews;
}
