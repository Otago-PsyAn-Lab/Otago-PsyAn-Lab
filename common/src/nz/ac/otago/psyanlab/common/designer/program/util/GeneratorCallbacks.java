
package nz.ac.otago.psyanlab.common.designer.program.util;

import nz.ac.otago.psyanlab.common.designer.ExperimentDesignerActivity.GeneratorDataChangeListener;
import nz.ac.otago.psyanlab.common.designer.ProgramComponentAdapter;
import nz.ac.otago.psyanlab.common.model.Generator;

public interface GeneratorCallbacks {
    void addGeneratorDataChangeListener(GeneratorDataChangeListener listener);

    long createGenerator(Generator generator);

    void deleteGenerator(long id);

    Generator getGenerator(long id);

    ProgramComponentAdapter<Generator> getGeneratorAdapter(long loopId);

    void removeGeneratorDataChangeListener(GeneratorDataChangeListener listener);

    void updateGenerator(long id, Generator generator);

}
