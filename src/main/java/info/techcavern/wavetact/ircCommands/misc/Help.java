package info.techcavern.wavetact.ircCommands.misc;

import info.techcavern.wavetact.annot.IRCCMD;
import info.techcavern.wavetact.objects.IRCCommand;
import info.techcavern.wavetact.utils.GeneralUtils;
import info.techcavern.wavetact.utils.IRCUtils;
import org.apache.commons.lang3.StringUtils;
import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.User;

import java.util.Arrays;

@IRCCMD
public class Help extends IRCCommand {

    public Help() {
        super(GeneralUtils.toArray("help halp"), 0, "help (command)", "Gets help on a command", false);
    }

    @Override
    public void onCommand(String command, User user, PircBotX network, String prefix, Channel channel, boolean isPrivate, int userPermLevel, String... args) throws Exception {
        if (args.length > 0) {
                IRCCommand irCommand = IRCUtils.getCommand(args[0], IRCUtils.getNetworkNameByNetwork(network), channel.getName());
                if (irCommand != null) {
                    IRCUtils.sendMessage(user, network, channel, "Variations: " + StringUtils.join(Arrays.asList(irCommand.getCommandID()), ", "), prefix);
                    String syntax = irCommand.getSyntax();
                    if (!syntax.isEmpty())
                        IRCUtils.sendMessage(user, network, channel, "Syntax: " + syntax, prefix);
                    IRCUtils.sendMessage(user, network, channel, irCommand.getDesc(), prefix);
                } else {
                    IRCUtils.sendError(user, network, channel, "Command does not exist", prefix);
                }
        } else {
            IRCUtils.sendMessage(user, network, channel, "help (command) - Run list for available commands, generally a + before something means editing it, and a - means removing it. None means adding it. Time is in [time](s/m/h/d/w) format. [] is a required argument. () is an optional argument.", prefix);
        }
    }
}
