package gitlet;

import java.io.IOException;

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
                if (args.length == 1) {
                    Repository.init();
                } else {
                    incorrectOperands();
                }
                break;
            case "add":
                if (args.length == 2) {
                    Repository.stagingFileByName(args[1]);
                } else {
                    incorrectOperands();
                }
                break;
            case "commit":
                if (args.length == 2) {
                    String message = args[1];
                    Repository.stagingToCommit(message);
                } else {
                    incorrectOperands();
                }
                break;
            case "checkout":
                if (args.length == 3 && args[1].equals("--")) {
                    String fileName = args[2];
                    Repository.checkout(fileName);
                } else if (args.length == 4 && args[2].equals("--")) {
                    String commitID = args[1];
                    String fileName = args[3];
                    Repository.checkout(commitID, fileName);
                } else if (args.length == 2) {

                    return;
                } else {
                    incorrectOperands();
                }
                break;
            case "log":
                Repository.log();
                break;
            default:
                message("No command with that name exists.");
                break;
        }
    }

    private static void incorrectOperands() {
        message("Incorrect operands.");
        System.exit(0);
    }
}
