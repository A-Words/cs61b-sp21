package gitlet;

import static gitlet.Utils.message;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author TODO
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ...
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Please enter a command.");
            System.exit(0);
        }

        String firstArg = args[0];
        switch(firstArg) {
            case "init":
                checkOperands(args, 1);
                Repository.init();
                break;
            case "add":
                checkOperands(args, 2);
                Repository.stagingFileByName(args[1]);
                break;
            case "commit":
                checkOperands(args, 2);
                String message = args[1];
                Repository.stagingToCommit(message);
                break;
            case "rm":
                checkOperands(args, 2);
                String fileName = args[1];
                Repository.rmFileByName(fileName);
                break;
            case "log":
                checkOperands(args, 1);
                Repository.log();
                break;
            case "global-log":
                checkOperands(args, 1);
                Repository.globalLog();
                break;
            case "find":
                checkOperands(args, 2);
                String commitMessage = args[1];
                Repository.find(commitMessage);
                break;
            case "status":
                checkOperands(args, 1);
                Repository.status();
                break;
            case "checkout":
                if (args.length == 3 && args[1].equals("--")) {
                    fileName = args[2];
                    Repository.checkout(fileName);
                } else if (args.length == 4 && args[2].equals("--")) {
                    String commitID = args[1];
                    fileName = args[3];
                    Repository.checkout(commitID, fileName);
                } else if (args.length == 2) {
                    String branchName = args[1];
                    Repository.checkoutBranch(branchName);
                } else {
                    incorrectOperands();
                }
                break;
            case "branch":
                checkOperands(args, 2);
                Repository.createBranch(args[1]);
                break;
            case "rm-branch":
                checkOperands(args, 2);
                Repository.removeBranch(args[1]);
                break;
            case "reset":
                checkOperands(args, 2);
                Repository.reset(args[1]);
                break;
            case "merge":
                checkOperands(args, 2);
                Repository.merge(args[1]);
                break;
            default:
                message("No command with that name exists.");
                break;
        }
    }

    private static void checkOperands(String[] args, int num) {
        if (args.length != num) {
            incorrectOperands();
        }
    }

    private static void incorrectOperands() {
        message("Incorrect operands.");
        System.exit(0);
    }
}
