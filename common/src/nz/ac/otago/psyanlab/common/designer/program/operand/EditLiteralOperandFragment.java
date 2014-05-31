
package nz.ac.otago.psyanlab.common.designer.program.operand;

import nz.ac.otago.psyanlab.common.R;
import nz.ac.otago.psyanlab.common.designer.ExperimentDesignerActivity.OperandDataChangeListener;
import nz.ac.otago.psyanlab.common.designer.program.operand.ClearOperandDialogueFragment.OnClearListener;
import nz.ac.otago.psyanlab.common.designer.program.operand.EditOperandDialogFragment.OnDoneListener;
import nz.ac.otago.psyanlab.common.designer.program.operand.RenameOperandDialogueFragment.OnRenameListener;
import nz.ac.otago.psyanlab.common.designer.util.OperandListItemViewBinder;
import nz.ac.otago.psyanlab.common.designer.util.ProgramComponentAdapter;
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
import nz.ac.otago.psyanlab.common.model.operand.StubOperand;
import nz.ac.otago.psyanlab.common.model.operand.kind.ExpressionOperand;
import nz.ac.otago.psyanlab.common.model.operand.kind.LiteralOperand;
import nz.ac.otago.psyanlab.common.model.util.Type;
import nz.ac.otago.psyanlab.common.util.TonicFragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.BackgroundColorSpan;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

/**
 * A fragment that provides a UI to input a literal value or expression as an
 * operand.
 */
