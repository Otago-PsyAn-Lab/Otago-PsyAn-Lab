
package nz.ac.otago.psyanlab.common.model.subject;

import com.google.gson.annotations.Expose;

import nz.ac.otago.psyanlab.common.model.Subject;

import java.util.ArrayList;

public abstract class SubjectDetailWithOptions extends Subject {
    @Expose
    public ArrayList<String> options;

    public SubjectDetailWithOptions(ArrayList<String> list) {
        if (options == null) {
            options = new ArrayList<String>();
        }
        options = list;
    }
}
