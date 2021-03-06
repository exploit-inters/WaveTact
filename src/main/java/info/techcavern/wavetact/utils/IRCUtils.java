package info.techcavern.wavetact.utils;

import info.techcavern.wavetact.objects.IRCCommand;
import info.techcavern.wavetactdb.tables.Channelproperty;
import info.techcavern.wavetact.objects.IRCCommand;
import org.apache.commons.lang3.StringUtils;
import org.jooq.Record;
import org.jooq.Result;
import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.User;
import org.pircbotx.UserLevel;
import org.pircbotx.exception.DaoException;
import org.pircbotx.hooks.WaitForQueue;
import org.pircbotx.hooks.events.WhoisEvent;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static info.techcavern.wavetactdb.Tables.*;


public class IRCUtils {
    public static void setMode(Channel channelObject, PircBotX networkObject, String modeToSet, String hostmask) {
        if (hostmask != null) {
            Registry.messageQueue.get(networkObject).add("MODE " + channelObject.getName() + " " + modeToSet + " " + hostmask);
        } else {
            Registry.messageQueue.get(networkObject).add("MODE " + channelObject.getName() + " " + modeToSet);
        }
    }

    public static WhoisEvent WhoisEvent(PircBotX network, String userObject, boolean useCache) {
        WhoisEvent WhoisEvent = Registry.whoisEventCache.get(network).get(userObject);
        if (useCache) {
            if (WhoisEvent != null) {
                return WhoisEvent;
            } else {
                WhoisEvent = Registry.whoisEventCache.get(network).get(userObject);
                if (WhoisEvent != null) {
                    return WhoisEvent;
                } else {
                    if (Registry.lastWhois.get(network).equalsIgnoreCase(userObject)) {
                        int i = 0;
                        while (WhoisEvent == null && i < 10) {
                            try {
                                TimeUnit.MILLISECONDS.sleep(100);
                            } catch (Exception e) {
                            }
                            WhoisEvent = Registry.whoisEventCache.get(network).get(userObject);
                            i++;
                        }
                        if (WhoisEvent != null)
                            return WhoisEvent;
                    }
                }
            }
        } else if (WhoisEvent != null) {
            Registry.whoisEventCache.get(network).remove(WhoisEvent);
        }
        Registry.lastWhois.put(network, userObject);
        WaitForQueue waitForQueue = new WaitForQueue(network);
        try {
            Registry.messageQueue.get(network).add("WHOIS " + userObject + " " + userObject);
            WhoisEvent = waitForQueue.waitFor(WhoisEvent.class);
            waitForQueue.close();
        } catch (InterruptedException | NullPointerException ex) {
            ex.printStackTrace();
            WhoisEvent = null;
        }
        if (WhoisEvent == null || !WhoisEvent.isExists() || !WhoisEvent.getNick().equalsIgnoreCase(userObject))
            return null;
        else
            Registry.whoisEventCache.get(network).put(userObject, WhoisEvent);
        return WhoisEvent;
    }

    public static void sendMessage(String userObject, PircBotX networkObject, Channel channelObject, String message, String prefix) {
        for (int i = 0; i < message.length(); i += 350) {
            String messageToSend = message.substring(i, Math.min(message.length(), i + 350));
            if (channelObject != null) {
                if (!messageToSend.isEmpty()) {
                    Registry.messageQueue.get(networkObject).add("PRIVMSG " + prefix + channelObject.getName() + " :" + messageToSend);
                    if (prefix.isEmpty())
                        sendRelayMessage(networkObject, channelObject, noPing(networkObject.getNick()) + ": " + messageToSend);
                }
            } else {
                Registry.messageQueue.get(networkObject).add(("PRIVMSG " + userObject + " :" + messageToSend));
            }
        }
    }
    public static void sendMessage(User userObject, PircBotX networkObject, Channel channelObject, String message, String prefix) {
        sendMessage(userObject.getNick(), networkObject, channelObject, message, prefix);
    }
    public static void sendMessage(User userObject, PircBotX networkObject, String message, String prefix) {
        sendMessage(userObject.getNick(), networkObject, null, message, prefix);
    }
    public static void sendMessage(String userObject, PircBotX networkObject, String message, String prefix) {
        sendMessage(userObject, networkObject, null, message, prefix);
    }
    public static void setTopic(PircBotX networkObject, Channel channelObject, String message) {
        if (channelObject != null){
            if (!message.isEmpty()) {
                Registry.messageQueue.get(networkObject).add("TOPIC " + channelObject.getName() + " :" + message);
                   sendRelayTopic(networkObject, channelObject, message);
            }
    }
    }
    public static void sendRelayTopic(PircBotX networkObject, Channel channel, String msg){
        Set<String[]> toBeRelayed = new HashSet<>();
        for (Record relay : DatabaseUtils.getRelays()) {
            String[] channels = relay.getValue(RELAYS.VALUE).split(",");
            for (String chan : channels) {
                String[] netchan = chan.split("\\.");
                if (IRCUtils.getNetworkNameByNetwork(networkObject).equalsIgnoreCase(netchan[0]) && channel.getName().equalsIgnoreCase(netchan[1])) {
                    toBeRelayed.add(channels);
                    break;
                }
            }
        }
        for (String[] channels : toBeRelayed) {
            for (String chan : channels) {
                String[] netchan = chan.split("\\.");
                if (!(IRCUtils.getNetworkNameByNetwork(networkObject).equalsIgnoreCase(netchan[0]) && channel.getName().equalsIgnoreCase(netchan[1]))) {
                    Registry.messageQueue.get(IRCUtils.getNetworkByNetworkName(netchan[0])).add("TOPIC " + netchan[1] + " :" + msg);
                }
            }
        }
    }

