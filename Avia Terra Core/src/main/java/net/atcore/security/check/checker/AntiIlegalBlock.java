package net.atcore.security.check.checker;

import io.papermc.paper.event.packet.PlayerChunkLoadEvent;
import net.atcore.AviaTerraCore;
import net.atcore.security.check.BaseChecker;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.List;

public class AntiIlegalBlock extends BaseChecker<PlayerChunkLoadEvent> {

    // Código tomando de 8b8t
    @Override
    public void onCheck(PlayerChunkLoadEvent event) {
        Chunk chunk = event.getChunk();

        AviaTerraCore.enqueueTaskAsynchronously(() -> {
            List<Block> blocksRemove = new ArrayList<>();
            int yUpperLimit = event.getWorld().getMaxHeight();
            int yLowerLimit = event.getWorld().getMinHeight();

            if (event.getWorld().getEnvironment() == World.Environment.NETHER) yUpperLimit = 125;

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
                                type == Material.COMMAND_BLOCK ||
                                type == Material.REPEATING_COMMAND_BLOCK ||
                                type == Material.CHAIN_COMMAND_BLOCK ||
                                (type == Material.BEDROCK && y >= yLowerLimit + 5)) {
                            blocksRemove.add(block);
                        }
                    }
                }
            }
            AviaTerraCore.taskSynchronously(() -> blocksRemove.forEach(block -> block.setType(Material.AIR)));
        });
    }
}
