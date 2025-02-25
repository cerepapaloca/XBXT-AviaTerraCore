package net.atcore.security.check.checker;

import io.papermc.paper.event.packet.PlayerChunkLoadEvent;
import net.atcore.security.check.BaseChecker;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

public class AntiIlegalBlock extends BaseChecker<PlayerChunkLoadEvent> {

    @Override
    public void onCheck(PlayerChunkLoadEvent event) {
        Chunk chunk = event.getChunk();

        int yUpperLimit = event.getWorld().getMaxHeight();
        int yLowerLimit = event.getWorld().getMinHeight();

        if (event.getWorld().getEnvironment() == World.Environment.NETHER) {
            yUpperLimit = 125;
        }

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = yLowerLimit; y < yUpperLimit; y++) {
                    Block block = chunk.getBlock(x, y, z);
                    Material type = block.getType();

                    if (type == Material.END_PORTAL_FRAME ||
                            type == Material.REINFORCED_DEEPSLATE ||
                            type == Material.BARRIER ||
                            type == Material.LIGHT ||
                            type == Material.END_PORTAL ||
                            (type == Material.BEDROCK && y >= yLowerLimit + 5)) {
                        block.setType(Material.AIR);
                    }
                }
            }
        }
    }
}
