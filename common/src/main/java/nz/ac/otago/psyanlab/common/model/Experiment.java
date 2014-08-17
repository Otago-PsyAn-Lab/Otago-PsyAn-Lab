/*
 Copyright (C) 2012, 2013, 2014 University of Otago, Tonic Artos <tonic.artos@gmail.com>

 Otago PsyAn Lab is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program. If not, see <http://www.gnu.org/licenses/>.

 In accordance with Section 7(b) of the GNU General Public License version 3,
 all legal notices and author attributions must be preserved.
 */

package nz.ac.otago.psyanlab.common.model;

import com.google.gson.annotations.Expose;

import android.annotation.SuppressLint;

import java.io.File;
import java.util.HashMap;

import nz.ac.otago.psyanlab.common.model.chansrc.Field;

public class Experiment {

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

    @Expose
    public HashMap<Long, Variable> variables;

    @Expose
    public HashMap<Long, Timer> timers;

    private File workingDirectory;

    @SuppressLint("UseSparseArrays")
    public Experiment() {
        // Using HashMap instead of LongSparseArray for compatibility with Gson.
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
        variables = new HashMap<Long, Variable>();
        timers = new HashMap<Long, Timer>();

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
