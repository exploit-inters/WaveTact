package com.techcavern.wavetact.commands.minecraft;

import com.techcavern.wavetact.annot.CMD;
import com.techcavern.wavetact.annot.GenCMD;
import com.techcavern.wavetact.utils.GeneralUtils;
import com.techcavern.wavetact.utils.IRCUtils;
import com.techcavern.wavetact.utils.objects.GenericCommand;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.User;

@CMD
@GenCMD
public class MCDrama extends GenericCommand {

    public MCDrama() {
        super(GeneralUtils.toArray("mcdrama"), 0, null, "Displays Minecraft Drama");
    }

    @Override
    public void onCommand(User user, PircBotX Bot, Channel channel, boolean isPrivate, int UserPermLevel, String... args) throws Exception {
        Document doc = Jsoup.connect("http://asie.pl/drama.php?2").get();
        String c = doc.select("h1").text();
        IRCUtils.sendMessage(user, channel, c, isPrivate);
    }
}