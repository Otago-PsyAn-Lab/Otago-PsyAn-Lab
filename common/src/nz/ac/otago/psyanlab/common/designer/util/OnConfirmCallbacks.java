
package nz.ac.otago.psyanlab.common.designer.util;

public interface OnConfirmCallbacks<T> {
    /**
     * Called when a number is picked by the dialog. The value is passed back
     * including a request code used to sort out the results.
     * 
     * @param requestCode Code indicating the purpose of the number passed.
     * @param value Number chosen by user.
     */
    void onConfirm(String requestCode, T value);
}
