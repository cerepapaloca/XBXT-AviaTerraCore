package net.atcore.command;

import lombok.Getter;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


/**
 * Creas el cómo tiene que usar el comando y Autogenera un TAB en caso la clase extienda de {@link BaseCommand} si extiende de
 * {@link BaseTabCommand} se usara el {@link BaseTabCommand#onTab(CommandSender, String[]) onTab()} para crear el TAB
 * <p>
 * Un ejemplo de como crear los argumentos.
 * <blockquote><pre>
 *     new ArgumentUse("prueba")
 *          .addArg("Alfa", "Beta", "Gamma")
 *          .addArgPlayer(ModeTabPlayers.ADVANCED)
 *          .addNote("Note")
 *          .addTime(true),
 * </pre></blockquote>
 */


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
        private boolean isFinal = false;
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
            if (modePlayers == ModeTabPlayers.ADVANCED) {
                this.arg = new String[]{"jugador/es"};
            } else {
                this.arg = new String[]{"jugador"};
            }
            this.required = true;
        }
    }

    private final List<Argument> args = new ArrayList<>();

    public ArgumentUse addFinalArg(@NotNull String s) {
        Argument arg = new Argument(isRequired, new String[]{s});
        arg.isFinal = true;
        args.add(arg);
        return this;
    }

    /**
     * Añades una lista de argumentos donde el jugador podrá autocompletar
     * @param arg Argumento/s
     */

    public ArgumentUse addArg(String... arg) {
        args.add(new Argument(isRequired, arg));
        return this;
    }

    @SuppressWarnings("rawtypes")
    public ArgumentUse addArg(Enum[] arg) {
        args.add(new Argument(isRequired, CommandUtils.enumsToStrings(arg, true)));
        return this;
    }

    /**
     * Hace que los argumentos que siguen después sean opcionales y no salte el error de
     * sintaxis
     */

    public ArgumentUse addArgOptional() {
        isRequired = false;
        return this;
    }

    /**
     * Añades una nota a los argumentos que tiene que escribir el jugador no podría autocompletar,
     * solo está para indicar lo que tiene que escribir, pero sin decir exactamente lo hay que
     * poner un ejemplo sería las contraseñas
     * @param note La nota
     */

    public ArgumentUse addNote(String... note) {
        Argument arg = new Argument(isRequired, note);
        arg.note = true;
        args.add(arg);
        return this;
    }


    /**
     * Añades un argumento de tiempo usando {@link CommandUtils#listTabTime(String, boolean) listTabTime()}
     * @param isEspecial ¿Se usará el numeró máximo y el permanente?
     */

    public ArgumentUse addTime(boolean isEspecial) {
        Argument arg = new Argument(isRequired, new String[]{"Tiempo"});
        arg.useTime = true;
        arg.especialTime = isEspecial;
        args.add(arg);
        return this;
    }

    /**
     * Añades una lista de jugadores usando {@link CommandUtils#tabForPlayer(String) tabForPlayer()} o
     * el vanilla dependiendo el modo que use.
     * <p>
     * <strong>Importante: Usar {@link ModeTabPlayers#NONE NONE} crea una lista vacía</strong>
     * @see ModeTabPlayers
     */
    
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
        if (arg.isFinal) {
            return "<" + sbArgs + "...";
        }else {
            return "<" + sbArgs + ">";
        }

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
            if (length > getLength()){
                if (this.args.getLast().isFinal){
                    return List.of(this.args.getLast().getArg());
                }else {
                    return List.of();
                }
            }
            Argument argument = getArg(length - 1);
            String lastArgString = args[length - 1];
            switch (argument.mode){
                case NORMAL -> {
                    return null;
                }
                case ADVANCED -> {
                    return CommandUtils.tabForPlayer(lastArgString);
                }
                default -> {
                    if (argument.note) {
                        return List.of(argument.arg);
                    }
                    if (argument.useTime) {
                        return CommandUtils.listTabTime(lastArgString, true);
                    }
                    return List.of(argument.arg);
                }
            }
        }catch (IndexOutOfBoundsException e){
            return List.of();
        }
    }

    public int getLength(){
        return args.size();
    }
}
