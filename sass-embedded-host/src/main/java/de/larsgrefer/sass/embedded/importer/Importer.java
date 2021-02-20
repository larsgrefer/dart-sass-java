package de.larsgrefer.sass.embedded.importer;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public abstract class Importer {

    private static int idCounter = 0;

    @Getter
    @EqualsAndHashCode.Include
    private final int id = idCounter++;

    protected void checkId(int id) {
        if (id != getId()) {
            throw new IllegalArgumentException();
        }
    }
}
