package info.techcavern.wavetact.ircCommands.fun;

import info.techcavern.wavetact.annot.IRCCMD;
import info.techcavern.wavetact.objects.IRCCommand;
import info.techcavern.wavetact.utils.DatabaseUtils;
import info.techcavern.wavetact.utils.GeneralUtils;
import info.techcavern.wavetact.utils.IRCUtils;
import info.techcavern.wavetactdb.tables.Channelproperty;
import org.jooq.Record;
import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.User;

/**
 * @author jztech101
 */
@IRCCMD
public class SomethingAwesome extends IRCCommand {

    public SomethingAwesome() {
        super(GeneralUtils.toArray("somethingawesome sa awesome something"), 1, null, "Something AWESOME!", false);
    }

    @Override
    public void onCommand(String command, User user, PircBotX network, String prefix, Channel channel, boolean isPrivate, int userPermLevel, String... args) throws Exception {
        if (IRCUtils.checkIfCanKick(channel, network, user)) {
            IRCUtils.sendKick(network.getUserBot(), user, network, channel, "┻━┻ ︵ ¯\\_(ツ)_/¯ ︵ ┻━┻");
        } else {
            IRCUtils.sendAction(user, network, channel, "kicks " + IRCUtils.noPing(user.getNick()) + " (┻━┻ ︵ ¯\\_(ツ)_/¯ ︵ ┻━┻)", prefix);
        }
        Record rec = DatabaseUtils.getChannelProperty(IRCUtils.getNetworkNameByNetwork(network), channel.getName(), "funmsg");
        if (rec != null && rec.getValue(Channelproperty.CHANNELPROPERTY.VALUE).equalsIgnoreCase("true")) {
            IRCUtils.sendMessage(user, network, channel, GeneralUtils.prism("    __   ______ ______  ___     ___  ____  ________"), prefix);
            IRCUtils.sendMessage(user, network, channel, GeneralUtils.prism("   / /  /  _/ //_/ __/ / _ |   / _ )/ __ \\/ __/ __/"), prefix);
            IRCUtils.sendMessage(user, network, channel, GeneralUtils.prism("  / /___/ // ,< / _/  / __ |  / _  / /_/ /\\ \\_\\ \\  "), prefix);
            IRCUtils.sendMessage(user, network, channel, GeneralUtils.prism(" /____/___/_/|_/___/ /_/ |_| /____/\\____/___/___/"), prefix);
            IRCUtils.sendMessage(user, network, channel, GeneralUtils.prism("                                                  "), prefix);
        }
    }
}
