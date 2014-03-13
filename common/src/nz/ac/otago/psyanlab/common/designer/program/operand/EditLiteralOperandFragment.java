
package nz.ac.otago.psyanlab.common.designer.program.operand;

import nz.ac.otago.psyanlab.common.R;
import nz.ac.otago.psyanlab.common.designer.ExperimentDesignerActivity;
import nz.ac.otago.psyanlab.common.designer.util.ExpressionCompiler;
import nz.ac.otago.psyanlab.common.model.Operand;
import nz.ac.otago.psyanlab.common.model.operand.kind.ExpressionOperand;
import nz.ac.otago.psyanlab.common.model.operand.kind.LiteralOperand;
import nz.ac.otago.psyanlab.common.util.TonicFragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;

/**
 * A fragment that provides a UI to input a literal value or expression as an
 * operand.
 */
public class EditLiteralOperandFragment extends TonicFragment {
    private static final String ARG_OPERAND_TYPE_CONSTRAINTS = "arg_operand_type";

    public TextWatcher mExpressionChangedListener = new TextWatcher() {
        @Override
        public void afterTextChanged(Editable s) {
            if (TextUtils.equals(s, mCurrentExpressionString)) {
                return;
            }
            handleExpressionChange(s);
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }
    };

    public OnItemClickListener mOnOperandItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            showEditOperandDialogue(id);
        }
    };

    private Operand mOperand;

    private ViewHolder mViews;

    protected String mCurrentExpressionString = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (mObjectId != -1) {
            mOperand = mCallbacks.getOperand(mObjectId);
            if (!(mOperand instanceof ExpressionOperand)) {
                mObjectId = -1;
                mOperand = null;
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit_literal_operand, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mViews = new ViewHolder(view);
        mViews.initViews();
        mViews.setViewValues(mOperand);
    }

    protected void handleExpressionChange(Editable s) {
        int caretPosition = mViews.expression.getSelectionStart();
        ExpressionCompiler expression = new ExpressionCompiler(mCallbacks);
        // expression.compile(s.toString());
        // Result r = OldExpressionParser.process(s.toString(), mOperand,
        // caretPosition);
        //
        // mOperand = r.operand;
        mCallbacks.updateOperand(mObjectId, mOperand);

        mViews.updateViews((LiteralOperand)mOperand);
        // Update caret position to equivalent after any formatting.
        // mViews.expression.setSelection(r.caretPosition);
    }

    protected void showEditOperandDialogue(long id) {
        EditOperandDialogFragment dialog = EditOperandDialogFragment.newDialog(id);
        dialog.show(getChildFragmentManager(), "dialog_edit_operand");
    }

    public class ViewHolder extends TonicFragment.ViewHolder<Operand> {
        public EditText expression;

        private ListView operands;

        public ViewHolder(View view) {
            expression = (EditText)view.findViewById(R.id.text);
            operands = (ListView)view.findViewById(R.id.operands);
        }

        @Override
        public void initViews() {
            expression.addTextChangedListener(mExpressionChangedListener);
            operands.setOnItemClickListener(mOnOperandItemClickListener);
        }

        @Override
        public void setViewValues(Operand operand) {
            if (operand instanceof LiteralOperand) {
                expression.setText(operand.getName());

            }
        }

        public void updateViews(LiteralOperand operand) {
            mCurrentExpressionString = operand.getValue();
            mViews.expression.setText(mCurrentExpressionString);

            if (operand instanceof ExpressionOperand) {
                operands.setAdapter(mCallbacks.getOperandAdapter(mObjectId,
                        ExperimentDesignerActivity.OPERAND_ACCESS_SCOPE_OPERAND));
            }
        }
    }
}
