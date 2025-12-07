package gitlet;

import java.io.File;
import java.io.Serializable;
import java.nio.file.Files;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.HashSet;

import static gitlet.Repository.BLOB_DIR;
import static gitlet.Repository.COMMIT_DIR;
import static gitlet.Utils.*;

/** Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author A_Words
 */
public class Commit implements Serializable {
    /**
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    /** The message of this Commit. */
    private String message;
    private ZonedDateTime timestamp;
    HashMap<String, String> filesMappingBlobs;
    private String parentSha1;
    private String secondParentSha1;
    private String sha1;

    public Commit(String message, ZonedDateTime timestamp) {
        this.message = message;
        this.timestamp = timestamp;
        filesMappingBlobs = new HashMap<>();
        sha1 = sha1(message + timestamp.toString());
    }

    public Commit(String message, HashMap<String, String> filesMappingBlobs, String parentSha1) {
        this.message = message;
        this.filesMappingBlobs = filesMappingBlobs;
        this.parentSha1 = parentSha1;
        timestamp = ZonedDateTime.now();
        sha1 = sha1(message + timestamp + filesMappingBlobs.toString() + parentSha1);
    }

    public Commit(String message, HashMap<String, String> filesMappingBlobs, String parentSha1, String secondParentSha1) {
        this.message = message;
        this.filesMappingBlobs = filesMappingBlobs;
        this.parentSha1 = parentSha1;
        this.secondParentSha1 = secondParentSha1;
        timestamp = ZonedDateTime.now();
        sha1 = sha1(message + timestamp + filesMappingBlobs.toString() + parentSha1 + secondParentSha1);
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

    public static Commit load(String sha1) {
        File file = join(COMMIT_DIR, sha1);
        if (!file.exists()) {
            return null;
        }
        return readObject(file, Commit.class);
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

    public HashSet<String> getTrackedFiles() {
        return new HashSet<>(filesMappingBlobs.keySet());
    }
}
