package gitlet;

import java.io.*;
import java.nio.file.Files;

import static gitlet.Repository.BLOB_DIR;
import static gitlet.Utils.*;

public class Blob {
    private File file;
    private String sha1;

    public Blob(File file) {
        this.file = file;
        this.sha1 = sha1(readContentsAsString(file) + file.getName());
    }

    public void save() throws IOException {
        File blobFile = join(BLOB_DIR, this.sha1);
        Files.copy(file.toPath(), blobFile.toPath());
    }

    public static File load(String sha1) {
        return join(BLOB_DIR, sha1);
    }

    public String getSha1() {
        return sha1;
    }
}
