
package nz.ac.otago.psyanlab.common.designer.program.util;

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
 * Collection of all the call-backs used to implement the program editor
 * interface.
 */
public interface ProgramCallbacks extends DialogueResultListenerRegistrar, OperandCallbacks,
        ActionCallbacks, GeneratorCallbacks, LoopCallbacks, RuleCallbacks, SceneCallbacks,
        PropCallbacks, ExperimentObjectBrowserCallbacks {
}
