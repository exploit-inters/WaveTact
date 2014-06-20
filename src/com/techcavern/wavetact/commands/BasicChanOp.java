/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.techcavern.wavetact.commands;

import com.techcavern.wavetact.utils.GeneralRegistry;
import com.techcavern.wavetact.utils.IRCUtils;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.MessageEvent;

public class BasicChanOp extends ListenerAdapter<PircBotX> {
		public void onMessage(MessageEvent<PircBotX> event) throws Exception{
                    String[] messageParts = event.getMessage().split(" ");
			 if (messageParts[0].equalsIgnoreCase((GeneralRegistry.CommandChar + "kick"))){
	            	event.getChannel().send().kick(IRCUtils.getUserByNick(event.getChannel(), messageParts[1]));
			 } else 
                             if (messageParts[0].equalsIgnoreCase((GeneralRegistry.CommandChar + "mode"))){
	            	event.getChannel().send().setMode(messageParts[1]);
			 } else if (messageParts[0].equalsIgnoreCase((GeneralRegistry.CommandChar + "somethingawesome"))){
                            if(event.getChannel().isOp(IRCUtils.getUserByNick(event.getChannel(), event.getBot().getNick())) && event.getChannel().isOwner(event.getUser()) == false && event.getChannel().isSuperOp(event.getUser()) == false){
                             event.getChannel().send().kick(event.getUser(),"http://bit.ly/1c9vo1S");
                            } else if(event.getChannel().isOwner(IRCUtils.getUserByNick(event.getChannel(), event.getBot().getNick()))){
                             event.getChannel().send().kick(event.getUser(),"http://bit.ly/1c9vo1S");
                            }else {
                                event.getChannel().send().message("http://bit.ly/1c9vo1S");

                            }
			 }
                         
                         
	} 
}
	
