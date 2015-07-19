package com.techcavern.wavetact.ircCommands.reference;

import com.techcavern.wavetact.annot.IRCCMD;
import com.techcavern.wavetact.objects.IRCCommand;
import com.techcavern.wavetact.utils.GeneralUtils;
import com.techcavern.wavetact.utils.IRCUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.User;

@IRCCMD
public class Wiki extends IRCCommand {

    public Wiki() {
        super(GeneralUtils.toArray("wiki wi mcwiki mcw mcmodwiki mcmw"), 0, "wiki (result #) [query wiki]", "Searches wikis for something", false);
    }

    @Override
    public void onCommand(String command, User user, PircBotX network, String prefix, Channel channel, boolean isPrivate, int userPermLevel, String... args) throws Exception {
        int ArrayIndex = 1;
        if (GeneralUtils.isInteger(args[0])) {
            ArrayIndex = Integer.parseInt(args[0]);
            args = ArrayUtils.remove(args, 0);
        }
        String url = "https://en.wikipedia.org/w/";
        String userurl = "https://en.wikipedia.org/wiki/";
        String title = GeneralUtils.getMediaWikiTitle(url, StringUtils.join(args, "%20"), ArrayIndex);
        String content = GeneralUtils.getMediaWikiContentFromTitle(url, title);
        String urljoin = "%20";
        if (command.equalsIgnoreCase("mcmodwiki") || command.equalsIgnoreCase("mcmw")) {
            url = "http://ftb.gamepedia.com/";
            userurl = url;
            title = GeneralUtils.getMediaWikiTitle(url, StringUtils.join(args, "%20"), ArrayIndex);
            content = GeneralUtils.getMediaWikiContentFromTitle(url, title);
            urljoin = "_";
            if (title == null || content == null) {
                url = "http://ftbwiki.org/";
                title = GeneralUtils.getMediaWikiTitle(url, StringUtils.join(args, "%20"), ArrayIndex);
                content = GeneralUtils.getMediaWikiContentFromTitle(url, title);
            }
        } else if (command.equalsIgnoreCase("mcwiki") || command.equalsIgnoreCase("mcw")) {
            url = "http://minecraft.gamepedia.com/";
            userurl = url;
            title = GeneralUtils.getMediaWikiTitle(url, StringUtils.join(args, "%20"), ArrayIndex);
            content = GeneralUtils.getMediaWikiContentFromTitle(url, title);
            urljoin = "_";
        }
        if (title != null && content != null) {
            IRCUtils.sendMessage(user, network, channel, "[" + title + "] " + content + " - " + GeneralUtils.shortenURL(userurl + title.replace(" ", urljoin)), prefix);
        } else if (ArrayIndex > 1) {
            IRCUtils.sendError(user, "Result #" + ArrayIndex + " does not exist");
        } else {
            IRCUtils.sendError(user, "No results found");
        }
    }
}
