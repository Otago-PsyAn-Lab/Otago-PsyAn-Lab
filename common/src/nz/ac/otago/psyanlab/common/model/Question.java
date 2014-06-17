
package nz.ac.otago.psyanlab.common.model;

import com.google.gson.annotations.Expose;

import nz.ac.otago.psyanlab.common.R;
import nz.ac.otago.psyanlab.common.model.subject.Date;
import nz.ac.otago.psyanlab.common.model.subject.DateTime;
import nz.ac.otago.psyanlab.common.model.subject.Dropdown;
import nz.ac.otago.psyanlab.common.model.subject.MultiChoice;
import nz.ac.otago.psyanlab.common.model.subject.Number;
import nz.ac.otago.psyanlab.common.model.subject.SingleChoice;
import nz.ac.otago.psyanlab.common.model.subject.Text;
import nz.ac.otago.psyanlab.common.model.subject.Time;
import nz.ac.otago.psyanlab.common.model.subject.Toggle;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public abstract class Question {
    public static final int KIND_DATE = 0x01;

    public static final int KIND_DATE_TIME = 0x02;

    public static final int KIND_DROPDOWN = 0x03;

    public static final int KIND_MULTI_CHOICE = 0x04;

    public static final int KIND_NUMBER = 0x05;

    public static final int KIND_SINGLE_CHOICE = 0x06;

    public static final int KIND_TEXT = 0x07;

    public static final int KIND_TIME = 0x08;

    public static final int KIND_TOGGLE = 0x09;

    @SuppressLint("UseSparseArrays")
    public static HashMap<Integer, String> getKinds(Context context) {
        HashMap<Integer, String> kinds = new HashMap<Integer, String>();
        kinds.put(KIND_DATE, context.getString(R.string.label_subject_detail_date));
        kinds.put(KIND_DATE_TIME, context.getString(R.string.label_subject_detail_date_and_time));
        kinds.put(KIND_DROPDOWN, context.getString(R.string.label_subject_detail_dropdown));
        kinds.put(KIND_MULTI_CHOICE,
                context.getString(R.string.label_subject_detail_multiple_choice));
        kinds.put(KIND_NUMBER, context.getString(R.string.label_subject_detail_number));
        kinds.put(KIND_SINGLE_CHOICE,
                context.getString(R.string.label_subject_detail_single_choice));
        kinds.put(KIND_TEXT, context.getString(R.string.label_subject_detail_text));
        kinds.put(KIND_TIME, context.getString(R.string.label_subject_detail_time));
        kinds.put(KIND_TOGGLE, context.getString(R.string.label_subject_detail_toggle));
        return kinds;
    }

    public static Question getNewInstance(int kind) {
        switch (kind) {
            case Question.KIND_DATE:
                return new Date();
            case Question.KIND_DATE_TIME:
                return new DateTime();
            case Question.KIND_DROPDOWN:
                return new Dropdown();
            case Question.KIND_MULTI_CHOICE:
                return new MultiChoice();
            case Question.KIND_NUMBER:
                return new Number();
            case Question.KIND_SINGLE_CHOICE:
                return new SingleChoice();
            case Question.KIND_TEXT:
                return new Text();
            case Question.KIND_TIME:
                return new Time();
            case Question.KIND_TOGGLE:
                return new Toggle();

            default:
                throw new RuntimeException("Unknown question kind " + kind);
        }
    }

    public static Question getNewInstance(Question q) {
        switch (q.getKind()) {
            case Question.KIND_DATE:
                return new Date(q);
            case Question.KIND_DATE_TIME:
                return new DateTime(q);
            case Question.KIND_DROPDOWN:
                return new Dropdown(q);
            case Question.KIND_MULTI_CHOICE:
                return new MultiChoice(q);
            case Question.KIND_NUMBER:
                return new Number(q);
            case Question.KIND_SINGLE_CHOICE:
                return new SingleChoice(q);
            case Question.KIND_TEXT:
                return new Text(q);
            case Question.KIND_TIME:
                return new Time(q);
            case Question.KIND_TOGGLE:
                return new Toggle(q);

            default:
                throw new RuntimeException("Unknown question kind " + q.getKind());
        }
    }

    @Expose
    public String name;

    @Expose
    public boolean required;

    @Expose
    public String text;

    @Expose
    public String hint;

    @Expose
    public ArrayList<String> options;

    public Question() {
        options = new ArrayList<String>();
    }

    public Question(Question q) {
        name = q.name;
        required = q.required;
        text = q.text;
        hint = q.hint;
        options = q.options;
    }

    public abstract int getKind();

    public abstract int getKindResId();

    /**
     * Gets a view for the label of this subject detail UI.
     * 
     * @return Label view component.
     */
    public View getLabelView(Context context) {
        TextView v = new TextView(context);
        v.setText(getLabelText(context));
        return v;
    }

    private CharSequence getLabelText(Context context) {
        if (required) {
            return text + context.getResources().getString(R.string.symbol_required);
        }
        return text;
    }

    public static class Comparator implements java.util.Comparator<Question> {
        @Override
        public int compare(Question lhs, Question rhs) {
            if (lhs.name == null || rhs.name == null) {
                return 0;
            }

            return lhs.name.compareToIgnoreCase(rhs.name);
        }
    }
}
