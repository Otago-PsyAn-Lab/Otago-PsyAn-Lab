
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

package nz.ac.otago.psyanlab.common.designer.program.stage;

import nz.ac.otago.psyanlab.common.designer.util.PropIdPair;
import nz.ac.otago.psyanlab.common.model.Prop;

import android.widget.ArrayAdapter;

public interface StageCallbacks {
    void deleteProp(int propId);

    PropIdPair getProp(int id);

    ArrayAdapter<PropIdPair> getPropAdapter();

    int getStageHeight();

    int getStageMode();

    int getStageOrientation();

    int getStageWidth();

    /**
     * Refresh the stage to reflect the current set properties.
     */
    void refreshStage();

    void saveProp(int propId, Prop prop);

    void saveProp(Prop prop);

    void setStageOrientation(int orientation);

    long getNewPropKey();
}
