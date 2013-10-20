
package nz.ac.otago.psyanlab.common.model;

import com.google.gson.annotations.Expose;

import android.support.v4.util.LongSparseArray;

import java.io.File;

public class Experiment {
    @Expose
    public LongSparseArray<Action> actions;

    @Expose
    public LongSparseArray<Asset> assets;

    @Expose
    public String authors;

    @Expose
    public long dateCreated;

    @Expose
    public String description;

    @Expose
    public LongSparseArray<Generator> generators;

    @Expose
    public LandingPage landingPage;

    @Expose
    public LongSparseArray<Loop> loops;

    @Expose
    public String name;

    @Expose
    public Program program;

    @Expose
    public LongSparseArray<Prop> props;

    @Expose
    public LongSparseArray<Rule> rules;

    @Expose
    public LongSparseArray<Scene> scenes;

    @Expose
    public int version;

    private File workingDirectory;

    public Experiment() {
        actions = new LongSparseArray<Action>();
        assets = new LongSparseArray<Asset>();
        generators = new LongSparseArray<Generator>();
        loops = new LongSparseArray<Loop>();
        props = new LongSparseArray<Prop>();
        rules = new LongSparseArray<Rule>();
        scenes = new LongSparseArray<Scene>();

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
