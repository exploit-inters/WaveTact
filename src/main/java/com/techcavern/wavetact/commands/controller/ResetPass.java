package com.techcavern.wavetact.commands.controller;

import com.techcavern.wavetact.annot.CMD;
import com.techcavern.wavetact.annot.ConCMD;
import com.techcavern.wavetact.utils.*;
import com.techcavern.wavetact.utils.databaseUtils.AccountUtils;
import com.techcavern.wavetact.utils.objects.Account;
import com.techcavern.wavetact.utils.objects.GenericCommand;
import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.User;

@CMD
@ConCMD
public class ResetPass extends GenericCommand {

    public ResetPass() {
        super(GeneralUtils.toArray("resetpassword resetpass"), 9001, "setpass [user] [newpass]", "Resets password", false);
    }

    @Override
    public void onCommand(User user, PircBotX network, String prefix, Channel channel, boolean isPrivate, int userPermLevel, String... args) throws Exception {
        if (!PermUtils.checkIfAccountEnabled(network)) {
            ErrorUtils.sendError(user, "This network is set to " + GetUtils.getAuthType(network) + " authentication");
            return;
        }
        Account acc = AccountUtils.getAccount(args[0]);
        if (acc != null) {
            acc.setAuthPassword(Constants.encryptor.encryptPassword(args[1]));
            AccountUtils.saveAccounts();
            IRCUtils.sendMessage(user, network, channel, "Password changed successfully", prefix);
        } else {
            ErrorUtils.sendError(user, "User does not exist");
        }

    }
}

