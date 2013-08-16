
package nz.ac.otago.psyanlab.single;

import nz.ac.otago.psyanlab.common.ScreenValuesI;

import android.os.Parcel;
import android.os.Parcelable;

public class ScreenValues implements ScreenValuesI, Parcelable {

    public static final Parcelable.Creator<ScreenValues> CREATOR = new Parcelable.Creator<ScreenValues>() {
        @Override
        public ScreenValues createFromParcel(Parcel in) {
            return new ScreenValues(in);
        }

        @Override
        public ScreenValues[] newArray(int size) {
            return new ScreenValues[size];
        }
    };

    private OrientationDimensionsI mLandScapeDimensionsInterface = new OrientationDimensionsI() {
        @Override
        public int getHeight() {
            return mLandscapeHeight;
        }

        @Override
        public int getWidth() {
            return mLandscapeWidth;
        }
    };

    private int mLandscapeHeight;

    private int mLandscapeWidth;

    private OrientationDimensionsI mPortraitDimensionsInterface = new OrientationDimensionsI() {
        @Override
        public int getHeight() {
            return mPortraitHeight;
        }

        @Override
        public int getWidth() {
            return mPortraitWidth;
        }
    };

    private int mPortraitHeight;

    private int mPortraitWidth;

    public ScreenValues() {
    }

    public ScreenValues(int portraitWidth, int portraitHeight, int landscapeWidth,
            int landscapeHeight) {
        mPortraitWidth = portraitWidth;
        mPortraitHeight = portraitHeight;
        mLandscapeWidth = landscapeWidth;
        mLandscapeHeight = landscapeHeight;
    }

    public ScreenValues(Parcel in) {
        this(in.readInt(), in.readInt(), in.readInt(), in.readInt());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public OrientationDimensionsI getLandscapeScreen() {
        return mLandScapeDimensionsInterface;
    }

    @Override
    public OrientationDimensionsI getPortraitScreen() {
        return mPortraitDimensionsInterface;
    }

    public void setLandscapeValues(int width, int height) {
        mLandscapeWidth = width;
        mLandscapeHeight = height;
    }

    public void setPortraitValues(int width, int height) {
        mPortraitWidth = width;
        mPortraitHeight = height;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mPortraitWidth);
        dest.writeInt(mPortraitHeight);
        dest.writeInt(mLandscapeWidth);
        dest.writeInt(mLandscapeHeight);
    }
}
