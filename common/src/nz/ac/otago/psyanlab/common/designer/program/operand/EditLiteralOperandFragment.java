
package nz.ac.otago.psyanlab.common.designer.program.operand;

import nz.ac.otago.psyanlab.common.R;
import nz.ac.otago.psyanlab.common.model.Operand;
import nz.ac.otago.psyanlab.common.model.operand.IntegerValue;
import nz.ac.otago.psyanlab.common.util.TonicFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * A fragment that provides a UI to input a literal value or expression as an
 * operand.
 */
public class EditLiteralOperandFragment extends TonicFragment {
    private Operand mOperand;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (mObjectId == -1) {
            mOperand = new IntegerValue();
            mObjectId = mCallbacks.createOperand(mOperand);
        } else {
            mOperand = mCallbacks.getOperand(mObjectId);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit_literal_operand, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onViewCreated(view, savedInstanceState);
    }

    public class ViewHolder extends TonicFragment.ViewHolder<Operand> {
        public ViewHolder(View view) {
            expression = (TextView)view.findViewById(R.id.text);
        }

        public TextView expression;

        @Override
        public void initViews() {
        }

        @Override
        public void setViewValues(Operand operand) {
        }
    }
}
