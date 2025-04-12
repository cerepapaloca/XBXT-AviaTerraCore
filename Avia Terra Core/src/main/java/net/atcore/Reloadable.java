package net.atcore;

import java.net.UnknownHostException;
import java.sql.SQLException;

public interface Reloadable {
    void reload() throws UnknownHostException, SQLException;
}
