package com.techcavern.wavetact.ircCommands.dnsinfo;

import com.techcavern.wavetact.annot.IRCCMD;
import com.techcavern.wavetact.objects.IRCCommand;
import com.techcavern.wavetact.utils.ErrorUtils;
import com.techcavern.wavetact.utils.GeneralUtils;
import com.techcavern.wavetact.utils.IRCUtils;
import org.apache.http.conn.util.InetAddressUtils;
import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.User;

@IRCCMD
public class GetIP extends IRCCommand {

    public GetIP() {
        super(GeneralUtils.toArray("getip ip"), 0, "getip (+)[domain]", "gets IP Address of domain", false);
    }

    @Override
    public void onCommand(String command, User user, PircBotX network, String prefix, Channel channel, boolean isPrivate, int userPermLevel, String... args) throws Exception {
        boolean IPv6Priority = false;
        if(args[0].startsWith("+")){
            IPv6Priority = true;
            args[0] = args[0].replaceFirst("\\+","");
        }
        String IP = GeneralUtils.getIP(args[0], network, IPv6Priority);
        if (IP == null) {
            ErrorUtils.sendError(user, "Host Unreachable");
        } else {
            IRCUtils.sendMessage(user, network, channel, IP, prefix);
        }

    }
}