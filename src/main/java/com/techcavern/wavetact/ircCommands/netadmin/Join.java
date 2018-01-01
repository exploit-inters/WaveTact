/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.techcavern.wavetact.ircCommands.netadmin;

import com.techcavern.wavetact.annot.IRCCMD;
import com.techcavern.wavetact.objects.IRCCommand;
import com.techcavern.wavetact.utils.DatabaseUtils;
import com.techcavern.wavetact.utils.GeneralUtils;
import com.techcavern.wavetact.utils.IRCUtils;
import com.techcavern.wavetact.utils.Registry;
import org.jooq.Record;
import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.User;

import static com.techcavern.wavetactdb.Tables.NETWORKS;


/**
 * @author jztech101
 */
@IRCCMD
public class Join extends IRCCommand {

    public Join() {
        super(GeneralUtils.toArray("join jo"), 20, "join (+)[channel]", "Joins a channel", false);
    }

    @Override
    public void onCommand(String command, User user, PircBotX network, String prefix, Channel channel, boolean isPrivate, int userPermLevel, String... args) throws Exception {
        boolean permanent = false;
        if (args[0].startsWith("+")) {
            args[0] = args[0].replace("+", "");
            permanent = true;
        }
        if (permanent) {
            Record netRecord = DatabaseUtils.getNetwork(IRCUtils.getNetworkNameByNetwork(network));
            netRecord.setValue(NETWORKS.CHANNELS, DatabaseUtils.getNetwork(IRCUtils.getNetworkNameByNetwork(network)).getValue(NETWORKS.CHANNELS) + ", " + args[0]);
            DatabaseUtils.updateNetwork(netRecord);
        }
        Registry.messageQueue.get(network).add("JOIN :" + args[0]);
    }
}
