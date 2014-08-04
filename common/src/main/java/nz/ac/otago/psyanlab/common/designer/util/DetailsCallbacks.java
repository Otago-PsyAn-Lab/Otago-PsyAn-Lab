
package nz.ac.otago.psyanlab.common.designer.util;

public interface DetailsCallbacks {
    String getAuthors();

    String getDescription();

    String getName();

    int getVersion();

    void updateAuthors(String authors);

    void updateDescription(String description);

    void updateName(String name);

    void updateVersion(int version);
}
