
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

    public static NameResolverFactory getEventNameFactory() {
        return new EventNameFactory();
    }

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
        public int getResId(int lookup) {
            switch (lookup) {
                case EVENT_SCENE_FINISH:
                    return R.string.event_scene_finish;
                case EVENT_SCENE_START:
                    return R.string.event_scene_start;
                case EVENT_SCENE_FINGER_MOTION:
                    return R.string.event_finger_motion;
                default:
                    return R.string.event_missing_string;
            }
        }
    }

    protected static class MethodNameFactory implements NameResolverFactory {
        @Override
        public int getResId(int lookup) {
            switch (lookup) {
                case METHOD_FINISH_SCENE:
                    return R.string.method_finish_scene;
                default:
                    return R.string.method_missing_string;
            }
        }
    }
}
