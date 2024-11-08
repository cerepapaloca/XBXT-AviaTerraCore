package net.atcore.inventory;

import lombok.Getter;
import net.atcore.inventory.Action.ManipulatedAction;
import net.atcore.inventory.Action.ManipulatorAction;
import net.atcore.inventory.inventors.ManipulatorInventory;

@Getter
public enum InventorySection {
    MANIPULATOR(new ManipulatorAction(), new ManipulatorInventory(), false),
    MANIPULATED(new ManipulatedAction(), null, false);

    InventorySection(BaseActions baseActions, BaseInventory baseInventory, boolean isProtected) {
        this.baseActions = baseActions;
        this.baseInventory = baseInventory;
        this.protectedInventory = isProtected;
    }

    private final BaseActions baseActions;
    private final BaseInventory baseInventory;
    private final boolean protectedInventory;

    public void init() {
        if (baseActions != null) {
            baseActions.setSection(this);
        }
        if (baseInventory != null) {
            baseInventory.setSection(this);
        }
    }

    static {
        for (InventorySection section : values()) {
            section.init();
        }
    }
}
