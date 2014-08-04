package nz.ac.otago.psyanlab.common.model;

import com.google.gson.annotations.Expose;

import java.io.File;
import java.util.HashMap;

import nz.ac.otago.psyanlab.common.model.chansrc.Field;

public class Experiment {
    private File workingDirectory;

    @Expose
    public HashMap<Long, Action> actions;

    @Expose
    public HashMap<Long, Asset> assets;

    @Expose
    public HashMap<Long, Source> sources;

    @Expose
    public String authors;

    @Expose
    public HashMap<Long, DataChannel> dataChannels;

    @Expose
    public long dateCreated;

    @Expose
    public String description;

    @Expose
    public HashMap<Long, Generator> generators;

    @Expose
    public LandingPage landingPage;

    @Expose
    public long lastModified;

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
    public HashMap<Long, Question> questions;

    @Expose
    public HashMap<Long, Rule> rules;

    @Expose
    public HashMap<Long, Scene> scenes;

    @Expose
    public TargetScreen screen;

    @Expose
    public int version;

    public Experiment() {
        actions = new HashMap<Long, Action>();
        assets = new HashMap<Long, Asset>();
        generators = new HashMap<Long, Generator>();
        loops = new HashMap<Long, Loop>();
        operands = new HashMap<Long, Operand>();
        props = new HashMap<Long, Prop>();
        rules = new HashMap<Long, Rule>();
        scenes = new HashMap<Long, Scene>();
        dataChannels = new HashMap<Long, DataChannel>();
        questions = new HashMap<Long, Question>();
        sources = new HashMap<Long, Source>();

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
