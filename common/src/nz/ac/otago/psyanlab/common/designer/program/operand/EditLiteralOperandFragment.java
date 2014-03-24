
package nz.ac.otago.psyanlab.common.designer.program.operand;

import nz.ac.otago.psyanlab.common.R;
import nz.ac.otago.psyanlab.common.designer.ExperimentDesignerActivity;
import nz.ac.otago.psyanlab.common.designer.ProgramComponentAdapter;
import nz.ac.otago.psyanlab.common.expression.Lexer;
import nz.ac.otago.psyanlab.common.expression.OpalExpressionParser;
import nz.ac.otago.psyanlab.common.expression.ParseException;
import nz.ac.otago.psyanlab.common.expression.Parser;
import nz.ac.otago.psyanlab.common.expression.PrintTypeErrorVisitor;
import nz.ac.otago.psyanlab.common.expression.PrintVisitor;
import nz.ac.otago.psyanlab.common.expression.RefineTypeVisitor;
import nz.ac.otago.psyanlab.common.expression.RefineTypeVisitor.TypeException;
import nz.ac.otago.psyanlab.common.expression.expressions.BooleanExpression;
import nz.ac.otago.psyanlab.common.expression.expressions.Expression;
import nz.ac.otago.psyanlab.common.expression.expressions.FloatExpression;
import nz.ac.otago.psyanlab.common.expression.expressions.IntegerExpression;
import nz.ac.otago.psyanlab.common.expression.expressions.StringExpression;
import nz.ac.otago.psyanlab.common.model.Operand;
import nz.ac.otago.psyanlab.common.model.operand.BooleanValue;
import nz.ac.otago.psyanlab.common.model.operand.ExpressionValue;
import nz.ac.otago.psyanlab.common.model.operand.FloatValue;
import nz.ac.otago.psyanlab.common.model.operand.IntegerValue;
import nz.ac.otago.psyanlab.common.model.operand.StringValue;
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
import android.widget.HeaderViewListAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Collections;
import java.util.Comparator;
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
            showEditOperandDialogue(
                    id,
                    operand.getType(),
                    getString(R.string.title_edit_operand,
                            Operand.getTypeString(getActivity(), operand.getType())));
        }
    };

    private final BackgroundColorSpan mErrorSpan = new BackgroundColorSpan(0xFFFF0000);

    private LiteralOperand mOperand;

    private ViewHolder mViews;

    private ExpressionValue mExpression;

    private HashMap<String, Long> mOperandMap;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (mObjectId == INVALID_ID) {
            throw new RuntimeException("Invalid operand id given.");
        }

        Operand operand = mCallbacks.getOperand(mObjectId);
        if (operand instanceof ExpressionValue) {
            mExpression = (ExpressionValue)operand;
            mOperandMap = new HashMap<String, Long>();
            for (Long operandId : mExpression.operands) {
                mOperandMap.put(mCallbacks.getOperand(operandId).getName(), operandId);
            }
            mOperand = (LiteralOperand)operand;
        } else if (operand instanceof LiteralOperand) {
            mOperand = (LiteralOperand)operand;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_literal_operand, container, false);
        ListView list = (ListView)view.findViewById(R.id.operands);
        list.addHeaderView(inflater.inflate(R.layout.edit_literal_operand_header_content, list,
                false));
        list.addFooterView(inflater.inflate(R.layout.separator, list, false));
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mViews = new ViewHolder(view);
        mViews.initViews();
        mViews.setViewValues(mOperand);
    }

    private void saveOperand(LiteralOperand value) {
        mOperand = value;
        mCallbacks.updateOperand(mObjectId, (Operand)mOperand);
        mViews.updateViews(mOperand);
    }

    protected void handleExpressionChange(Editable s) {
        mViews.hideParsed();
        mViews.hideError();

        if (TextUtils.isEmpty(s)) {
            return;
        }

        String inputString = s.toString();
        Lexer lexer = new Lexer(inputString);
        Parser parser = new OpalExpressionParser(lexer);
        Expression expression;

        try {
            expression = parser.parseExpression();
        } catch (ParseException e) {
            handleParseException(inputString, lexer, parser, e);
            return;
        }

        if (handleLiteral(expression)) {
            return;
        }

        PrintVisitor prettyPrint = new PrintVisitor();
        expression.accept(prettyPrint);
        String prettyExpression = prettyPrint.toString();

        if (parser.areUnparsedTokens()) {
            handlePartialParse(lexer, parser, prettyExpression);
            return;
        }

        if (mOperandMap == null) {
            mOperandMap = new HashMap<String, Long>();
        }

        RefineTypeVisitor typeCheck = new RefineTypeVisitor(getActivity(), mCallbacks, mOperandMap,
                mOperandType);
        boolean wasError = false;
        try {
            expression.accept(typeCheck);
        } catch (TypeException error) {
            wasError = true;
        }

        PrintTypeErrorVisitor findError = new PrintTypeErrorVisitor(typeCheck.getError());
        expression.accept(findError);
        if (wasError) {
            handleTypeError(prettyExpression, findError);
            return;
        }

        HashMap<String, Long> operandMap = typeCheck.getOperandMap();
        if (mExpression == null) {
            mExpression = new ExpressionValue();
        }
        mExpression.operands.clear();
        mExpression.operands.addAll(operandMap.values());
        Collections.sort(mExpression.operands, new Comparator<Long>() {
            @Override
            public int compare(Long lhs, Long rhs) {
                return mCallbacks.getOperand(lhs).getName()
                        .compareToIgnoreCase(mCallbacks.getOperand(rhs).getName());
            }
        });

        mViews.setParsed(prettyExpression);
        mExpression.expression = prettyPrint.toString();
        saveOperand(mExpression);
    }

    private void handleTypeError(String prettyExpression, PrintTypeErrorVisitor findError) {
        SpannableString text = new SpannableString(prettyExpression);
        text.setSpan(mErrorSpan, findError.getErrorStart(), findError.getErrorEnd(),
                Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        mViews.setParsed(text);
        mViews.setError(findError.getErrorMessage());
        mViews.hideOperands();
    }

    private void handlePartialParse(Lexer lexer, Parser parser, String prettyExpression) {
        String lastToken = parser.getLastUnparsed().toString();
        Log.d("", lastToken);
        SpannableString text = new SpannableString(prettyExpression + " " + lastToken
                + lexer.getTextRemainder());
        text.setSpan(mErrorSpan, prettyExpression.length() + 1, prettyExpression.length()
                + lastToken.length() + 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        mViews.setParsed(text);
        mViews.setError(getActivity().getString(R.string.error_expected_operator));
        mViews.hideOperands();
    }

    private boolean handleLiteral(Expression expression) {
        if (expression instanceof StringExpression) {
            StringValue value = new StringValue();
            value.value = ((StringExpression)expression).getRawString();
            saveOperand(value);
            return true;
        } else if (expression instanceof IntegerExpression) {
            IntegerValue value = new IntegerValue();
            value.value = Integer.parseInt(((IntegerExpression)expression).getValueString());
            saveOperand(value);
            return true;
        } else if (expression instanceof FloatExpression) {
            FloatValue value = new FloatValue();
            value.value = Float.parseFloat(((FloatExpression)expression).getValueString());
            saveOperand(value);
            return true;
        } else if (expression instanceof BooleanExpression) {
            BooleanValue value = new BooleanValue();
            value.value = Boolean.parseBoolean(((BooleanExpression)expression).getValueString());
            saveOperand(value);
            return true;
        }
        return false;
    }

    private void handleParseException(String inputString, Lexer lexer, Parser parser,
            ParseException e) {
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
        mViews.hideOperands();
        return;
    }

    protected void showEditOperandDialogue(long id, int type, String title) {
        EditOperandDialogFragment dialog = EditOperandDialogFragment.newDialog(id, type, title);
        dialog.show(getChildFragmentManager(), "dialog_edit_operand");
    }

    public class ViewHolder extends TonicFragment.ViewHolder<LiteralOperand> {
        public TextView error;

        public EditText expression;

        private ListView operands;

        private View operandsTitle;

        private TextView parsed;

        private ProgramComponentAdapter<Operand> mAdapter;

        public ViewHolder(View view) {
            expression = (EditText)view.findViewById(R.id.text);
            error = (TextView)view.findViewById(R.id.error);
            parsed = (TextView)view.findViewById(R.id.parsed);
            operands = (ListView)view.findViewById(R.id.operands);
            operandsTitle = view.findViewById(R.id.operands_title);
        }

        public void hideError() {
            error.setVisibility(View.GONE);
        }

        public void hideOperands() {
            operandsTitle.setVisibility(View.GONE);
            if (mExpression != null) {
                mExpression.operands.clear();
                mAdapter.notifyDataSetChanged();
            }
        }

        private ProgramComponentAdapter<?> getOperandAdapterFromList() {
            ListAdapter listAdapter = operands.getAdapter();
            ProgramComponentAdapter<?> adapter;
            if (listAdapter instanceof ProgramComponentAdapter<?>) {
                adapter = (ProgramComponentAdapter<?>)listAdapter;
            } else {
                HeaderViewListAdapter wrapper = (HeaderViewListAdapter)listAdapter;
                adapter = (ProgramComponentAdapter<?>)wrapper.getWrappedAdapter();
            }
            return adapter;
        }

        public void hideParsed() {
            parsed.setVisibility(View.GONE);
        }

        @Override
        public void initViews() {
            expression.addTextChangedListener(mExpressionChangedListener);
            operands.setOnItemClickListener(mOperandItemClickListener);
            operands.setDivider(null);
            operands.setAdapter(null);
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
                /*
                 * Triggers expression parsing so we get the parsed display for
                 * free.
                 */
                expression.setText(operand.getValue());
            }
            updateViews(operand);
        }

        public void showOperands() {
            operandsTitle.setVisibility(View.VISIBLE);
        }

        public void updateViews(LiteralOperand operand) {
            if (operand instanceof ExpressionOperand) {
                showOperands();
                if (mAdapter == null) {
                    mAdapter = mCallbacks.getOperandAdapter(mObjectId,
                            ExperimentDesignerActivity.OPERAND_ACCESS_SCOPE_OPERAND);
                    operands.setAdapter(mAdapter);
                }
                mViews.error.setVisibility(View.GONE);
            } else {
                hideOperands();
            }
        }
    }
}
