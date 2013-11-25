
package nz.ac.otago.psyanlab.common.designer.util;

import android.os.Bundle;

public interface DialogueResultListenerRegistrar {
    void registerDialogueResultListener(int requestCode, DialogueResultListener listener);

    public interface DialogueResultListener {
        void onResult(Bundle data);
    }
}
