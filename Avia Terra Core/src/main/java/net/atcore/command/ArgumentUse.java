package net.atcore.command;

import lombok.Getter;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ArgumentUse {

    private final String command;
    private boolean isRequired = true;

    public ArgumentUse(@NotNull String s) {
        if (s.startsWith("/")){
            s = s.substring(1);
        }
        this.command = s;
    }

    @Getter
    public static class Argument {
        @NotNull
        private final String[] arg;
        private boolean note = false;
        private boolean useTime = false;
        private boolean especialTime = false;
        @NotNull
        private final ModeTabPlayers mode;
        private final boolean required;
        

        public Argument(boolean required, String[] arg) {
            this.arg = arg;
            this.mode = ModeTabPlayers.NONE;
            this.required = required;
        }

        public Argument(@NotNull ModeTabPlayers modePlayers) {
            this.mode = modePlayers;
            this.arg = new String[]{"jugador"};
            this.required = true;
        }
    }

    private final List<Argument> args = new ArrayList<>();;

    public ArgumentUse addArg(String... arg) {
        args.add(new Argument(isRequired, arg));
        return this;
    }

    public ArgumentUse addArgOptional() {
        isRequired = false;
        return this;
    }

    public ArgumentUse addNote(String... note) {
        Argument arg = new Argument(isRequired, note);
        arg.note = true;
        args.add(arg);
        return this;
    }
    
    public ArgumentUse addTime(boolean isEspecial) {
        Argument arg = new Argument(isRequired, new String[]{"Tiempo"});
        arg.useTime = true;
        arg.especialTime = isEspecial;
        args.add(arg);
        return this;
    }
    
    public ArgumentUse addArgPlayer(ModeTabPlayers modePlayers) {
        args.add(new Argument(modePlayers));
        return this;
    }

    public Argument getArg(int index) {
        return args.get(index);
    }

    public String getArgRaw(int i){
        Argument arg = args.get(i);
        StringBuilder sbArgs = new StringBuilder();
        boolean first = true;
        for (String s : arg.arg) {
            if (first) {
                first = false;
                sbArgs.append(s);
            }else {
                sbArgs.append(" | ").append(s);
            }
        }
        return "<" + sbArgs + ">";
    }

    @Override
    public String toString() {
        StringBuilder sbMain = new StringBuilder();
        sbMain.append("/").append(command).append(" ");
        for (int i = 0; i < args.size(); i++) {
            sbMain.append(getArgRaw(i)).append(" ");
        }
        return sbMain.toString();
    }

    public List<String> onTab(String[] args) {
        int length = args.length;
        try{
            if (length > getLength()) return List.of();
            Argument argument = getArg(length - 1);
            String lastArgString = args[length - 1];
            if (argument.note) {
                return List.of(argument.arg);
            }
            if (argument.useTime) {
                return CommandUtils.listTabTime(lastArgString, true);
            }
            switch (argument.mode){
                case NONE -> {
                    return CommandUtils.listTab(lastArgString, argument.arg);
                }
                case NORMAL -> {
                    return null;
                }
                case ADVANCED -> {
                    return CommandUtils.tabForPlayer(lastArgString);
                }
            }
        }catch (IndexOutOfBoundsException e){
            return List.of();
        }
        return List.of();
    }

    public int getLength(){
        return args.size();
    }
}
