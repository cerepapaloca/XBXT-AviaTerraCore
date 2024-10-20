package net.atcore.BaseCommand;

import lombok.experimental.UtilityClass;

@UtilityClass
public final class CommandUtils {

    public boolean isTrueOrFalse(String ars) {
        ars = ars.toLowerCase();
        switch (ars) {
            case "true" ->{
                return true;
            }
            case "false" ->{
                return false;
            }
        }
        return false;
    }

    public String booleanToString(boolean bool) {
        return bool ? "<|Activo|>" : "<|Desactivado|>";
    }

}
