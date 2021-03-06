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

package nz.ac.otago.psyanlab.common.designer.program.util;

import java.nio.channels.Channel;

import nz.ac.otago.psyanlab.common.designer.channel.ChannelCallbacks;
import nz.ac.otago.psyanlab.common.designer.source.SourceCallbacks;
import nz.ac.otago.psyanlab.common.designer.util.ActionCallbacks;
import nz.ac.otago.psyanlab.common.designer.util.DialogueResultListenerRegistrar;
import nz.ac.otago.psyanlab.common.designer.util.ExperimentObjectBrowserCallbacks;
import nz.ac.otago.psyanlab.common.designer.util.GeneratorCallbacks;
import nz.ac.otago.psyanlab.common.designer.util.LoopCallbacks;
import nz.ac.otago.psyanlab.common.designer.util.OperandCallbacks;
import nz.ac.otago.psyanlab.common.designer.util.PropCallbacks;
import nz.ac.otago.psyanlab.common.designer.util.RuleCallbacks;
import nz.ac.otago.psyanlab.common.designer.util.SceneCallbacks;

/**
 * Collection of all the call-backs used to implement the program editor interface.
 */
public interface ProgramCallbacks
        extends DialogueResultListenerRegistrar, OperandCallbacks, ActionCallbacks,
                GeneratorCallbacks, LoopCallbacks, RuleCallbacks, SceneCallbacks, PropCallbacks,
                ExperimentObjectBrowserCallbacks, ChannelCallbacks, SourceCallbacks,
                TimerCallbacks {}
