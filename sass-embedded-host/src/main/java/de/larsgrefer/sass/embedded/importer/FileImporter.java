package de.larsgrefer.sass.embedded.importer;

import java.io.File;

public abstract class FileImporter extends Importer {

    public abstract File handleImport(String url);
}
