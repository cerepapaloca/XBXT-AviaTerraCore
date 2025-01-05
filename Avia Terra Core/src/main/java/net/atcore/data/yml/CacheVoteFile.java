package net.atcore.data.yml;

import net.atcore.data.FileYaml;
import net.atcore.listener.NuVotifierListener;

import java.util.List;

public class CacheVoteFile extends FileYaml {

    public CacheVoteFile() {
        super("cacheVote", null, false, true);
    }

    @Override
    public void loadData() {
        loadConfig();
        NuVotifierListener.LIST_VOTE.clear();
        List<?> name = fileYaml.getList("usernames");
        if (name != null) for (Object o : name) NuVotifierListener.LIST_VOTE.add((String) o);
    }

    @Override
    public void saveData() {
        fileYaml.set("usernames", NuVotifierListener.LIST_VOTE);
        saveConfig();
    }
}
