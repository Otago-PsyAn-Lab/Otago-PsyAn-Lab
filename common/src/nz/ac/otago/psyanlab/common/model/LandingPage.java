
package nz.ac.otago.psyanlab.common.model;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;

public class LandingPage {
    @Expose
    public String title;

    @Expose
    public String introduction;

    @Expose
    public ArrayList<Subject> subjectDetails;

    public LandingPage() {
        subjectDetails = new ArrayList<Subject>();
    }
}
