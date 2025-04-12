package net.atcore.command;

import net.atcore.messages.Message;

/**
 * Los comandos que implemente está interfaz se tiene que confirmar
 */

public interface Confirmable {

    Message getMessageConfirm();
}
