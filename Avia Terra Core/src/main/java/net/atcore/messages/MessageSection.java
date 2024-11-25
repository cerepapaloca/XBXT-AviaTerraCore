package net.atcore.messages;

import me.scarsz.jdaappender.ChannelLoggingHandler;
import me.scarsz.jdaappender.ExtensionBuilder;
import net.atcore.AviaTerraCore;
import net.atcore.Section;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.bukkit.Bukkit;

import java.text.SimpleDateFormat;
import java.util.*;

import static net.atcore.AviaTerraCore.jda;
import static net.atcore.AviaTerraCore.TOKEN_BOT;
import static net.atcore.messages.MessagesManager.sendMessageConsole;
import static org.bukkit.Bukkit.*;

public class MessageSection implements Section {

    public static final String CONSOLE_ID = "1294324285602795550";

    @Override
    public void enable() {
        Bukkit.getScheduler().runTaskAsynchronously(AviaTerraCore.getInstance(), () -> {
            jda = JDABuilder.createDefault(TOKEN_BOT).enableIntents(GatewayIntent.MESSAGE_CONTENT).build();
            try {
                jda.awaitReady();
                TextChannel logChannel = jda.getTextChannelById(CONSOLE_ID);
                if (logChannel == null) {
                    getServer().getPluginManager().disablePlugin(AviaTerraCore.getInstance());
                    return;
                }
                SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss");
                sdf.setTimeZone(TimeZone.getTimeZone("GMT-5"));
                new ChannelLoggingHandler(() -> logChannel, handlerConfig -> {
                    handlerConfig.setColored(true);
                    handlerConfig.setSplitCodeBlockForLinks(false);
                    handlerConfig.setAllowLinkEmbeds(true);
                    handlerConfig.mapLoggerName("net.minecraft.server.MinecraftServer","Server");
                    handlerConfig.mapLoggerName("net.dv8tion.jda","JDA");
                    handlerConfig.mapLoggerName("net.minecraft.server","Server");
                    handlerConfig.mapLoggerName("net.minecraft","Minecraft");
                    handlerConfig.setPrefixer(new ExtensionBuilder(handlerConfig)
                            .text("[").timestamp(sdf).text("]")
                            .space()
                            .text("[").level().text("]").space()
                            .text("[").logger().text("]").space()
                            .build()
                    );

                }).attach().schedule();
                sendMessageConsole("discord bot &2 Ok", TypeMessages.INFO, false);

            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        });
    }

    @Override
    public void disable() {

    }

    @Override
    public void reloadConfig() {

    }

    @Override
    public String getName() {
        return "Mensajes";
    }
}
