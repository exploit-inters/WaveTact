package info.techcavern.wavetact.ircCommands.misc;

import info.techcavern.wavetact.annot.IRCCMD;
import info.techcavern.wavetact.objects.IRCCommand;
import info.techcavern.wavetact.utils.*;
import org.jooq.Record;
import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.User;

@IRCCMD
public class Tell extends IRCCommand {

    public Tell() {
        super(GeneralUtils.toArray("tell"), 1, "tell [user] [message]", "Tells a user a message the next time they speak", false);
    }

    @Override
    public void onCommand(String command, User user, PircBotX network, String prefix, Channel channel, boolean isPrivate, int userPermLevel, String... args) throws Exception {
        Record relaybotsplit = null;
        if (!isPrivate)
            relaybotsplit = DatabaseUtils.getChannelUserProperty(IRCUtils.getNetworkNameByNetwork(network), channel.getName(), PermUtils.authUser(network, user.getNick()), "relaybotsplit");
        if (relaybotsplit == null) {
            String sender = PermUtils.authUser(network, user.getNick());
            String recipient;
            if (Registry.authedUsers.get(network).keySet().stream().filter(key -> Registry.authedUsers.get(network).get(key).equals(args[0].toLowerCase())).toArray().length > 0) {
                recipient = args[0].toLowerCase();
            } else {
                recipient = PermUtils.authUser(network, args[0]);
            }
            if (recipient == null) {
                IRCUtils.sendError(user, network, channel, "Recipient must be identified", prefix);
                return;
            }
            DatabaseUtils.addTellMessage(IRCUtils.getNetworkNameByNetwork(network), sender, recipient, GeneralUtils.buildMessage(1, args.length, args));
            IRCUtils.sendMessage(user, network, channel, "Latent Message Sent", prefix);
        }
    }

}
