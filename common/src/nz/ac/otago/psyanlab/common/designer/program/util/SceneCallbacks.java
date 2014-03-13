
package nz.ac.otago.psyanlab.common.designer.program.util;

import nz.ac.otago.psyanlab.common.designer.ProgramComponentAdapter;
import nz.ac.otago.psyanlab.common.designer.ExperimentDesignerActivity.SceneDataChangeListener;
import nz.ac.otago.psyanlab.common.model.Scene;

public interface SceneCallbacks {

    Scene getScene(long sceneId);

    ProgramComponentAdapter<Scene> getScenesAdapter(long loopId);

    long createScene(Scene scene);

    void deleteScene(long id);

    void addSceneDataChangeListener(SceneDataChangeListener listener);

    void removeSceneDataChangeListener(SceneDataChangeListener listener);

    void updateScene(long id, Scene scene);

}
