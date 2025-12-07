package gitlet;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static gitlet.Utils.*;
import static java.time.ZoneOffset.UTC;

/** Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author A_Words
 */
public class Repository {
    /**
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

    private static final File BRANCHES_FILE = join(GITLET_DIR, "branches");
    private static final File CURRENT_BRANCH_FILE = join(GITLET_DIR, "currentBranch");

    public static void init() {
        if (isRepositoryExists()) {
            message("A Gitlet version-control system already exists in the current directory.");
            System.exit(0);
        }

        List<File> dirs = List.of(GITLET_DIR, COMMIT_DIR, STAGING_DIR, BLOB_DIR);
        for (File dir : dirs) {
            try {
                Files.createDirectory(dir.toPath());
            } catch (IOException e) {
                throw error("Unable to create directory.");
            }
        }
        HashMap<String, String> branchMappingCommit = new HashMap<>();
        writeObject(BRANCHES_FILE, branchMappingCommit);

        Commit initial = new Commit("initial commit", ZonedDateTime.of(1970, 1, 1, 0, 0, 0, 0, UTC));
        initial.save();
        setBranch("master", initial.getSha1());
        switchBranch("master");
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

    public static void stagingFileByName(String fileName) {
        File file = join(CWD, fileName);
        stagingFile(file);
    }

    public static void stagingFile(File file) {
        checkDir();
        if (!file.exists()) {
            message("File does not exist.");
            System.exit(0);
        }

        String fileSha1 = sha1(readContentsAsString(file));
        File fileInCurrentCommit = getCurrentCommit().findFile(file.getName());
        File stagingDirFile = join(STAGING_DIR, file.getName());
        if (fileInCurrentCommit != null) {
            String fileInCurrentCommitSha1 = sha1(readContentsAsString(fileInCurrentCommit));
            if (fileSha1.equals(fileInCurrentCommitSha1)) {
                if (stagingDirFile.exists()) {
                    try {
                        Files.delete(stagingDirFile.toPath());
                    } catch (IOException e) {
                        throw error("Unable to delete staging file.");
                    }
                }
                return;
            }
        }
        try {
            Files.copy(file.toPath(), stagingDirFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw error("Unable to copy staging file.");
        }
    }

    public static void stagingToCommit(String message) {
        checkDir();
        if (Objects.requireNonNull(STAGING_DIR.list()).length == 0) {
            message("No changes added to the commit.");
            System.exit(0);
        }
        if (message == null || message.isEmpty()) {
            message("Please enter a commit message.");
            System.exit(0);
        }

        // 先继承父提交的所有文件映射
        Commit parentCommit = getCurrentCommit();
        HashMap<String, String> filesMappingBlobs = new HashMap<>();
        if (parentCommit != null) {
            for (String fileName : parentCommit.getTrackedFiles()) {
                String blobSha1 = parentCommit.findFileSha1(fileName);
                if (blobSha1 != null) {
                    filesMappingBlobs.put(fileName, blobSha1);
                }
            }
        }

        // 处理暂存区的文件（添加/修改/删除）
        for (File file : Objects.requireNonNull(STAGING_DIR.listFiles())) {
            try {
                if (Files.size(file.toPath()) == 0) {
                    // 空文件表示删除标记
                    filesMappingBlobs.remove(file.getName());
                } else {
                    // 添加或修改文件
                    Blob blob = new Blob(file);
                    blob.save();
                    filesMappingBlobs.put(file.getName(), blob.getSha1());
                }
            } catch (IOException e) {
                throw error("Unable to read staging file.");
            }
        }
        Commit commit = new Commit(message, filesMappingBlobs, getCurrentCommitSha1());
        commit.save();
        setCurrentCommitSha1(commit.getSha1());
        for (File file : Objects.requireNonNull(STAGING_DIR.listFiles())) {
            try {
                Files.delete(file.toPath());
            } catch (IOException e) {
                throw error("Unable to delete staging file.");
            }
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
        commitLog(commit);
        helperLog(commit.parent());
    }

    public static void globalLog() {
        checkDir();
        for (File file : Objects.requireNonNull(COMMIT_DIR.listFiles())) {
            Commit commit = Commit.load(file.getName());
            if (commit == null) {
                return;
            }
            commitLog(commit);
        }
    }

    private static void commitLog(Commit commit) {
        message("===");
        message("commit " + commit.getSha1());
        if (commit.secondParent() != null) {
            message("Merge: " + commit.getParentSha1().substring(0, 7) + " " + commit.getSecondParentSha1().substring(0, 7));
        }
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("E MMM d HH:mm:ss yyyy Z", Locale.ENGLISH);
        message("Date: " + commit.getTimestamp().withZoneSameInstant(ZoneId.systemDefault()).format(dateTimeFormatter));
        message(commit.getMessage());
        message("");
    }

    private static Commit getCurrentCommit() {
        return Commit.load(getCurrentCommitSha1());
    }

    private static String getCurrentCommitSha1() {
        return getBranch(getCurrentBranchName());
    }

    private static void setCurrentCommitSha1(String sha1) {
        setBranch(getCurrentBranchName(), sha1);
    }

    private static HashSet<String> getCurrentFileSet() {
        return new HashSet<>(Objects.requireNonNull(plainFilenamesIn(CWD)));
    }

    public static void checkout(String fileName) {
        checkout(getCurrentCommitSha1(), fileName);
    }

    public static void checkout(String commitSha1, String fileName) {
        checkDir();
        if (commitSha1.length() < 40) {
            if (findFileByPrefix(COMMIT_DIR, commitSha1) != null) {
                commitSha1 = findFileByPrefix(COMMIT_DIR, commitSha1);
            }
        }
        Commit commit = Commit.load(commitSha1);
        if (commit == null) {
            message("No commit with that id exists.");
            System.exit(0);
        }
        File commitFile = commit.findFile(fileName);
        if (commitFile == null) {
            message("File does not exist in that commit.");
            System.exit(0);
        }
        File workingFile = join(CWD, fileName);
        try {
            Files.copy(commitFile.toPath(), workingFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw error("Unable to copy commit file.");
        }
    }

    public static void checkoutBranch(String branchName) {
        checkDir();
        if (!getBranchSet().contains(branchName)) {
            message("No such branch exists.");
            System.exit(0);
        }
        if (branchName.equals(getCurrentBranchName())) {
            message("No need to checkout the current branch.");
            System.exit(0);
        }

        checkoutCommit(getBranch(branchName));
        switchBranch(branchName);
    }

    public static void reset(String commitSha1) {
        checkDir();
        if (commitSha1.length() < 40) {
            if (findFileByPrefix(COMMIT_DIR, commitSha1) != null) {
                commitSha1 = findFileByPrefix(COMMIT_DIR, commitSha1);
            }
        }
        if (Commit.load(commitSha1) == null) {
            message("No commit with that id exists.");
            System.exit(0);
        }

        checkoutCommit(commitSha1);
        setCurrentCommitSha1(commitSha1);
    }

    private static void checkoutCommit(String commitSha1) {
        Commit commit = Commit.load(commitSha1);
        if (commit == null) {
            message("No commit with that id exists.");
            System.exit(0);
        }

        HashSet<String> currentFileSet = getCurrentFileSet();
        HashSet<String> currentTrackedFiles = getCurrentCommit().getTrackedFiles();
        HashSet<String> targetTrackedFiles = commit.getTrackedFiles();

        for (String fileName : targetTrackedFiles) {
            if (currentFileSet.contains(fileName) && !currentTrackedFiles.contains(fileName)) {
                message("There is an untracked file in the way; delete it, or add and commit it first.");
                System.exit(0);
            }
            checkout(commitSha1, fileName);
        }

        for (String fileName : currentTrackedFiles) {
            if (!targetTrackedFiles.contains(fileName)) {
                File file = join(CWD, fileName);
                try {
                    Files.deleteIfExists(file.toPath());
                } catch (IOException e) {
                    throw error("Unable to delete file.");
                }
            }
        }

        for (File file : Objects.requireNonNull(STAGING_DIR.listFiles())) {
            try {
                Files.delete(file.toPath());
            } catch (IOException e) {
                throw error("Unable to delete staging file.");
            }
        }
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

    public static void rmFileByName(String fileName) {
        checkDir();
        File file = join(CWD, fileName);
        rmFile(file);
    }

    private static void rmFile(File file) {
        File stagingDirFile = join(STAGING_DIR, file.getName());
        Commit currentCommit = getCurrentCommit();
        File fileInCurrentCommit = currentCommit.findFile(file.getName());
        if (!stagingDirFile.exists() && fileInCurrentCommit == null) {
            message("No reason to remove the file.");
            System.exit(0);
        }
        if (stagingDirFile.exists()) {
            try {
                Files.delete(stagingDirFile.toPath());
            } catch (IOException e) {
                throw error("Unable to delete staging file.");
            }
        }
        if (fileInCurrentCommit != null) {
            try {
                stagingDirFile.createNewFile();
            } catch (IOException e) {
                throw error("Unable to create staging file.");
            }
            try {
                Files.deleteIfExists(file.toPath());
            } catch (IOException e) {
                throw error("Unable to delete file from the working directory.");
            }
        }
    }

    public static void createBranch(String branchName) {
        checkDir();
        if (getBranchSet().contains(branchName)) {
            message("A branch with that name already exists.");
            System.exit(0);
        }
        setBranch(branchName, getCurrentCommitSha1());
    }

    public static void removeBranch(String branchName) {
        checkDir();
        if (!getBranchSet().contains(branchName)) {
            message("A branch with that name does not exist.");
            System.exit(0);
        }
        if (getCurrentBranchName().equals(branchName)) {
            message("Cannot remove the current branch.");
            System.exit(0);
        }
        HashMap<String, String> branchMappingCommit = getBranchMappingCommit();
        branchMappingCommit.remove(branchName);
        setBranchMappingCommit(branchMappingCommit);
    }

    private static void setBranch(String branchName, String commitSha1) {
        HashMap<String, String> branchMappingCommit = getBranchMappingCommit();
        branchMappingCommit.put(branchName, commitSha1);
        setBranchMappingCommit(branchMappingCommit);
    }

    private static void switchBranch(String branchName) {
        if (getBranchSet().contains(branchName)) {
            writeContents(CURRENT_BRANCH_FILE, branchName);
        }
        else {
            message("No such branch exists.");
            System.exit(0);
        }
    }

    private static String getCurrentBranchName() {
        return readContentsAsString(CURRENT_BRANCH_FILE);
    }

    private static String getBranch(String branchName) {
        return getBranchMappingCommit().get(branchName);
    }

    private static Set<String> getBranchSet() {
        return getBranchMappingCommit().keySet();
    }

    private static HashMap<String, String> getBranchMappingCommit() {
        return readObject(BRANCHES_FILE, HashMap.class);
    }

    private static void setBranchMappingCommit(HashMap<String, String> branchMappingCommit) {
        writeObject(BRANCHES_FILE, branchMappingCommit);
    }

    public static void find(String commitMessage) {
        checkDir();
        boolean isOutput = false;
        for (File file : Objects.requireNonNull(COMMIT_DIR.listFiles())) {
            Commit commit = Commit.load(file.getName());
            if (commit == null) {
                return;
            }
            if (commit.getMessage().equals(commitMessage)) {
                message(commit.getSha1());
                isOutput = true;
            }
        }
        if (!isOutput) {
            message("Found no commit with that message.");
        }
    }

    public static void status() {
        checkDir();
        message("=== Branches ===");
        List<String> sortedBranches = new ArrayList<>(getBranchSet());
        Collections.sort(sortedBranches);
        for (String branchName : sortedBranches) {
            if (branchName.equals(getCurrentBranchName())) {
                message("*" + branchName);
            } else {
                message(branchName);
            }
        }
        message("");

        List<String> stagedFilesList = new ArrayList<>();
        List<String> removedFilesList = new ArrayList<>();
        for (File file : Objects.requireNonNull(STAGING_DIR.listFiles())) {
            try {
                if (Files.size(file.toPath()) == 0) {
                    removedFilesList.add(file.getName());
                } else {
                    stagedFilesList.add(file.getName());
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        Collections.sort(stagedFilesList);
        Collections.sort(removedFilesList);

        message("=== Staged Files ===");
        for (String fileName : stagedFilesList) {
            message(fileName);
        }
        message("");

        message("=== Removed Files ===");
        for (String fileName : removedFilesList) {
            message(fileName);
        }
        message("");

        HashSet<String> stagedFilesSet = new HashSet<>(stagedFilesList);
        HashSet<String> removedFilesSet = new HashSet<>(removedFilesList);
        HashSet<String> fileSet = getCurrentFileSet();
        HashSet<String> trackedFiles = getCurrentCommit().getTrackedFiles();

        HashSet<String> delNotStageFilesSet = new HashSet<>(trackedFiles);
        delNotStageFilesSet.removeAll(fileSet);
        delNotStageFilesSet.removeAll(stagedFilesSet);
        delNotStageFilesSet.removeAll(removedFilesSet);

        HashSet<String> untrackedFilesSet = new HashSet<>(fileSet);
        untrackedFilesSet.removeAll(trackedFiles);
        untrackedFilesSet.removeAll(stagedFilesSet);

        HashSet<String> modNotStageFilesSet = new HashSet<>();
        Commit currentCommit = getCurrentCommit();
        for (String fileName : fileSet) {
            File file = join(CWD, fileName);
            String fileSha1 = sha1(readContentsAsString(file));
            File fileInCurrentCommit = currentCommit.findFile(fileName);
            // Case 1: Tracked in current commit, changed but not staged
            if (fileInCurrentCommit != null && !stagedFilesSet.contains(fileName)) {
                String fileInCurrentCommitSha1 = sha1(readContentsAsString(fileInCurrentCommit));
                if (!fileSha1.equals(fileInCurrentCommitSha1)) {
                    modNotStageFilesSet.add(fileName);
                }
            }
            // Case 2: Staged for addition, but with different contents than in working directory
            if (stagedFilesSet.contains(fileName)) {
                File stagedFile = join(STAGING_DIR, fileName);
                String stagedFileSha1 = sha1(readContentsAsString(stagedFile));
                if (!fileSha1.equals(stagedFileSha1)) {
                    modNotStageFilesSet.add(fileName);
                }
            }
        }
        // Case 3: Staged for addition, but deleted in the working directory
        for (String fileName : stagedFilesSet) {
            if (!fileSet.contains(fileName)) {
                modNotStageFilesSet.add(fileName);
                // Update delNotStageFilesSet to include this case with (deleted)
                delNotStageFilesSet.add(fileName);
                modNotStageFilesSet.remove(fileName);
            }
        }

        // 合并并排序 Modifications Not Staged
        List<String> modNotStagedList = new ArrayList<>();
        for (String fileName : delNotStageFilesSet) {
            modNotStagedList.add(fileName + " (deleted)");
        }
        for (String fileName : modNotStageFilesSet) {
            modNotStagedList.add(fileName + " (modified)");
        }
        Collections.sort(modNotStagedList);

        message("=== Modifications Not Staged For Commit ===");
        for (String entry : modNotStagedList) {
            message(entry);
        }
        message("");

        // 排序 Untracked Files
        List<String> untrackedList = new ArrayList<>(untrackedFilesSet);
        Collections.sort(untrackedList);

        message("=== Untracked Files ===");
        for (String fileName : untrackedList) {
            message(fileName);
        }
        message("");
    }

    public static void merge(String branchName) {
        checkDir();
        // 检查暂存区是否有未提交的更改
        if (Objects.requireNonNull(STAGING_DIR.listFiles()).length > 0) {
            message("You have uncommitted changes.");
            System.exit(0);
        }
        String currentBranch = getCurrentBranchName();
        String givenBranch = branchName;
        HashMap<String, String> branchMappingCommit = getBranchMappingCommit();
        if (!branchMappingCommit.containsKey(givenBranch)) {
            message("A branch with that name does not exist.");
            System.exit(0);
        }
        if (currentBranch.equals(givenBranch)) {
            message("Cannot merge a branch with itself.");
            System.exit(0);
        }

        // 检查是否有 untracked files 会被覆盖
        String givenCommitSha1ForCheck = getBranch(givenBranch);
        Commit givenCommitForCheck = Commit.load(givenCommitSha1ForCheck);
        HashSet<String> currentFileSet = getCurrentFileSet();
        HashSet<String> currentTrackedFiles = getCurrentCommit().getTrackedFiles();
        if (givenCommitForCheck != null) {
            for (String fileName : givenCommitForCheck.getTrackedFiles()) {
                if (currentFileSet.contains(fileName) && !currentTrackedFiles.contains(fileName)) {
                    message("There is an untracked file in the way; delete it, or add and commit it first.");
                    System.exit(0);
                }
            }
        }

        String currentCommitSha1 = getBranch(currentBranch);
        String givenCommitSha1 = getBranch(givenBranch);
        String splitPointSha1 = findSplitPoint(currentCommitSha1, givenCommitSha1);
        if (splitPointSha1.equals(givenCommitSha1)) {
            message("Given branch is an ancestor of the current branch.");
            return;
        }
        if (splitPointSha1.equals(currentCommitSha1)) {
            checkoutBranch(givenBranch);
            message("Current branch fast-forwarded.");
            return;
        }
        processMergeFiles(splitPointSha1, currentCommitSha1, givenCommitSha1);

        // 先继承当前 commit 的所有文件
        Commit parentCommit = getCurrentCommit();
        HashMap<String, String> filesMappingBlobs = new HashMap<>();
        if (parentCommit != null) {
            for (String fileName : parentCommit.getTrackedFiles()) {
                String blobSha1 = parentCommit.findFileSha1(fileName);
                if (blobSha1 != null) {
                    filesMappingBlobs.put(fileName, blobSha1);
                }
            }
        }

        // 然后处理暂存区的修改（添加/修改/删除）
        for (File file : Objects.requireNonNull(STAGING_DIR.listFiles())) {
            try {
                if (Files.size(file.toPath()) == 0) {
                    // 空文件表示删除标记
                    filesMappingBlobs.remove(file.getName());
                } else {
                    Blob blob = new Blob(file);
                    blob.save();
                    filesMappingBlobs.put(file.getName(), blob.getSha1());
                }
            } catch (IOException e) {
                throw error("Unable to read staging file.");
            }
        }

        // 清理暂存区
        for (File file : Objects.requireNonNull(STAGING_DIR.listFiles())) {
            try {
                Files.delete(file.toPath());
            } catch (IOException e) {
                throw error("Unable to delete staging file.");
            }
        }

        Commit newCommit = new Commit("Merged " + givenBranch + " into " + currentBranch + ".",
                filesMappingBlobs, currentCommitSha1, givenCommitSha1);
        newCommit.save();
        setCurrentCommitSha1(newCommit.getSha1());
    }

    private static String findSplitPoint(String currentCommitSha1, String givenCommitSha1) {
        // 获取当前分支的所有祖先
        HashSet<String> currentAncestors = getAllAncestors(currentCommitSha1);
        // BFS 查找 given 分支中最近的共同祖先
        Queue<String> queue = new LinkedList<>();
        queue.add(givenCommitSha1);
        while (!queue.isEmpty()) {
            String sha1 = queue.poll();
            if (sha1 == null) {
                continue;
            }
            if (currentAncestors.contains(sha1)) {
                return sha1;
            }
            Commit commit = Commit.load(sha1);
            if (commit != null) {
                if (commit.getParentSha1() != null) {
                    queue.add(commit.getParentSha1());
                }
                if (commit.getSecondParentSha1() != null) {
                    queue.add(commit.getSecondParentSha1());
                }
            }
        }
        return null;
    }

    private static HashSet<String> getAllAncestors(String commitSha1) {
        HashSet<String> ancestors = new HashSet<>();
        Queue<String> queue = new LinkedList<>();
        queue.add(commitSha1);
        while (!queue.isEmpty()) {
            String sha1 = queue.poll();
            if (sha1 == null || ancestors.contains(sha1)) {
                continue;
            }
            ancestors.add(sha1);
            Commit commit = Commit.load(sha1);
            if (commit != null) {
                if (commit.getParentSha1() != null) {
                    queue.add(commit.getParentSha1());
                }
                if (commit.getSecondParentSha1() != null) {
                    queue.add(commit.getSecondParentSha1());
                }
            }
        }
        return ancestors;
    }

    private static void processMergeFiles(String splitPointSha1, String currentCommitSha1, String givenCommitSha1) {
        Commit splitCommit = Commit.load(splitPointSha1);
        Commit currentCommit = Commit.load(currentCommitSha1);
        Commit givenCommit = Commit.load(givenCommitSha1);

        // 收集所有涉及的文件名
        HashSet<String> allFiles = new HashSet<>();
        if (splitCommit != null) {
            allFiles.addAll(splitCommit.getTrackedFiles());
        }
        if (currentCommit != null) {
            allFiles.addAll(currentCommit.getTrackedFiles());
        }
        if (givenCommit != null) {
            allFiles.addAll(givenCommit.getTrackedFiles());
        }

        boolean hasConflict = false;

        for (String fileName : allFiles) {
            boolean modifiedInCurrent = isFileModified(fileName, splitPointSha1, currentCommitSha1);
            boolean modifiedInGiven = isFileModified(fileName, splitPointSha1, givenCommitSha1);
            boolean deletedInCurrent = isFileDeleted(fileName, splitPointSha1, currentCommitSha1);
            boolean deletedInGiven = isFileDeleted(fileName, splitPointSha1, givenCommitSha1);

            String splitSha1 = splitCommit != null ? splitCommit.findFileSha1(fileName) : null;
            String currentSha1 = currentCommit != null ? currentCommit.findFileSha1(fileName) : null;
            String givenSha1 = givenCommit != null ? givenCommit.findFileSha1(fileName) : null;

            if (!modifiedInCurrent && modifiedInGiven) {
                // 规则1: 仅 given 修改 -> 使用 given 版本
                if (deletedInGiven) {
                    // 删除文件
                    rmFileByName(fileName);
                } else {
                    checkout(givenCommitSha1, fileName);
                    stagingFileByName(fileName);
                }
            } else if (modifiedInCurrent && !modifiedInGiven) {
                // 规则2: 仅 current 修改 -> 保持不变
            } else if (modifiedInCurrent && modifiedInGiven) {
                // 两边都修改了
                if (Objects.equals(currentSha1, givenSha1)) {
                    // 规则3: 同样修改 -> 保持不变
                } else {
                    // 规则4: 不同修改 -> 冲突
                    handleConflict(fileName, currentCommit, givenCommit);
                    hasConflict = true;
                }
            }
        }

        if (hasConflict) {
            message("Encountered a merge conflict.");
        }
    }

    private static boolean isFileModified(String fileName, String fromCommitSha1, String toCommitSha1) {
        Commit fromCommit = Commit.load(fromCommitSha1);
        Commit toCommit = Commit.load(toCommitSha1);

        String blobSha1InFrom = (fromCommit != null) ? fromCommit.findFileSha1(fileName) : null;
        String blobSha1InTo = (toCommit != null) ? toCommit.findFileSha1(fileName) : null;

        // 两者都为 null 表示未修改（都不存在）
        if (blobSha1InFrom == null && blobSha1InTo == null) {
            return false;
        }
        // 其中一个为 null 表示文件被添加或删除
        if (blobSha1InFrom == null || blobSha1InTo == null) {
            return true;
        }
        // 比较 blob 的 SHA1 判断内容是否改变
        return !blobSha1InFrom.equals(blobSha1InTo);
    }

    private static boolean isFileDeleted(String fileName, String fromCommitSha1, String toCommitSha1) {
        Commit fromCommit = Commit.load(fromCommitSha1);
        Commit toCommit = Commit.load(toCommitSha1);

        // 文件在 from 中存在，但在 to 中不存在
        boolean existsInFrom = fromCommit != null && fromCommit.findFileSha1(fileName) != null;
        boolean existsInTo = toCommit != null && toCommit.findFileSha1(fileName) != null;

        return existsInFrom && !existsInTo;
    }

    private static boolean isFileAdded(String fileName, String fromCommitSha1, String toCommitSha1) {
        Commit fromCommit = Commit.load(fromCommitSha1);
        Commit toCommit = Commit.load(toCommitSha1);

        // 文件在 from 中不存在，但在 to 中存在
        boolean existsInFrom = fromCommit != null && fromCommit.findFileSha1(fileName) != null;
        boolean existsInTo = toCommit != null && toCommit.findFileSha1(fileName) != null;

        return !existsInFrom && existsInTo;
    }

    private static void handleConflict(String fileName, Commit currentCommit, Commit givenCommit) {
        File workingFile = join(CWD, fileName);
        String currentContent = "";
        String givenContent = "";

        File currentBlobFile = currentCommit.findFile(fileName);
        if (currentBlobFile != null) {
            currentContent = readContentsAsString(currentBlobFile);
        }

        File givenBlobFile = givenCommit.findFile(fileName);
        if (givenBlobFile != null) {
            givenContent = readContentsAsString(givenBlobFile);
        }

        String conflictContent = "<<<<<<< HEAD\n" +
                currentContent +
                "=======\n" +
                givenContent +
                ">>>>>>>\n";

        writeContents(workingFile, conflictContent);
        stagingFileByName(fileName);
    }
}
