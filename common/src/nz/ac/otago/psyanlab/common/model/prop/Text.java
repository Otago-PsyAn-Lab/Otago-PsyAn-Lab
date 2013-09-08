
package nz.ac.otago.psyanlab.common.model.prop;

import com.google.gson.annotations.Expose;

import nz.ac.otago.psyanlab.common.model.Prop;

import android.os.Parcel;

public class Text extends Prop {
    @Expose
    String mText;

    @Expose
    int mFontSize;

    public Text(Parcel in) {
        super(in);

        mText = in.readString();
        mFontSize = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);

        dest.writeString(mText);
        dest.writeInt(mFontSize);
    }
}