    public static String getCommandChar(PircBotX network,Channel channel) {
        Record commandcharRecord;
        String commandchar = null;
        if (channel != null) {
            commandcharRecord = DatabaseUtils.getChannelProperty(IRCUtils.getNetworkNameByNetwork(network), channel.getName(), "commandchar");
            if (commandcharRecord != null) {
                return commandcharRecord.getValue(CHANNELPROPERTY.VALUE);
            }
        }
            commandcharRecord = DatabaseUtils.getNetworkProperty(IRCUtils.getNetworkNameByNetwork(network), "commandchar");
            if (commandcharRecord != null) {
                return commandcharRecord.getValue(NETWORKPROPERTY.VALUE);
            }
        return null;
    }
    public static void sendKick(User userObject, User recipientObject, PircBotX networkObject, Channel channelObject, String message) {
        if (channelObject != null) {
                if (!message.isEmpty()) {
                    Registry.messageQueue.get(networkObject).add("KICK " + channelObject.getName() + " " + recipientObject.getNick() + " :" + message);
                    //       sendRelayMessage(networkObject, channelObject, "* " + userObject.getNick() + " kicks " + recipientObject.getNick() + " (" + messageToSend + ")");
                }
            }
        }

    public static void sendRelayMessage(PircBotX networkObject, Channel channel, String msg) {
        sendRelayMessage(networkObject, channel, msg, null);
    }
    public static void addVoice(PircBotX networkObject, Channel channel,User user) {
        if((channel.getOps().contains(networkObject.getUserBot()) ||channel.getSuperOps().contains(networkObject.getUserBot()) || channel.getHalfOps().contains(networkObject.getUserBot()) || channel.getOwners().contains(networkObject.getUserBot())) && !(channel.getOps().contains(user) ||channel.getSuperOps().contains(user) || channel.getHalfOps().contains(user) || channel.getOwners().contains(user))){
            Record rec = DatabaseUtils.getChannelProperty(IRCUtils.getNetworkNameByNetwork(networkObject), channel.getName(), "autovoice");
            if (rec != null && rec.getValue(Channelproperty.CHANNELPROPERTY.VALUE).equalsIgnoreCase("true")) {
                Record rec2 = DatabaseUtils.getChannelUserProperty(IRCUtils.getNetworkNameByNetwork(networkObject), channel.getName(), PermUtils.authUser(networkObject, user.getNick()), "autovoice");
                if (rec2 != null && rec.getValue(CHANNELUSERPROPERTY.VALUE).equalsIgnoreCase("false")) {
                    return;
                }else {
                    Record rec3 = DatabaseUtils.getVoice(IRCUtils.getNetworkNameByNetwork(networkObject), channel.getName(), user.getNick());
                    if (rec3 != null) {
                        rec3.setValue(VOICES.TIME, System.currentTimeMillis() + GeneralUtils.getMilliSeconds("1h"));
                        DatabaseUtils.updateVoiceTime(rec3);
                    } else {
                        IRCUtils.setMode(channel,networkObject, "+v", user.getNick());
                        DatabaseUtils.addVoice(IRCUtils.getNetworkNameByNetwork(networkObject), channel.getName(), user.getNick(), System.currentTimeMillis() + GeneralUtils.getMilliSeconds("1h"));
                    }
                }
            }else{
                return;
            }
        }else{
            return;
        }
    }
    public static void removeVoice(PircBotX networkObject, Channel channel,User user) {
        DatabaseUtils.removeVoice(IRCUtils.getNetworkNameByNetwork(networkObject), channel.getName(), user.getNick());
    }
    public static void removeVoice(PircBotX networkObject,User user) {
        DatabaseUtils.removeVoice(IRCUtils.getNetworkNameByNetwork(networkObject), user.getNick());
    }
    public static void updateVoice(PircBotX networkObject,String oldNick, User user) {
        Result<Record> records = DatabaseUtils.getVoicedNicks(IRCUtils.getNetworkNameByNetwork(networkObject), oldNick);
        for(Record rec:records){
            rec.setValue(VOICES.NICK, user.getNick());
            DatabaseUtils.updateVoiceNick(rec);
        }
    }

