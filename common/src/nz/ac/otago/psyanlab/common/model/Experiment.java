
package nz.ac.otago.psyanlab.common.model;

import com.google.gson.annotations.Expose;

import java.io.File;
import java.util.HashMap;

public class Experiment {
    @Expose
    public HashMap<Long, Action> actions;

    @Expose
    public HashMap<Long, Asset> assets;

    @Expose
    public String authors;

    @Expose
    public long dateCreated;

    @Expose
    public long lastModified;

    @Expose
    public String description;

    @Expose
    public HashMap<Long, Generator> generators;

    @Expose
    public LandingPage landingPage;

    @Expose
    public HashMap<Long, Loop> loops;

    @Expose
    public String name;

    @Expose
    public HashMap<Long, Operand> operands;

    @Expose
    public Program program;

    @Expose
    public HashMap<Long, Prop> props;

    @Expose
    public HashMap<Long, Rule> rules;

    @Expose
    public HashMap<Long, Scene> scenes;

    @Expose
    public TargetScreen screen;

    @Expose
    public int version;

    private File workingDirectory;

    public Experiment() {
        actions = new HashMap<Long, Action>();
        assets = new HashMap<Long, Asset>();
        generators = new HashMap<Long, Generator>();
        loops = new HashMap<Long, Loop>();
        operands = new HashMap<Long, Operand>();
        props = new HashMap<Long, Prop>();
        rules = new HashMap<Long, Rule>();
        scenes = new HashMap<Long, Scene>();

        landingPage = new LandingPage();
        program = new Program();
    }

    public File getWorkingDirectory() {
        return workingDirectory;
    }

    public void setWorkingDirectory(File workingDirectory) {
        this.workingDirectory = workingDirectory;
    }
}
