package net.atcore.test;

import net.atcore.aviaterraplayer.AviaTerraPlayer;
import net.atcore.security.Login.LoginManager;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.TestOnly;

@TestOnly
public final class LoginRemove implements RunTest {
    @Override
    public void runTest(AviaTerraPlayer player) {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "removesession " + player.getPlayer().getName());
        LoginManager.checkLoginIn(player.getPlayer());
        Bukkit.dispatchCommand(player.getPlayer(), "test");
    }
}
