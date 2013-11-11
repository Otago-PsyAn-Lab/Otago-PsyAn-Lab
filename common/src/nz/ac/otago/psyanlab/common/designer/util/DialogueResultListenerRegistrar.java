
package nz.ac.otago.psyanlab.common.designer.util;

import android.os.Bundle;

public interface DialogueResultListenerRegistrar {
    void registerDialogueResultListener(String requestTag, DialogueResultListener listener);

    public interface DialogueResultListener {
        void onResult(Bundle data);
    }
}
