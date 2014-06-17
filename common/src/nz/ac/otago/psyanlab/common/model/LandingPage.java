
package nz.ac.otago.psyanlab.common.model;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;

public class LandingPage {
    @Expose
    public String introduction;

    @Expose
    public ArrayList<Long> questions;

    @Expose
    public String title;

    public LandingPage() {
        questions = new ArrayList<Long>();
    }
}
