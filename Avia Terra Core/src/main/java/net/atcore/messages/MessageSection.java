package net.atcore.messages;

import me.scarsz.jdaappender.ChannelLoggingHandler;
import me.scarsz.jdaappender.ExtensionBuilder;
import me.scarsz.jdaappender.LogItem;
import me.scarsz.jdaappender.LogLevel;
import net.atcore.AviaTerraCore;
import net.atcore.Section;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.BiFunction;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static net.atcore.AviaTerraCore.jda;
import static net.atcore.AviaTerraCore.TOKEN_BOT;
import static net.atcore.messages.MessagesManager.sendMessageConsole;
import static org.bukkit.Bukkit.*;

public class MessageSection implements Section {

    private static final List<String> LEVELS = List.of("TEST","HOLA","MUNDO");

    @Override
    public void enable() {
        Bukkit.getScheduler().runTaskAsynchronously(AviaTerraCore.getInstance(), () -> {
            jda = JDABuilder.createDefault(TOKEN_BOT).build();
            try {
                jda.awaitReady();
                TextChannel logChannel = jda.getTextChannelById("1294324285602795550");
                if (logChannel == null) {
                    getLogger().severe("El canal de Discord con ID no existe.");
                    getServer().getPluginManager().disablePlugin(AviaTerraCore.getInstance());
                    return;
                }
                SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss");
                sdf.setTimeZone(TimeZone.getTimeZone("GMT-5"));
                new ChannelLoggingHandler(() -> logChannel, handlerConfig -> {
                    handlerConfig.setColored(true);
                    handlerConfig.setSplitCodeBlockForLinks(false);
                    handlerConfig.setAllowLinkEmbeds(true);
                    handlerConfig.mapLoggerName("net.minecraft.server.MinecraftServer"," Server");
                    handlerConfig.mapLoggerName("net.dv8tion.jda","JDA");
                    handlerConfig.mapLoggerName("net.minecraft.server","/Server");
                    handlerConfig.mapLoggerName("net.minecraft","/Minecraft");
                    handlerConfig.setPrefixer(new ExtensionBuilder(handlerConfig)
                            .text("[").timestamp(sdf).text("]")
                            .space()
                            .text("[").level().loggerPadded().text("]").space()
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
