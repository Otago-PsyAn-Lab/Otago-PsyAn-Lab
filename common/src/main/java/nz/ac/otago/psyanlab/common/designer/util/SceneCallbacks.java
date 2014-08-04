
package nz.ac.otago.psyanlab.common.designer.util;

import nz.ac.otago.psyanlab.common.designer.ExperimentDesignerActivity.SceneDataChangeListener;
import nz.ac.otago.psyanlab.common.model.Scene;

public interface SceneCallbacks {
    long addScene(Scene scene);

    void addSceneDataChangeListener(SceneDataChangeListener listener);

    void deleteScene(long id);

    Scene getScene(long sceneId);

    ProgramComponentAdapter<Scene> getScenesAdapter(long loopId);

    void putScene(long id, Scene scene);

    void removeSceneDataChangeListener(SceneDataChangeListener listener);

    void startEditStage(long id);
}
