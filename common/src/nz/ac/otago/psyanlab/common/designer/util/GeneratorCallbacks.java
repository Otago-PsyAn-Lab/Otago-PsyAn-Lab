
package nz.ac.otago.psyanlab.common.designer.util;

import nz.ac.otago.psyanlab.common.designer.ExperimentDesignerActivity.GeneratorDataChangeListener;
import nz.ac.otago.psyanlab.common.model.Generator;

public interface GeneratorCallbacks {
    void addGeneratorDataChangeListener(GeneratorDataChangeListener listener);

    long addGenerator(Generator generator);

    void deleteGenerator(long id);

    Generator getGenerator(long id);

    ProgramComponentAdapter<Generator> getGeneratorAdapter(long loopId);

    void removeGeneratorDataChangeListener(GeneratorDataChangeListener listener);

    void putGenerator(long id, Generator generator);

}