        public static void sendRelayMessage(PircBotX networkObject, Channel channel, String msg, User user) {
        Set<String[]> toBeRelayed = new HashSet<>();
        for (Record relay : DatabaseUtils.getRelays()) {
            String[] channels = relay.getValue(RELAYS.VALUE).split(",");
            for (String chan : channels) {
                String[] netchan = chan.split("\\.");
                if (channel == null) {
                    if (getNetworkNameByNetwork(networkObject).equalsIgnoreCase(netchan[0]) && user.getChannels().contains(IRCUtils.getChannelbyName(networkObject,netchan[1]))) {
                        toBeRelayed.add(channels);
                        break;
                    }
                } else {
                    if (getNetworkNameByNetwork(networkObject).equalsIgnoreCase(netchan[0]) && channel.getName().equalsIgnoreCase(netchan[1])) {
                        toBeRelayed.add(channels);
                        break;
                    }
                }
            }
        }
        for (String[] channels : toBeRelayed) {
            for (String chan : channels) {
                String[] netchan = chan.split("\\.");
                if (channel == null) {
                    if (!getNetworkNameByNetwork(networkObject).equalsIgnoreCase(netchan[0])) {
                        Registry.messageQueue.get(getNetworkByNetworkName(netchan[0])).add("PRIVMSG " + netchan[1] + " :[" + getNetworkNameByNetwork(networkObject) + "] " + msg);
                    }
                } else {
                    if (!(getNetworkNameByNetwork(networkObject).equalsIgnoreCase(netchan[0]) && channel.getName().equalsIgnoreCase(netchan[1]))) {
                        Registry.messageQueue.get(getNetworkByNetworkName(netchan[0])).add("PRIVMSG " + netchan[1] + " :[" + getNetworkNameByNetwork(networkObject) + "] " + msg);
                    }
                }
            }
        }
    }

    public static void sendAction(User userObject, PircBotX networkObject, Channel channelObject, String message, String prefix) {
        if (channelObject != null) {
            Registry.messageQueue.get(networkObject).add("PRIVMSG " + prefix + channelObject.getName() + " :\u0001ACTION " + message + "\u0001");
            if (prefix.isEmpty())
                sendRelayMessage(networkObject, channelObject, "* " + noPing(networkObject.getNick()) + " " + message);
        } else {
            Registry.messageQueue.get(networkObject).add("PRIVMSG " + userObject.getNick() + " :\u0001ACTION " + message + "\u0001");
        }
    }

    public static void sendAction(PircBotX networkObject, Channel channelObject, String message, String prefix) {
        sendAction(null, networkObject, channelObject, message, prefix);
    }

    public static void sendMessage(PircBotX networkObject, Channel channelObject, String message, String prefix) {
        sendMessage((String) null, networkObject, channelObject, message, prefix);

    }


    public static String getPrefix(PircBotX network, String fullChannelName) {
        String prefix = String.valueOf(fullChannelName.charAt(0));
        if (network.getServerInfo().getStatusMessage().contains(prefix)) {
            return prefix;
        }
        return "";
    }

    public static boolean checkIfCanKick(Channel channel, PircBotX network, User user) {
        Record relaybotsplit = DatabaseUtils.getChannelUserProperty(IRCUtils.getNetworkNameByNetwork(network), channel.getName(), PermUtils.authUser(network, user.getNick()), "relaybotsplit");
        if (relaybotsplit == null) {
            if (channel != null && channel.getUserLevels(network.getUserBot()).contains(UserLevel.OP) && !channel.isOwner(user) && !channel.isSuperOp(user)) {
                return true;
            } else if (channel != null && channel.getUserLevels(network.getUserBot()).contains(UserLevel.SUPEROP) && !channel.isOwner(user) && !channel.isSuperOp(user)) {
                return true;
            } else return channel != null && channel.getUserLevels(network.getUserBot()).contains(UserLevel.OWNER);
        } else {
            return false;
        }
    }

