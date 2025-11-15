package gitlet;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import static gitlet.Utils.*;
import static java.time.ZoneOffset.UTC;

// TODO: any imports you need here

/** Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Repository {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    /** The commit directory. */
    public static final File COMMIT_DIR = join(GITLET_DIR, "commits");
    /** The staging directory. */
    public static final File STAGING_DIR = join(GITLET_DIR, "staging");
    /** The blob directory. */
    public static final File BLOB_DIR = join(GITLET_DIR, "blobs");

    private static final File CURRENT_COMMIT_SHA1_FILE = join(COMMIT_DIR, "currentCommitSha1");

    public static void init() throws IOException {
        if (isRepositoryExists()) {
            throw error("A Gitlet version-control system already exists in the current directory.");
        }
        List<File> dirs = List.of(GITLET_DIR, COMMIT_DIR, STAGING_DIR, BLOB_DIR);
        for (File dir : dirs) {
            Files.createDirectory(dir.toPath());
        }
        Commit initial = new Commit("initial commit", ZonedDateTime.of(1970, 1, 1, 0, 0, 0, 0, UTC));
        initial.save();
        setCurrentCommitSha1(initial.getSha1());
        // 加入分支系统, master 分支, 一个分支一个文件夹，Commit 存第二父母分支名
    }

    private static boolean isRepositoryExists() {
        return GITLET_DIR.exists() && GITLET_DIR.isDirectory();
    }

    private static void checkDir() {
        if (!isRepositoryExists()) {
            message("Not in an initialized Gitlet directory.");
            System.exit(0);
        }
    }

    public static void stagingFileByName(String fileName) throws IOException {
        File file = join(CWD, fileName);
        stagingFile(file);
    }

    public static void stagingFile(File file) throws IOException {
        checkDir();
        if (!file.exists()) {
            throw error("File does not exist.");
        }
        String fileString = readContentsAsString(file);
        String fileSha1 = sha1(fileString);
        Commit currentCommit = getCurrentCommit();
        File fileInCurrentCommit = currentCommit.findFile(file.getName());
        File stagingDirFile = join(STAGING_DIR, file.getName());
        if (fileInCurrentCommit != null) {
            String fileInCurrentCommitSha1 = sha1(readContentsAsString(fileInCurrentCommit));
            if (fileSha1.equals(fileInCurrentCommitSha1)) {
                if (stagingDirFile.exists()) {
                    Files.delete(stagingDirFile.toPath());
                }
                return;
            }
        }
        writeContents(stagingDirFile, fileString);
    }

    public static void stagingToCommit(String message) throws IOException {
        checkDir();
        if (STAGING_DIR.list().length == 0) {
            message("No changes added to the commit.");
            return;
        }
        if (message == null || message.isEmpty()) {
            throw error("Please enter a commit message.");
        }
        HashMap<String, String> filesMappingBlobs = new HashMap<>();
        for (File file : STAGING_DIR.listFiles()) {
            Blob blob = new Blob(file);
            blob.save();
            filesMappingBlobs.put(file.getName(), blob.getSha1());
        }
        Commit commit = new Commit(message, filesMappingBlobs, getCurrentCommitSha1());
        commit.save();
        setCurrentCommitSha1(commit.getSha1());
        for (File file : STAGING_DIR.listFiles()) {
            Files.delete(file.toPath());
        }
    }

    public static void log() {
        checkDir();
        helperLog(getCurrentCommit());
    }

    private static void helperLog(Commit commit) {
        if (commit == null) {
            return;
        }
        message("===");
        message("commit " + commit.getSha1());
        if (commit.secondParent() != null) {
            message("Merge: " + commit.getParentSha1().substring(0,7) + commit.getSecondParentSha1().substring(0, 7));
        }
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("E MMM d HH:mm:ss yyyy Z", Locale.ENGLISH);
        message("Date: " + commit.getTimestamp().withZoneSameInstant(ZoneId.systemDefault()).format(dateTimeFormatter));
        message(commit.getMessage());
        message("");
        helperLog(commit.parent());
    }

    private static Commit getCurrentCommit() {
        return Commit.load(getCurrentCommitSha1());
    }

    private static String getCurrentCommitSha1() {
        return readContentsAsString(CURRENT_COMMIT_SHA1_FILE);
    }

    private static void setCurrentCommitSha1(String sha1) {
        writeContents(CURRENT_COMMIT_SHA1_FILE, sha1);
    }

    public static void checkout(String fileName) throws IOException {
        checkout(getCurrentCommitSha1(), fileName);
    }

    public static void checkout(String commitSha1, String fileName) throws IOException {
        checkDir();
        if (commitSha1.length() < 40) {
            String fullCommitSha1 = findFileByPrefix(COMMIT_DIR, commitSha1);
            commitSha1 = fullCommitSha1;
        }
        Commit commit = Commit.load(commitSha1);
        if (commit == null) {
            throw error("No commit with that id exists.");
        }
        File commitFile = commit.findFile(fileName);
        if (commitFile == null) {
            throw error("File does not exist in that commit.");
        }
        File workingFile = join(CWD, fileName);
        Files.copy(commitFile.toPath(), workingFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
    }

    private static String findFileByPrefix(File dir, String prefix) {
        String fileName = null;
        for (File file : Objects.requireNonNull(dir.listFiles())) {
            if (file.getName().startsWith(prefix)) {
                if (fileName != null) {
                    return null;
                }
                fileName = file.getName();
            }
        }
        return fileName;
    }
}
