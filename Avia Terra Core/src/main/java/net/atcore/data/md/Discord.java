package net.atcore.data.md;

import net.atcore.data.FileMarkdownDocumentation;
import net.atcore.security.Login.TwoFactorAuth;

public class Discord extends FileMarkdownDocumentation {

    public Discord() {
        super("Discord");
    }

    @Override
    public void loadData() {
        String string = readFile();
        TwoFactorAuth.setDiscord(string);
    }
}