    public static String getHostmask(PircBotX network, String userObject, boolean isBanmask) {
        String hostmask = getIRCHostmask(network, userObject, isBanmask);
        if (hostmask == null) {
            hostmask = getWhoisHostmask(network, userObject, isBanmask);
        }
        return hostmask;
    }

    public static String getWhoisHostmask(PircBotX network, String userObject, boolean isBanmask) {
        String hostmask;
        WhoisEvent whois = WhoisEvent(network, userObject, true);
        if (whois != null) {
            String hostname = whois.getHostname();
            String Login = whois.getLogin();
            if (isBanmask) {
                hostmask =  getBanmask(hostname, Login);
            } else {
                hostmask = getLoginmask(hostname, Login);
            }
            hostmask = hostmask.replace(" ", "");
        } else {
            hostmask = null;
        }
        return hostmask;
    }

    public static String getBanmask(String hostname, String ident){
        String hostmask;
        if (!ident.startsWith("~")) {
            hostmask = "*!" + ident + "@" + hostname;
        } else {
            hostmask = "*!*@" + hostname;
        }
        return hostmask;
    }

    public static String getLoginmask(String hostname, String ident){
        return "*!" + ident + "@" + hostname;
    }


    public static void sendGlobal(String message, User user) {
        Iterator iterator = Registry.networks.inverse().keySet().iterator();
        while (iterator.hasNext())
            sendNetworkGlobal(message, (PircBotX) iterator.next(), user, true);
    }

    public static void sendNetworkGlobal(String message, PircBotX network, User user, boolean isGlobal) {
        String beginning = "[Network Notice] ";
        if (isGlobal) {
            beginning = "[Global Notice] ";
        }
        for (Channel channel : network.getUserBot().getChannels()) {
            if (user != null)
                channel.send().message(beginning + user.getNick() + " - " + message);
            else
                channel.send().message(beginning + message);
        }
    }

    public static String getIRCHostmask(PircBotX network, String userObject, boolean isBanmask) {
        User user = getUserByNick(network, userObject);
        String hostmask;
        if (user != null && user.getHostname() != null && user.getLogin() != null) {
            String hostname = user.getHostname();
            String Login = user.getLogin();
            if (isBanmask) {
                hostmask =  getBanmask(hostname, Login);
            } else {
                hostmask = getLoginmask(hostname, Login);
            }
            hostmask = hostmask.replace(" ", "");
        } else {
            hostmask = null;
        }
        return hostmask;
    }

    public static String getHost(PircBotX network, String userObject) {
        String host = "";
        User use = getUserByNick(network, userObject);
        if (use != null && use.getHostname() != null) {
            host = use.getHostname();
        } else {
            WhoisEvent whois = WhoisEvent(network, userObject, true);
            if (whois != null) {
                host = whois.getHostname();
            } else {
                host = "";
            }
        }
        return host;
    }

