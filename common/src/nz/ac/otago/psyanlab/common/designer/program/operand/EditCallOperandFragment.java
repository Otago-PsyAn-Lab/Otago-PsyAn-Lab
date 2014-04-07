
package nz.ac.otago.psyanlab.common.designer.program.operand;


/**
 * A fragment that provides a UI to input a call value as an operand.
 */
public class EditCallOperandFragment extends AbsOperandFragment {

    // private class ViewHolder extends
    // BaseProgramFragment.ViewHolder<CallOperand> {
    // public Spinner actionMethod;
    //
    // public Button actionObject;
    //
    // private ListView operands;
    //
    // public ViewHolder(View view) {
    // super(view);
    // actionObject = (Button)view.findViewById(R.id.action_object);
    // actionMethod = (Spinner)view.findViewById(R.id.action_method);
    // operands = (ListView)view.findViewById(R.id.operands);
    // }
    //
    // @Override
    // public void initViews() {
    // actionObject.setOnClickListener(mActionObjectOnClickListener);
    // actionMethod.setOnItemSelectedListener(mActionMethodOnItemSelectedListener);
    //
    // operands.setAdapter(mParameterAdapter);
    // operands.setOnItemClickListener(mOnParameterItemClickListener);
    // operands.setDivider(null);
    // }
    //
    // @Override
    // public void setViewValues(CallOperand operand) {
    // ExperimentObjectReference actionObject = operand.getActionObject();
    // if (actionObject != null) {
    // setAction(operand);
    // } else {
    // unsetAction();
    // }
    // }
    //
    // private void setAction(CallOperand operand) {
    // actionObject.setText(mCallbacks.getExperimentObject(operand.getActionObject())
    // .getPrettyName(getActivity()));
    // actionMethod.setEnabled(true);
    // SpinnerAdapter methodsAdapter = mCallbacks.getMethodsAdapter(mCallbacks
    // .getExperimentObject(operand.getActionObject()).getClass(), Void.TYPE);
    // actionMethod.setAdapter(methodsAdapter);
    // for (int i = 0; i < methodsAdapter.getCount(); i++) {
    // if ((int)methodsAdapter.getItemId(i) == operand.getActionMethod()) {
    // actionMethod.setSelection(i);
    // break;
    // }
    // }
    //
    // }
    //
    // private void unsetAction() {
    // actionObject.setText(null);
    // actionMethod.setEnabled(false);
    // actionMethod.setAdapter(null);
    // }
    // }
}
