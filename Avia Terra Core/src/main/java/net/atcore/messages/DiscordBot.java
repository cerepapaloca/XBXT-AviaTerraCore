package net.atcore.messages;

import me.scarsz.jdaappender.ChannelLoggingHandler;
import me.scarsz.jdaappender.ExtensionBuilder;
import net.atcore.AviaTerraCore;
import net.atcore.command.CommandManager;
import net.atcore.utils.GlobalUtils;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

import static net.atcore.AviaTerraCore.*;
import static org.bukkit.Bukkit.getServer;

public class DiscordBot extends ListenerAdapter{

    public static final char PREFIX_COMMAND = '-';

    public static String consoleId = "1294324285602795550";
    public static String chatId = "1294324328401207389";//
    public static String JoinAndLeave = "1338315306585817158";
    public static ChannelLoggingHandler handler;
    public static BukkitTask stateTasks = null;

    public static void startDiscordBot(){
        AviaTerraCore.enqueueTaskAsynchronously(true, () -> {
            try {
                jda = JDABuilder.createDefault(tokenBot).enableIntents(GatewayIntent.MESSAGE_CONTENT).build();
                jda.removeEventListener(DiscordBot.class);//TODO: Cuando se hace reload los mensaje de discord se duplican
                jda.awaitReady();
                jda.addEventListener(new DiscordBot());
                startStateBot();
                TextChannel logChannel = jda.getTextChannelById(consoleId);
                if (logChannel == null) {
                    getServer().getPluginManager().disablePlugin(AviaTerraCore.getInstance());
                    return;
                }
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
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
            } catch (Exception e) {
                MessagesManager.logConsole("discord bot " + TypeMessages.ERROR.getMainColor() + "Fail", TypeMessages.INFO, false);
                MessagesManager.sendWaringException("No se pudo iniciar el bot de discord", e);
            }
        });
    }

    public static void startStateBot(){
        String os = System.getProperty("os.name").toLowerCase();
        // Esto solo funciona el linux es decir en el servidor principal
        if (jda == null) return;
        if (os.contains("nix") || os.contains("nux")) {
            stateTasks = new BukkitRunnable() {
                @Override
                public void run() {
                    jda.getPresence().setActivity(Activity.of(
                            Activity.ActivityType.PLAYING,
                            String.format("Jugadores: %s/%s", Bukkit.getOnlinePlayers().size(), Bukkit.getMaxPlayers()),
                            "https://xbxt.xyz"
                    ));
                }
            }.runTaskTimerAsynchronously(AviaTerraCore.getInstance(), 0L, 20L*2);
        }
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;

        Member member = event.getMember();
        if (member == null) return;

        Message message = event.getMessage();
        String channel = event.getChannel().getId();
        net.atcore.messages.Message format = net.atcore.messages.Message.EVENT_CHAT_FORMAT;

        if (channel.equals(DiscordBot.consoleId)){
            if (message.getContentRaw().startsWith(Character.toString(PREFIX_COMMAND))) CommandManager.processCommandFromDiscord(message, member);
        }else if (channel.equals(DiscordBot.chatId)){
            String rowContent;
            if (message.getContentRaw().startsWith("https")){
                rowContent = "<click:open_url:" + message.getContentRaw() + ">" + message.getContentRaw() + "</click>";
            }else {
                rowContent = message.getContentRaw();
            }
            Component component = MessagesManager.applyFinalProprieties(String.format(
                    MessagesManager.PREFIX_CHAT_DISCORD + format.getMessageLocaleDefault(),
                    member.getColor() == null ? "<#AAAAAA>" + member.getUser().getGlobalName() :
                            "<" + GlobalUtils.javaColorToStringHex(member.getColor()) + ">" + (member.getUser().getGlobalName() != null ? member.getUser().getGlobalName() : member.getUser().getName()),
                    "<" + NamedTextColor.GRAY.asHexString() + ">" + rowContent
            ), TypeMessages.GENERIC, CategoryMessages.PRIVATE, false);
            Component roles = Component.text("Roles: ");
            for (Role role : member.getRoles()) {
                roles = roles.append(Component.text(role.getName()).color(TextColor.color(role.getColorRaw())).appendSpace());
            }
            Bukkit.broadcast(component.hoverEvent(HoverEvent.showText(roles)));
        }
    }
}
