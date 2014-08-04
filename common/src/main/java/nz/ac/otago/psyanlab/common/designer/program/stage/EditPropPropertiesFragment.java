
package nz.ac.otago.psyanlab.common.designer.program.stage;

import nz.ac.otago.psyanlab.common.R;
import nz.ac.otago.psyanlab.common.model.Prop;
import nz.ac.otago.psyanlab.common.model.util.PALEPropProperty;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * A fragment that reflects upon the structure of a prop and dynamically creates
 * a UI to edit the properties.
 */
public class EditPropPropertiesFragment extends Fragment {
    private static final String ARG_PROP = "arg_prop";

    private static final String ARG_PROP_ID = "arg_prop_id";

    public static EditPropPropertiesFragment newInstance(int propId) {
        EditPropPropertiesFragment f = new EditPropPropertiesFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PROP_ID, propId);
        f.setArguments(args);

        return f;
    }

    public static EditPropPropertiesFragment newInstance(Prop prop) {
        EditPropPropertiesFragment f = new EditPropPropertiesFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_PROP, prop);
        f.setArguments(args);

        return f;
    }

    private StageCallbacks mCallbacks;

    private HashMap<String, Field> mFieldMap;

    private HashMap<String, ArrayList<String>> mGroupings = new HashMap<String, ArrayList<String>>();

    private Prop mProp;

    private HashMap<String, View> mViewMap;

    /**
     * Get the prop with properties set from the user entered values.
     * 
     * @return Prop with user values.
     */
    public Prop getConfiguredProp() {
        for (String name : mViewMap.keySet()) {
            Field field = mFieldMap.get(name);
            View view = mViewMap.get(name);

            Object value;

            if (view instanceof EditText) {
                String in = ((EditText)view).getText().toString();
                if (in == null) {
                    continue;
                }

                if (field.getType().isAssignableFrom(Integer.TYPE)) {
                    try {
                        value = Integer.valueOf(((EditText)view).getText().toString());
                    } catch (NumberFormatException e) {
                        // Bad input here can be ignored. Continue to next item.
                        continue;
                    }
                } else if (field.getType().isAssignableFrom(Float.TYPE)) {
                    try {
                        value = Float.valueOf(((EditText)view).getText().toString());
                    } catch (NumberFormatException e) {
                        // Bad input here can be ignored. Continue to next item.
                        continue;
                    }
                } else if (field.getType().isAssignableFrom(String.class)) {
                    value = ((EditText)view).getText().toString();
                } else {
                    continue;
                }
            } else {
                continue;
            }

            try {
                field.set(mProp, value);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        return mProp;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (!(activity instanceof StageCallbacks)) {
            throw new RuntimeException("Activity must implement fragment callbacks.");
        }
        mCallbacks = (StageCallbacks)activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle args = getArguments();
        if (args.containsKey(ARG_PROP)) {
            mProp = args.getParcelable(ARG_PROP);
        } else {
            mProp = mCallbacks.getProp(args.getInt(ARG_PROP_ID)).getProp();
        }

        mFieldMap = new HashMap<String, Field>();
        mViewMap = new HashMap<String, View>();

        // Run through the fields of the prop and build groups and mappings for
        // the fields and views to allow the user to change the property values.
        Field[] fields = mProp.getClass().getFields();
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            PALEPropProperty annotation = field.getAnnotation(PALEPropProperty.class);
            if (annotation != null) {
                String fieldName = annotation.value();
                View view;
                if (field.getType().isAssignableFrom(Integer.TYPE)) {
                    try {
                        view = newIntegerInputView(annotation.isSigned(), field.getInt(mProp));
                    } catch (IllegalArgumentException e) {
                        throw new RuntimeException("Should never get here.", e);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException("Should never get here.", e);
                    }
                } else if (field.getType().isAssignableFrom(Float.TYPE)) {
                    try {
                        view = newFloatInputView(annotation.isSigned(), field.getFloat(mProp));
                    } catch (IllegalArgumentException e) {
                        throw new RuntimeException("Should never get here.", e);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException("Should never get here.", e);
                    }
                } else if (field.getType().isAssignableFrom(String.class)) {
                    try {
                        view = newStringInputView((String)field.get(mProp));
                    } catch (IllegalArgumentException e) {
                        throw new RuntimeException("Should never get here.", e);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException("Should never get here.", e);
                    }
                } else {
                    continue;
                }
                mFieldMap.put(fieldName, field);
                mViewMap.put(fieldName, view);

                if (mGroupings.containsKey(annotation.group())) {
                    mGroupings.get(annotation.group()).add(fieldName);
                } else {
                    ArrayList<String> list = new ArrayList<String>();
                    list.add(fieldName);
                    mGroupings.put(annotation.group(), list);
                }
            }
        }

        // Initialise grid layout.
        GridLayout grid = new GridLayout(getActivity());
        grid.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        grid.setAlignmentMode(GridLayout.ALIGN_BOUNDS);
        grid.setColumnCount(2);
        grid.setUseDefaultMargins(true);

        // Run through groupings of properties adding the created views to the
        // GridLayout in sections.
        for (String group : mGroupings.keySet()) {
            if (!TextUtils.isEmpty(group) && !TextUtils.equals(group, "")) {
                TextView sectionBreak = (TextView)inflater.inflate(
                        R.layout.prop_property_section_break, grid, false);
                sectionBreak.setText(group);
                GridLayout.LayoutParams sectionBreakParams = new GridLayout.LayoutParams();
                sectionBreakParams.columnSpec = GridLayout.spec(0, 2);
                sectionBreak.setLayoutParams(sectionBreakParams);
                grid.addView(sectionBreak);
            }

            for (String name : mGroupings.get(group)) {
                TextView propertyLabel = (TextView)inflater.inflate(R.layout.prop_property_label,
                        grid, false);
                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                propertyLabel.setLayoutParams(params);
                propertyLabel.setText(getResources()
                        .getString(R.string.format_property_label, name));

                View propertyView = mViewMap.get(name);
                params = new GridLayout.LayoutParams();
                params.setGravity(Gravity.FILL_HORIZONTAL);
                propertyView.setLayoutParams(params);

                grid.addView(propertyLabel);
                grid.addView(propertyView);
            }
        }

        return grid;
    }

    /**
     * Creates an edit text entry field that allows the user to enter a
     * fractional number.
     * 
     * @param signedAnnotation
     * @return EditText
     */
    private View newFloatInputView(boolean isSigned, float f) {
        EditText view = new EditText(getActivity());
        if (isSigned) {
            view.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL
                    | InputType.TYPE_NUMBER_FLAG_SIGNED);
        } else {
            view.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        }
        view.setSingleLine();
        view.setText(String.valueOf(f));
        return view;
    }

    /**
     * Creates an edit text entry field that allows the user to enter an
     * integer.
     * 
     * @param i
     * @param posOnlyAnnotation
     * @return EditText
     */
    private View newIntegerInputView(boolean isSigned, int i) {
        EditText view = new EditText(getActivity());
        if (isSigned) {
            view.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_NORMAL
                    | InputType.TYPE_NUMBER_FLAG_SIGNED);
        } else {
            view.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_NORMAL);
        }
        view.setSingleLine();
        view.setText(String.valueOf(i));
        return view;
    }

    /**
     * Creates an edit text entry field that allows the user to enter a string.
     * 
     * @return EditText
     */
    private View newStringInputView(String s) {
        EditText view = new EditText(getActivity());
        view.setInputType(InputType.TYPE_CLASS_TEXT);
        view.setSingleLine();
        view.setText(s);
        return view;
    }
}
