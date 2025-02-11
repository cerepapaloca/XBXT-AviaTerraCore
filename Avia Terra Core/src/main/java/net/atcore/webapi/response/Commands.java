package net.atcore.webapi.response;

import io.undertow.server.HttpServerExchange;
import net.atcore.command.CommandSection;
import net.atcore.webapi.BaseApi;

import java.util.ArrayList;
import java.util.List;

public class Commands extends BaseApi {

    public Commands() {
        super("commands");
    }

    @Override
    public Object onRequest(HttpServerExchange request) {
        List<Command> commands = new ArrayList<>();
        CommandSection.getCommandHandler().getCommands().forEach(command -> {
            if (command.getAviaTerraPermissions().equals("*") || command.getAviaTerraPermissions().equals("**")){
                commands.add(new Command(command.getName(), command.getDescription(), command.getUsage()));
            }
        });

        return commands;
    }

    public static class Command {

        private final String name;
        private final String description;
        private final String usage;

        public Command(String name, String description, String usage) {
            this.name = "/" + name.toLowerCase();
            this.description = description;
            this.usage = usage;
        }
    }
}