public class EditLiteralOperandFragment extends AbsOperandFragment implements
        OperandDataChangeListener, OnDoneListener, OnClearListener, OnRenameListener {
    private static final String ARG_OPERAND_MAP = "arg_operand_map";

    private final BackgroundColorSpan mErrorSpan = new BackgroundColorSpan(0xFFFF0000);

    private LiteralOperand mOperand;

    private HashMap<String, Long> mOperandMap = new HashMap<String, Long>();

    private ViewHolder mViews;

    protected TextWatcher mExpressionChangedListener = new TextWatcher() {
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

    protected ProgramComponentAdapter<Operand> mOperandAdapter;

    protected OnItemClickListener mOperandItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Operand operand = mCallbacks.getOperand(id);
            showEditOperandDialogue(
                    id,
                    operand.getType(),
                    getString(R.string.title_edit_operand,
                            Type.getTypeString(getActivity(), operand.getType())));
        }
    };

    @Override
    public Operand initReplacement(Operand oldOperand) {
        return new StubOperand(oldOperand.getName());
    }

    @Override
    public void onOperandCleared() {
        handleExpressionChange(mViews.expression.getText());
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo)item.getMenuInfo();

        int itemId = item.getItemId();
        if (itemId == R.id.menu_edit) {
            Operand operand = mCallbacks.getOperand(info.id);
            showEditOperandDialogue(
                    info.id,
                    operand.getType(),
                    getString(R.string.title_edit_operand,
                            Type.getTypeString(getActivity(), operand.getType())));
            return true;
        } else if (itemId == R.id.menu_clear) {
            showClearOperandDialogue(info.id);
            return true;
        } else if (itemId == R.id.menu_rename) {
            showRenameOperandDialogue(info.id);
            return true;
        }

        return super.onContextItemSelected(item);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (mObjectId == INVALID_ID) {
            throw new RuntimeException("Invalid operand id given.");
        }

        if (savedInstanceState != null) {
            Bundle operandMap = savedInstanceState.getBundle(ARG_OPERAND_MAP);
            String[] keys = operandMap.keySet().toArray(new String[operandMap.size()]);
            mOperandMap = new HashMap<String, Long>();
            for (int i = 0; i < operandMap.size(); i++) {
                mOperandMap.put(keys[i], operandMap.getLong(keys[i]));
            }
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.variable_options, menu);

        // Bloody annoying.
        OnMenuItemClickListener listener = new OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                onContextItemSelected(item);
                return false;
            }
        };

        for (int i = 0, n = menu.size(); i < n; i++)
            menu.getItem(i).setOnMenuItemClickListener(listener);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_literal_operand, container, false);
        ListView list = (ListView)view.findViewById(R.id.operands);
        list.addHeaderView(inflater.inflate(R.layout.header_edit_literal_operand, list,
                false));
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mCallbacks.removeOperandDataChangeListener(this);
    }

    @Override
    public void OnEditOperandDialogueDone() {
        handleExpressionChange(mViews.expression.getText());
    }

    @Override
    public void onOperandDataChange() {
        mOperandAdapter.notifyDataSetChanged();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle operandMap = new Bundle();
        String[] keys = mOperandMap.keySet().toArray(new String[mOperandMap.size()]);
        for (int i = 0; i < mOperandMap.size(); i++) {
            String key = keys[i];
            operandMap.putLong(key, mOperandMap.get(key));
        }

        outState.putBundle(ARG_OPERAND_MAP, operandMap);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Operand operand = mCallbacks.getOperand(mObjectId);
        if (operand instanceof ExpressionValue) {
            mOperand = (ExpressionValue)operand;
            final ExpressionValue expression = (ExpressionValue)operand;
            mOperandMap = new HashMap<String, Long>();
            for (Long operandId : expression.operands) {
                mOperandMap.put(mCallbacks.getOperand(operandId).getName(), operandId);
            }
        } else if (operand instanceof LiteralOperand) {
            mOperand = (LiteralOperand)operand;
        } else {
            mOperand = new ExpressionValue(operand);
        }

        mCallbacks.addOperandDataChangeListener(this);

        mViews = new ViewHolder(view);
        mViews.initViews();
        mViews.setViewValues(mOperand);

        registerForContextMenu(mViews.operands);
        mViews.operands.setOnCreateContextMenuListener(this);
    }

    @Override
    public void saveOperand() {
        if (!TextUtils.isEmpty(mOperand.getValue())) {
            mCallbacks.putOperand(mObjectId, (Operand)mOperand);
        }
    }

    private boolean handleLiteral(Expression expression) {
        if (expression instanceof StringExpression) {
            StringValue value = new StringValue((Operand)mOperand);
            value.value = ((StringExpression)expression).getRawString();
            mViews.updateViews(value);
            mOperand = value;
            return true;
        } else if (expression instanceof IntegerExpression) {
            IntegerValue value = new IntegerValue((Operand)mOperand);
            value.value = Integer.parseInt(((IntegerExpression)expression).getValueString());
            mViews.updateViews(value);
            mOperand = value;
            return true;
        } else if (expression instanceof FloatExpression) {
            FloatValue value = new FloatValue((Operand)mOperand);
            value.value = Float.parseFloat(((FloatExpression)expression).getValueString());
            mViews.updateViews(value);
            mOperand = value;
            return true;
        } else if (expression instanceof BooleanExpression) {
            BooleanValue value = new BooleanValue((Operand)mOperand);
            value.value = Boolean.parseBoolean(((BooleanExpression)expression).getValueString());
            mViews.updateViews(value);
            mOperand = value;
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

    private void handleTypeError(String prettyExpression, PrintTypeErrorVisitor findError) {
        SpannableString text = new SpannableString(prettyExpression);
        text.setSpan(mErrorSpan, findError.getErrorStart(), findError.getErrorEnd(),
                Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        mViews.setParsed(text);
        mViews.setError(findError.getErrorMessage());
        mViews.hideOperands();
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
        Expression input;

        try {
            input = parser.parseExpression();
        } catch (ParseException e) {
            handleParseException(inputString, lexer, parser, e);
            return;
        }

        PrintVisitor prettyPrint = new PrintVisitor();
        input.accept(prettyPrint);
        String prettyExpression = prettyPrint.toString();

        if (parser.areUnparsedTokens()) {
            handlePartialParse(lexer, parser, prettyExpression);
            return;
        }

        RefineTypeVisitor typeCheck = new RefineTypeVisitor(getActivity(), mCallbacks, mOperandMap,
                mOperandType);
        boolean wasError = false;
        try {
            input.accept(typeCheck);
        } catch (TypeException error) {
            wasError = true;
        }

        mOperandMap = typeCheck.getOperandMap();
        PrintTypeErrorVisitor findError = new PrintTypeErrorVisitor(typeCheck.getError());
        input.accept(findError);
        if (wasError) {
            handleTypeError(prettyExpression, findError);
            return;
        }

        if (handleLiteral(input)) {
            return;
        }

        HashMap<String, Long> operandMap = typeCheck.getOperandsMentioned();
        mOperand = new ExpressionValue((Operand)mOperand);
        final ExpressionValue expressionValue = (ExpressionValue)mOperand;
        expressionValue.operands.clear();
        expressionValue.operands.addAll(operandMap.values());
        Collections.sort(expressionValue.operands, new Comparator<Long>() {
            @Override
            public int compare(Long lhs, Long rhs) {
                return mCallbacks.getOperand(lhs).getName()
                        .compareToIgnoreCase(mCallbacks.getOperand(rhs).getName());
            }
        });

        mViews.setParsed(prettyExpression);
        expressionValue.expression = prettyPrint.toString();
        mViews.updateViews(mOperand);
    }

    protected void showClearOperandDialogue(long id) {
        ClearOperandDialogueFragment dialogue = ClearOperandDialogueFragment.newDialog(
                R.string.title_clear_variable, id);
        dialogue.setOnClearListener(this);
        dialogue.show(getChildFragmentManager(), "dialogue_clear_operand");
    }

    protected void showEditOperandDialogue(long id, int type, String title) {
        EditOperandDialogFragment dialogue = EditOperandDialogFragment.newDialog(mSceneId, id,
                type, title);
        dialogue.setOnDoneListener(this);
        dialogue.show(getChildFragmentManager(), "dialogue_edit_operand");
    }

    protected void showRenameOperandDialogue(long id) {
        RenameOperandDialogueFragment dialogue = RenameOperandDialogueFragment.newDialog(id);
        dialogue.setOnRenameListener(this);
        dialogue.show(getChildFragmentManager(), "dialogue_rename_operand");
    }

    public class ViewHolder extends TonicFragment.ViewHolder<LiteralOperand> {
        public TextView error;

        public EditText expression;

        private ListView operands;

        private View operandsTitle;

        private TextView parsed;

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
            if (mOperand instanceof ExpressionValue) {
                final ExpressionValue expression = (ExpressionValue)mOperand;
                expression.operands.clear();
                mOperandAdapter.notifyDataSetChanged();
            }
        }

        public void hideParsed() {
            parsed.setVisibility(View.GONE);
        }

        @Override
        public void initViews() {
            expression.addTextChangedListener(mExpressionChangedListener);
            operands.setOnItemClickListener(mOperandItemClickListener);
            operands.setDivider(null);

            mOperandAdapter = new ProgramComponentAdapter<Operand>(mCallbacks.getOperands(), null,
                    new OperandListItemViewBinder(getActivity(), mCallbacks));
            operands.setAdapter(mOperandAdapter);
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
                mOperandAdapter.setKeys(((ExpressionOperand)mOperand).getOperands());
                mOperandAdapter.notifyDataSetChanged();
                mViews.error.setVisibility(View.GONE);
            } else {
                hideOperands();
            }
        }
    }

    @Override
    public void onRename(String name, String oldName) {
        // Update operand map.
        long id = mOperandMap.get(oldName);
        mOperandMap.remove(oldName);
        mOperandMap.put(name, id);

        // Update expression.
        String expression = mViews.expression.getText().toString();
        String wordBreak = "([^\\p{L}\\p{N}])";
        String word = oldName;
        String matchOnly = "^" + word + "$";
        String matchBeginning = "^" + word + wordBreak;
        String matchEnd = wordBreak + word + "$";
        String matchMiddle = wordBreak + oldName + wordBreak;
        expression = expression.replaceAll(matchOnly, name);
        expression = expression.replaceAll(matchBeginning, name + "$1");
        expression = expression.replaceAll(matchEnd, "$1" + name);
        expression = expression.replaceAll(matchMiddle, "$1" + name + "$2");
        mViews.expression.setText(expression);
    }
}
