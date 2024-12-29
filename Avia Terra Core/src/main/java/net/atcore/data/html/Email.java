package net.atcore.data.html;

import net.atcore.data.FileHTML;
import net.atcore.security.Login.TwoFactorAuth;

public class Email extends FileHTML {

    public Email() {
        super("email");
    }

    @Override
    public void loadData() {
        String string = readFile();
        TwoFactorAuth.setGmail(string);
    }
}
