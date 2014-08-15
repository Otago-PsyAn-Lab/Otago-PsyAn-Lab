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

import nz.ac.otago.psyanlab.common.R;
import nz.ac.otago.psyanlab.common.model.util.EventData;
import nz.ac.otago.psyanlab.common.model.util.MethodId;
import nz.ac.otago.psyanlab.common.model.util.NameResolverFactory;

import android.content.Context;

import java.util.ArrayList;

public class Scene extends ExperimentObject {
    public static final int ORIENTATION_LANDSCAPE = 0;

    public static final int ORIENTATION_PORTRAIT = 1;

    protected static final int EVENT_SCENE_FINGER_MOTION = 0x01;

    protected static final int EVENT_SCENE_FINISH = 0x03;

    protected static final int EVENT_SCENE_START = 0x02;

    protected static final int METHOD_FINISH_SCENE = 0x01;

    @Expose
    public String name;

    @Expose
    public int orientation = -1;

    @Expose
    public ArrayList<Long> props;

    @Expose
    public ArrayList<Long> rules;

    @Expose
    public int stageHeight = -1;

    @Expose
    public int stageWidth = -1;

    public Scene() {
        props = new ArrayList<Long>();
        rules = new ArrayList<Long>();
    }

    public static NameResolverFactory getEventNameFactory() {
        return new EventNameFactory();
    }

    @EventData(id = EVENT_SCENE_FINGER_MOTION, type = EventData.EVENT_TOUCH_MOTION)
    public void eventSceneFingerMotion() {
    }

    @EventData(id = EVENT_SCENE_FINISH, type = EventData.EVENT_SCENE_FINISH)
    public void eventSceneFinish() {
    }

    @EventData(id = EVENT_SCENE_START, type = EventData.EVENT_SCENE_START)
    public void eventSceneStart() {
    }

    @Override
    public String getExperimentObjectName(Context context) {
        return context.getString(R.string.format_scene_class_name, name);
    }

    @Override
    public NameResolverFactory getMethodNameFactory() {
        return new MethodNameFactory();
    }

    @Override
    public int kind() {
        return ExperimentObject.KIND_SCENE;
    }

    @MethodId(METHOD_FINISH_SCENE)
    public void stubFinishScene() {
    }

    protected static class EventNameFactory implements NameResolverFactory {
        @Override
        public String getName(Context context, int lookup) {
            switch (lookup) {
                case EVENT_SCENE_FINISH:
                    return context.getString(R.string.event_scene_finish);
                case EVENT_SCENE_START:
                    return context.getString(R.string.event_scene_start);
                case EVENT_SCENE_FINGER_MOTION:
                    return context.getString(R.string.event_finger_motion);
                default:
                    return context.getString(R.string.event_missing_string);
            }
        }
    }

    protected static class MethodNameFactory implements NameResolverFactory {
        @Override
        public String getName(Context context, int lookup) {
            switch (lookup) {
                case METHOD_FINISH_SCENE:
                    return context.getString(R.string.method_finish_scene);
                default:
                    return context.getString(R.string.method_missing_string);
            }
        }
    }
}
