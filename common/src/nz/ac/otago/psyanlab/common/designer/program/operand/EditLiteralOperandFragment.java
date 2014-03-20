
package nz.ac.otago.psyanlab.common.designer.program.operand;

import nz.ac.otago.psyanlab.common.R;
import nz.ac.otago.psyanlab.common.designer.ExperimentDesignerActivity;
import nz.ac.otago.psyanlab.common.expression.Lexer;
import nz.ac.otago.psyanlab.common.expression.OpalExpressionParser;
import nz.ac.otago.psyanlab.common.expression.ParseException;
import nz.ac.otago.psyanlab.common.expression.Parser;
import nz.ac.otago.psyanlab.common.expression.PrintTypeErrorVisitor;
import nz.ac.otago.psyanlab.common.expression.PrintVisitor;
import nz.ac.otago.psyanlab.common.expression.RefineTypeVisitor;
import nz.ac.otago.psyanlab.common.expression.RefineTypeVisitor.TypeException;
import nz.ac.otago.psyanlab.common.expression.expressions.Expression;
import nz.ac.otago.psyanlab.common.model.Operand;
import nz.ac.otago.psyanlab.common.model.operand.ExpressionValue;
import nz.ac.otago.psyanlab.common.model.operand.kind.ExpressionOperand;
import nz.ac.otago.psyanlab.common.model.operand.kind.LiteralOperand;
import nz.ac.otago.psyanlab.common.util.TonicFragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.BackgroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.HashMap;

/**
 * A fragment that provides a UI to input a literal value or expression as an
 * operand.
 */
public class EditLiteralOperandFragment extends AbsOperandFragment {
    public TextWatcher mExpressionChangedListener = new TextWatcher() {

        @Override
        public void afterTextChanged(Editable s) {
            handleExpressionChange(s);
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }
    };

    public OnItemClickListener mOperandItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Operand operand = mCallbacks.getOperand(id);
            showEditOperandDialogue(id, operand.getType());
        }
    };

    private final BackgroundColorSpan mErrorSpan = new BackgroundColorSpan(0xFFFF0000);

    private LiteralOperand mLiteral;

    private Operand mOperand;

    private ViewHolder mViews;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (mObjectId == INVALID_ID) {
            throw new RuntimeException("Invalid operand id given.");
        }

        mOperand = mCallbacks.getOperand(mObjectId);
        if (mOperand instanceof LiteralOperand) {
            mLiteral = (LiteralOperand)mOperand;
        } else {
            mLiteral = null;
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
        mViews.setViewValues(mLiteral);
    }

    protected void handleExpressionChange(Editable s) {
        mViews.hideParsed();
        mViews.hideError();

        String inputString = s.toString();
        Lexer lexer = new Lexer(inputString);
        Parser parser = new OpalExpressionParser(lexer);
        Expression expression;

        try {
            expression = parser.parseExpression();
        } catch (ParseException e) {
            int length = parser.getLastUnparsed().toString().length();
            Log.d("exception", length + "");
            int start = lexer.getTextLexed().length() + length;
            Log.d("exception", start + "");
            int end = start + (length == 0 ? 1 : length);
            Log.d("exception", end + "");

            SpannableString text = new SpannableString(inputString + (length == 0 ? " " : ""));
            text.setSpan(mErrorSpan, start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
            mViews.setParsed(text);
            mViews.setError(e.getMessage());
            return;
        }

        PrintVisitor prettyPrint = new PrintVisitor();
        expression.accept(prettyPrint);
        String prettyExpression = prettyPrint.toString();

        if (parser.areUnparsedTokens()) {
            String lastToken = parser.getLastUnparsed().toString();
            Log.d("", lastToken);
            SpannableString text = new SpannableString(prettyExpression + " " + lastToken
                    + lexer.getTextRemainder());
            text.setSpan(mErrorSpan, prettyExpression.length() + 1, prettyExpression.length()
                    + lastToken.length() + 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
            mViews.setParsed(text);
            mViews.setError("Expected operator.");
            return;
        }

        RefineTypeVisitor typeCheck = new RefineTypeVisitor(getActivity(), mCallbacks,
                new HashMap<String, Long>(), mOperandType);
        boolean wasError = false;
        try {
            expression.accept(typeCheck);
        } catch (TypeException error) {
            wasError = true;
        }

        PrintTypeErrorVisitor findError = new PrintTypeErrorVisitor(typeCheck.getError());
        expression.accept(findError);
        if (wasError) {
            SpannableString text = new SpannableString(prettyExpression);
            text.setSpan(mErrorSpan, findError.getErrorStart(), findError.getErrorEnd(),
                    Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
            mViews.setParsed(text);
            mViews.setError(findError.getErrorMessage());
            return;
        }

        if (mLiteral == null) {
            mLiteral = new ExpressionValue();
        }

        mViews.setParsed(prettyExpression);
        ((ExpressionValue)mLiteral).expression = typeCheck.toString();
        mCallbacks.updateOperand(mObjectId, (Operand)mLiteral);
    }

    protected void showEditOperandDialogue(long id, int type) {
        EditOperandDialogFragment dialog = EditOperandDialogFragment.newDialog(id, type);
        dialog.show(getChildFragmentManager(), "dialog_edit_operand");
    }

    public class ViewHolder extends TonicFragment.ViewHolder<LiteralOperand> {
        public TextView error;

        public EditText expression;

        private ListView operands;

        private TextView parsed;

        public ViewHolder(View view) {
            expression = (EditText)view.findViewById(R.id.text);
            error = (TextView)view.findViewById(R.id.error);
            parsed = (TextView)view.findViewById(R.id.parsed);
            operands = (ListView)view.findViewById(R.id.operands);
        }

        public void hideError() {
            error.setVisibility(View.GONE);
        }

        public void hideParsed() {
            parsed.setVisibility(View.GONE);
        }

        @Override
        public void initViews() {
            expression.addTextChangedListener(mExpressionChangedListener);
            operands.setOnItemClickListener(mOperandItemClickListener);
        }

        public void setError(Spannable errorMessage) {
            if (TextUtils.isEmpty(errorMessage)) {
                parsed.setVisibility(View.GONE);
                return;
            }
            error.setVisibility(View.VISIBLE);
            error.setText(errorMessage);
        }

        public void setError(String errorMessage) {
            if (TextUtils.isEmpty(errorMessage)) {
                parsed.setVisibility(View.GONE);
                return;
            }
            error.setVisibility(View.VISIBLE);
            error.setText(errorMessage);
        }

        public void setParsed(Spannable parsedExpression) {
            if (TextUtils.isEmpty(parsedExpression)) {
                parsed.setVisibility(View.GONE);
                return;
            }
            parsed.setVisibility(View.VISIBLE);
            parsed.setText(parsedExpression);
        }

        public void setParsed(String parsedExpression) {
            if (TextUtils.isEmpty(parsedExpression)) {
                parsed.setVisibility(View.GONE);
                return;
            }
            parsed.setVisibility(View.VISIBLE);
            parsed.setText(parsedExpression);
        }

        @Override
        public void setViewValues(LiteralOperand operand) {
            if (operand != null) {
                expression.setText(operand.getValue());
            }
        }

        public void updateViews(LiteralOperand operand) {
            mViews.expression.setText(operand.getValue());

            if (operand instanceof ExpressionOperand) {
                operands.setAdapter(mCallbacks.getOperandAdapter(mObjectId,
                        ExperimentDesignerActivity.OPERAND_ACCESS_SCOPE_OPERAND));
            }
        }
    }
}
