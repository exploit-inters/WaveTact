package com.techcavern.wavetact.commands.minecraft;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.techcavern.wavetact.annot.CMD;
import com.techcavern.wavetact.annot.GenCMD;
import com.techcavern.wavetact.utils.GeneralRegistry;
import com.techcavern.wavetact.utils.GeneralUtils;
import com.techcavern.wavetact.utils.IRCUtils;
import com.techcavern.wavetact.utils.objects.GenericCommand;
import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.User;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

@CMD
@GenCMD
public class MCAccountInfo extends GenericCommand {

    public MCAccountInfo() {
        super(GeneralUtils.toArray("mcaccountinfo mcuserinfo mcpremium mcuuid mcmigrated"), 0, "mcaccountinfo [user]", "Gets Info on a Minecraft Account");
    }

    @Override
    public void onCommand(User user, PircBotX Bot, Channel channel, boolean isPrivate, int UserPermLevel, String... args) throws Exception {
        URL url = new URL("https://api.mojang.com/profiles/minecraft");

        String payload="[\"" + args[0] +"\",\"egewewhewhwe\"]";
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream(), "UTF-8");
        writer.write(payload);
        writer.close();
        BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String result = "";
        String line = "";
        while ((line = br.readLine()) != null) {
            result += line.replaceAll("\n", " ") + "\n";
        }
        br.close();
        connection.disconnect();
        JsonArray mcapipre = new JsonParser().parse(result).getAsJsonArray();
        if (mcapipre.size() > 0) {
            JsonObject mcapi = mcapipre.get(0).getAsJsonObject();
            String User = mcapi.get("name").getAsString();
            String UUID = mcapi.get("id").getAsString();
            String Premium = "True";
            if(mcapi.get("demo")!= null && mcapi.get("legacy").getAsString().equalsIgnoreCase("True")) {
                Premium = "False";
            }
            String Migrated = "True";
            if(mcapi.get("legacy")!= null && mcapi.get("legacy").getAsString().equalsIgnoreCase("True")) {
                Migrated = "False";
            }
            IRCUtils.sendMessage(user, channel, User + " - " + "UUID: " + UUID + " - " + "Paid: " + Premium + " - " + "Mojang Account: " + Migrated, isPrivate);
        } else {
            IRCUtils.sendError(user, "user does not exist");
        }
    }

}
