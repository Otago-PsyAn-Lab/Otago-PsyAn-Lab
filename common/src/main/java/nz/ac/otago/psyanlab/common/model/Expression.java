
package nz.ac.otago.psyanlab.common.model;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;

public class Expression {
    @Expose
    public String formula;
    
    @Expose
    public ArrayList<Long> operands;

    @Expose
    // Index matched with the operand it names. The operand name matches that
    // written in the formula.
    public ArrayList<String> operandNames;

    public Expression() {
        operands = new ArrayList<Long>();
        operandNames = new ArrayList<String>();
    }
}
