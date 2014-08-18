/*
 Copyright (C) 2014 Tonic Artos <tonic.artos@gmail.com>

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

package nz.ac.otago.psyanlab.common.designer.program.util;

import android.widget.ListAdapter;

import nz.ac.otago.psyanlab.common.designer.ExperimentDesignerActivity;
import nz.ac.otago.psyanlab.common.model.Timer;

public interface TimerCallbacks {
    void addTimerDataChangeListener(ExperimentDesignerActivity.TimerDataChangeListener listener);

    long addTimer(Timer timer);

    void deleteTimer(long id);

    Timer getTimer(long id);

    ListAdapter getTimersAdapter(long objectId);

    void putTimer(long id, Timer timer);

    void removeTimerDataChangeListener(ExperimentDesignerActivity.TimerDataChangeListener listener);
}
