package com.techcavern.wavetact.utils;

import java.nio.charset.Charset;
import java.util.List;

import org.pircbotx.Channel;
import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;
import org.pircbotx.Configuration.Builder;
import org.pircbotx.User;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.techcavern.wavetact.commands.Ban;
import com.techcavern.wavetact.commands.BasicChanOp;
import com.techcavern.wavetact.commands.BasicCommands;
import com.techcavern.wavetact.commands.CheckUserLevel;
import com.techcavern.wavetact.commands.ControllerCommands;
import com.techcavern.wavetact.commands.Quiet;
import com.techcavern.wavetact.commands.Topic;
import com.techcavern.wavetact.events.HighFive;
//import com.techcavern.wavetact.commands.TestCommand;
import com.techcavern.wavetact.events.kickrejoin;
import org.pircbotx.output.OutputChannel;
import org.pircbotx.output.OutputUser;

public class IRCUtils{
    public static final Gson gson = new GsonBuilder().setPrettyPrinting().create(); 
	public static PircBotX createbot(String g, String Name, List<String> channels,String Nick, String Server) throws Exception{
    System.out.println("Configuring "+Name);
    Builder<PircBotX> Net = new Configuration.Builder<PircBotX>();
    Net.setName(Nick);
    Net.setLogin("WaveTact");
    Net.setEncoding(Charset.isSupported("UTF-8") ? Charset.forName("UTF-8") : Charset.defaultCharset());
    Net.setServer(Server, 6667);
	for (int i = 0; i < channels.size(); i++){
 		Net.addAutoJoinChannel(channels.get(i));
	}			
    Net.getListenerManager().addListener(new MessageListener());
    Net.getListenerManager().addListener(new HighFive());
    Net.getListenerManager().addListener(new kickrejoin());
    if(g != null){
    Net.setNickservPassword(g);
    }
  //  Net.getListenerManager().addListener(new TestCommand());
    PircBotX Bot = new PircBotX(Net.buildConfiguration());
    return Bot;
	}
	public static User getUserByNick(Channel c, String n)
    {
        for (User u : c.getUsers())
        {
            if (u.getNick().equalsIgnoreCase(n))
                return u;
        }
        return null;
    }
    	public static void setMode (Channel c, PircBotX b, String d, User u){
    		OutputChannel o = new OutputChannel(b, c);
		if (u != null){
		o.setMode(d, u.getHostmask());
		} else {
		o.setMode(d);	
		}
    	}
    	public static void SendNotice (PircBotX b, User u, String s){
    		OutputUser x = new OutputUser(b, u);
		x.notice(s);
		}
        
        public static void RegisterCommands(){
             System.out.println("Registering Commands");
                new Ban();
                BasicChanOp x = new BasicChanOp();
                x.new kick();
                x.new mode();
                x.new owner();
                x.new op();
                x.new part();
                x.new protect();
                x.new voice();
                BasicCommands y = new BasicCommands();
                y.new ping();
                y.new pong();
                y.new say();
                y.new act();
                y.new SomethingAwesome();
                y.new Source();
                new Topic();
                new CheckUserLevel();
                ControllerCommands z = new ControllerCommands();
                z.new join();
                new Quiet();
        }
    	}

	


