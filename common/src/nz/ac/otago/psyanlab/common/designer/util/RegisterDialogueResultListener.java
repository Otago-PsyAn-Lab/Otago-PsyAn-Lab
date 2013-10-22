
package nz.ac.otago.psyanlab.common.designer.util;

public interface RegisterDialogueResultListener {
    void registerDialogueResultListener(String requestTag, DialogueResultListener<?> listener);

    public interface DialogueResultListener<T> {
        void onResult(T value);
    }
}
