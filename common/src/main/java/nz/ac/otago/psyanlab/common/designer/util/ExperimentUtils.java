
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

package nz.ac.otago.psyanlab.common.designer.util;

import nz.ac.otago.psyanlab.common.model.Experiment;

import android.app.Activity;

/**
 * Utilities for manipulating the experiment data model.
 */
public class ExperimentUtils {
    public static void convertPropValues(Experiment experiment, float scalingFactor, int xOffset,
            int yOffset) {
    }

    /**
     * Converts the experiment reference screen to the current device. Changes
     * are logged and viewable as experiment meta-data.
     * 
     * @param experiment Experiment to convert.
     */
    public static void convertToScreen(Activity activity, Experiment experiment) {
        // View content = activity.getWindow().getDecorView().getRootView();
        // int targetWidth = content.getWidth();
        // int targetHeight = content.getHeight();
        //
        // float widthScale = ((float)targetWidth) / experiment.screen.width;
        // float heightScale = ((float)targetHeight) / experiment.screen.height;
        // float scalingFactor = (heightScale < widthScale) ? heightScale :
        // widthScale;
        //
        // targetWidth = (int)(experiment.screen.width * scalingFactor);
        // targetHeight = (int)(experiment.screen.height * scalingFactor);
        //
        // int xOffset = (targetWidth - experiment.screen.width) / 2;
        // int yOffset = (targetHeight - experiment.screen.height) / 2;
        //
        // convertPropValues(experiment, scalingFactor, xOffset, yOffset);
        //
        // experiment.screen.height = targetHeight;
        // experiment.screen.width = targetWidth;
        //
        // Time now = new Time();
        // now.setToNow();
        // ScreenConversion screenConversion = new ScreenConversion();
        // screenConversion.scalingFactor = scalingFactor;
        // screenConversion.xOffset = xOffset;
        // screenConversion.yOffset = yOffset;
        // experiment.screen.pastScaleFactors.put(now.toMillis(false),
        // screenConversion);
    }

    public static int zeroBasedToUserValue(int value) {
        return value + 1;
    }

    public static int userValueToZeroBased(int value) {
        return value - 1;
    }
}