    public static User getUserByNick(PircBotX networkObject, String Nick) {
        try {
            User userObject = networkObject.getUserChannelDao().getUser(Nick);
            if (!userObject.getHostmask().isEmpty()) {
                return userObject;
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    public static Channel getChannelbyName(PircBotX networkObject, String channelName) {
        try {
            return networkObject.getUserChannelDao().getChannel(channelName);
        } catch (DaoException|NullPointerException e) {
            return null;
        }

    }

    public static IRCCommand getCommand(String Command, String Network, String Channel) {
        IRCCommand cmd = getCustomCommand(Command, Network, Channel);
        if (cmd != null) {
            return cmd;
        } else {
            return Registry.ircCommands.get(Command);
        }
    }

    public static IRCCommand getCustomCommand(String Command, String Network, String Channel) {
        Record cmd = DatabaseUtils.getCustomCommand(Network, Channel, Command);
        if (cmd != null) {
            class SimpleCommand extends IRCCommand {
                public SimpleCommand() {
                    super(GeneralUtils.toArray(cmd.getValue(CUSTOMCOMMANDS.COMMAND)), cmd.getValue(CUSTOMCOMMANDS.PERMLEVEL), cmd.getValue(CUSTOMCOMMANDS.COMMAND), "Custom Command", false);

                }

                @Override
                public void onCommand(String command, User user, PircBotX network, String prefix, Channel channel, boolean isPrivate, int userPermLevel, String... args) throws Exception {
                    String action = cmd.getValue(CUSTOMCOMMANDS.VALUE);
                    String[] message = StringUtils.split(action, " ");
                    int i = 0;
                    for (String g : message) {
                        if (g.contains("$")) {
                            char[] chars = g.toCharArray();
                            for (int v = 0; v < chars.length; v++) {
                                if (chars[v] == ("$").charAt(0) &&
                                        GeneralUtils.isInteger(chars[v + 1]) &&
                                        Character.getNumericValue(chars[v+1]) <= args.length+1 && Character.getNumericValue(chars[v+1]) > 0) {
                                    action = action.replace(String.valueOf(chars[v]) + String.valueOf(chars[v + 1]), args[Integer.valueOf(String.valueOf(chars[v + 1])) - 1]);
                                    try {
                                        if (Character.getNumericValue(chars[v + 1]) > i) {
                                            i++;
                                        }
                                    } catch (Exception e) {
                                    }
                                }
                            }
                        }
                    }
                    action = action.replace("$*", GeneralUtils.buildMessage(i, args.length, args));
                    String responseprefix = DatabaseUtils.getNetworkProperty(IRCUtils.getNetworkNameByNetwork(network), "commandchar").getValue(NETWORKPROPERTY.VALUE);
                    if (action.startsWith(responseprefix)) {
                        action = action.replace(responseprefix, "");
                    }
                    if (cmd.getValue(CUSTOMCOMMANDS.ISACTION)) {
                        IRCUtils.sendAction(user, network, channel,  action, prefix);
                    } else {
                        if(!GeneralUtils.isFirstCharLetter(action)) {
                            IRCUtils.sendMessage(user, network, channel, "[" + IRCUtils.noPing(user.getNick()) + "] " + action, prefix);
                        }else{
                            IRCUtils.sendMessage(user, network, channel, action, prefix);
                        }
                    }


                }
            }
            return new SimpleCommand();
        } else
            return null;
    }


    public static PircBotX getNetworkByNetworkName(String name) {
        return Registry.networks.get(name);
    }

    public static String getNetworkNameByNetwork(PircBotX network) {
        return Registry.networks.inverse().get(network);
    }

    public static Channel getMsgChannel(Channel channel, boolean isPrivate) {
        if (isPrivate) {
            return null;
        } else {
            return channel;
        }
    }


    public static void sendLogChanMsg(PircBotX network, String message) {
        Record pmlog = DatabaseUtils.getNetworkProperty(getNetworkNameByNetwork(network), "pmlog");
        if (pmlog != null)
            sendMessage(pmlog.getValue(NETWORKPROPERTY.VALUE), network, message, "");
    }

    public static String noPing(String original) {
        /**
        char[] originChars = original.toCharArray();
        for (int i = 0; i < originChars.length; i++) {
            if (Registry.charReplacements.get(String.valueOf(originChars[i])) != null) {
                original = original.replaceFirst(String.valueOf(originChars[i]), Registry.charReplacements.get(String.valueOf(originChars[i])));
                break;
            }
        }
         **/
        return original.substring(0,1)+"\u200B"+original.substring(1);
    }

    public static void sendError(User userObject, PircBotX networkObject, Channel channelObject, String message, String prefix) {
        if (channelObject != null) {
            Record verbose = DatabaseUtils.getChannelProperty(IRCUtils.getNetworkNameByNetwork(networkObject), channelObject.getName(), "verboseerrors");
            if (verbose != null && verbose.getValue(CHANNELPROPERTY.VALUE).equalsIgnoreCase("false")) {
                sendNotice(userObject, networkObject, null, message, prefix);
            } else {
                sendMessage(userObject, networkObject, channelObject, message, prefix);
            }
        } else {
            sendMessage(userObject, networkObject, channelObject, message, prefix);
        }
    }

    public static void sendNotice(User userObject, PircBotX networkObject, Channel channelObject, String message, String prefix) {
        if (channelObject != null) {
            Registry.messageQueue.get(networkObject).add("NOTICE " + prefix + channelObject.getName() + " :" + message);
        } else {
            Registry.messageQueue.get(networkObject).add("NOTICE " + userObject.getNick() + " :" + message);
        }
    }
}

