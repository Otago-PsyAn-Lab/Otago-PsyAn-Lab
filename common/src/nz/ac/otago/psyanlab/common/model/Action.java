
package nz.ac.otago.psyanlab.common.model;

import com.google.gson.annotations.Expose;

import nz.ac.otago.psyanlab.common.model.util.OperandHolder;

import java.util.ArrayList;

public class Action implements OperandHolder {
    @Expose
    public int actionMethod;

    @Expose
    public ExperimentObjectReference actionObject;

    @Expose
    public String method;

    @Expose
    public String name;

    @Expose
    public String object;

    @Expose
    public ArrayList<Long> operands;

    public Action() {
        operands = new ArrayList<Long>();
    }

    @Override
    public ArrayList<Long> getOperands() {
        return operands;
    }
}
