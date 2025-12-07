package gitlet;

import java.io.File;
import java.io.Serializable;
import java.nio.file.Files;
import java.time.ZonedDateTime;
import java.util.TreeMap;
import java.util.TreeSet;

import static gitlet.Repository.COMMIT_DIR;
import static gitlet.Utils.*;

/**
 * Represents a gitlet commit object.
 *
 * @author A_Words
 */
public class Commit implements Serializable {
    /**
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    /**
     * The message of this Commit.
     */
    private final String message;
    private final ZonedDateTime timestamp;
    private final TreeMap<String, String> filesMappingBlobs;
    private String parentSha1;
    private String secondParentSha1;
    private final String sha1;

    public Commit(String message, ZonedDateTime timestamp) {
        this.message = message;
        this.timestamp = timestamp;
        filesMappingBlobs = new TreeMap<>();
        sha1 = sha1(message + timestamp.toString());
    }

    public Commit(String message, TreeMap<String, String> filesMappingBlobs, String parentSha1) {
        this.message = message;
        this.filesMappingBlobs = filesMappingBlobs;
        this.parentSha1 = parentSha1;
        timestamp = ZonedDateTime.now();
        sha1 = sha1(message + timestamp + filesMappingBlobs.toString() + parentSha1);
    }

    public Commit(
            String message,
            TreeMap<String, String> filesMappingBlobs,
            String parentSha1,
            String secondParentSha1) {
        this.message = message;
        this.filesMappingBlobs = filesMappingBlobs;
        this.parentSha1 = parentSha1;
        this.secondParentSha1 = secondParentSha1;
        timestamp = ZonedDateTime.now();
        sha1 = sha1(message + timestamp + filesMappingBlobs.toString() + parentSha1 + secondParentSha1);
    }

    public static Commit load(String sha1) {
        File file = join(COMMIT_DIR, sha1);
        if (!file.exists()) {
            return null;
        }
        return readObject(file, Commit.class);
    }

    public Commit parent() {
        if (parentSha1 == null) {
            return null;
        }
        return load(parentSha1);
    }

    public Commit secondParent() {
        if (secondParentSha1 == null) {
            return null;
        }
        return load(secondParentSha1);
    }

    public String getMessage() {
        return message;
    }

    public ZonedDateTime getTimestamp() {
        return timestamp;
    }

    public String getParentSha1() {
        return parentSha1;
    }

    public String getSecondParentSha1() {
        return secondParentSha1;
    }

    public String getSha1() {
        return sha1;
    }

    public void save() {
        File file = join(COMMIT_DIR, sha1);
        writeObject(file, this);
    }

    public File findFile(String fileName) {
        String blobSha1 = findFileSha1(fileName);
        if (blobSha1 == null) {
            return null;
        }
        File blobFile = Blob.load(blobSha1);
        try {
            if (Files.size(blobFile.toPath()) == 0) {
                return null;
            } else {
                return blobFile;
            }
        } catch (Exception e) {
            throw error("Internal error reading blob file.");
        }
    }

    public String findFileSha1(String fileName) {
        return filesMappingBlobs.get(fileName);
    }

    public TreeSet<String> getTrackedFiles() {
        return new TreeSet<>(filesMappingBlobs.keySet());
    }
}
