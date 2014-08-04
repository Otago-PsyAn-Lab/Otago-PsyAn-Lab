
package nz.ac.otago.psyanlab.common.designer.util;

import android.os.Bundle;

public interface DialogueResultCallbacks {
    void onDialogueResult(int requestCode, Bundle data);

    void onDialogueResultDelete(int requestCode, Bundle data);

    void onDialogueResultCancel(int requestCode);
}
