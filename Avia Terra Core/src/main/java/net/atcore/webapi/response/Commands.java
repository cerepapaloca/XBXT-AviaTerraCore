package net.atcore.webapi.response;

import io.undertow.server.HttpServerExchange;
import net.atcore.command.CommandHandler;
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
        CommandHandler.AVIA_TERRA_COMMANDS.forEach(command -> {
            switch (command.getVisibility()) {
                case PUBLIC, ALL -> commands.add(new Command(command.getName(), command.getDescription(), command.getUsage()));
                case SEMI_PUBLIC -> commands.add(new Command(command.getName(), command.getDescription(), command.getUsage(), command.getPermission()));
            }
        });

        return commands;
    }

    public static class Command {

        private final String name;
        private final String description;
        private final String usage;
        private final String permission;

        public Command(String name, String description, String usage) {
            this.name = "/" + name.toLowerCase();
            this.description = description;
            this.usage = usage;
            this.permission = null;
        }

        public Command(String name, String description, String usage, String permission) {
            this.name = "/" + name.toLowerCase();
            this.description = description;
            this.usage = usage;
            this.permission = permission;
        }
    }
}
