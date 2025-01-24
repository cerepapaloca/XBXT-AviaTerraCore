package net.atcore.messages;

import me.scarsz.jdaappender.ChannelLoggingHandler;
import me.scarsz.jdaappender.ExtensionBuilder;
import net.atcore.AviaTerraCore;
import net.atcore.command.CommandManager;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

import static net.atcore.AviaTerraCore.TOKEN_BOT;
import static net.atcore.AviaTerraCore.jda;
import static org.bukkit.Bukkit.getServer;

public class ConsoleDiscord {

    public static String consoleId = "1294324285602795550";
    public static ChannelLoggingHandler handler;
    public static BukkitTask stateTasks = null;

    public static void startConsoleAndBot(){
        AviaTerraCore.enqueueTaskAsynchronously(true, () -> {
            jda = JDABuilder.createDefault(TOKEN_BOT).enableIntents(GatewayIntent.MESSAGE_CONTENT).build();
            try {
                jda.awaitReady();
                jda.addEventListener(new ConsoleDiscordListener());
                startStateBot();
                TextChannel logChannel = jda.getTextChannelById(consoleId);
                if (logChannel == null) {
                    getServer().getPluginManager().disablePlugin(AviaTerraCore.getInstance());
                    return;
                }
                SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss");
                sdf.setTimeZone(TimeZone.getTimeZone("GMT-5"));
                handler = new ChannelLoggingHandler(() -> logChannel, handlerConfig -> {
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
                            .build()
                    );

                }).attach().schedule();
                MessagesManager.logConsole("discord bot " + TypeMessages.SUCCESS.getMainColor() + "Ok", TypeMessages.INFO, false);

            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public static void startStateBot(){
        String os = System.getProperty("os.name").toLowerCase();
        // Esto solo funciona el linux es decir en el servidor principal
        if (os.contains("nix") || os.contains("nux")) {
            stateTasks = new BukkitRunnable() {
                @Override
                public void run() {
                    jda.getPresence().setActivity(Activity.playing(String.format("Jugadores: %s/%s", Bukkit.getOnlinePlayers().size(), Bukkit.getMaxPlayers())));
                }
            }.runTaskTimerAsynchronously(AviaTerraCore.getInstance(), 0L, 20L);
        }
    }

    public static class ConsoleDiscordListener extends ListenerAdapter {

        @Override
        public void onMessageReceived(@NotNull MessageReceivedEvent event) {
            if (event.getAuthor().isBot()) return;

            Member member = event.getMember();
            assert member != null;

            Message message = event.getMessage();
            String channel = event.getChannel().getId();
            if (channel.equals(ConsoleDiscord.consoleId)){
                if (message.getContentRaw().startsWith("-")) CommandManager.processCommandFromDiscord(message, member);
            }
        }
    }
}
