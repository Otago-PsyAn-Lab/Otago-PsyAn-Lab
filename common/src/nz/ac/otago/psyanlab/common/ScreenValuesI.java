
package nz.ac.otago.psyanlab.common;

public interface ScreenValuesI {
    OrientationDimensionsI getLandscapeScreen();

    OrientationDimensionsI getPortraitScreen();

    public interface OrientationDimensionsI {
        int getHeight();

        int getWidth();
    }
}
