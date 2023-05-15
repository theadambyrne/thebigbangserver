package CS4442.OS;

import java.io.PrintWriter;

public class Command {
    private String rawCommand;

    private String[] validCommands = { "quit", "help", "list" };

    public enum ServerSignals {
        QUIT, LIST, HELP
    };

    public Command(String command) {
        this.rawCommand = command;
        this.validate();
    }

    private boolean validate() throws IllegalArgumentException {
        if (rawCommand == null || rawCommand.isEmpty() || rawCommand.trim().isEmpty()) {
            throw new IllegalArgumentException("Command cannot be null or empty");
        }

        for (String validCommand : validCommands) {
            if (rawCommand.equals(validCommand)) {
                return true;
            }
        }

        throw new IllegalArgumentException("Command is not valid");
    }

    public ServerSignals execute(PrintWriter out) {

        switch (rawCommand) {
            case "quit":
                return ServerSignals.QUIT;
            case "help":

                StringBuilder sb = new StringBuilder();

                for (String validCommand : validCommands) {
                    sb.append("/" + validCommand + " ");
                }

                out.println(sb.toString());
                return ServerSignals.HELP;
            case "list":
                return ServerSignals.LIST;
            default:
                out.println("Command not found");
                return null;
        }
    }

    public String getCommand() {
        return rawCommand;
    }
}