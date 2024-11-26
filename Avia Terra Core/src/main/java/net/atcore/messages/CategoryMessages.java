package net.atcore.messages;

import lombok.Getter;

@Getter
public enum CategoryMessages {
    PRIVATE,
    COMMANDS,
    BAN,
    MODERATION,
    LOGIN,
    PVP;

    private String idChannel;

    public void setIdChannel(String idChannel) {
        if (idChannel == null){
            this.idChannel = null;
        }else if (idChannel.isBlank()){
            this.idChannel = null;
        }else {
            this.idChannel = idChannel;
        }
    }

}
