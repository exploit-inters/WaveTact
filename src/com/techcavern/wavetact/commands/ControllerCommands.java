/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.techcavern.wavetact.commands;

import com.techcavern.wavetact.Main;
import com.techcavern.wavetact.utils.Command;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.MessageEvent;

/**
 *
 * @author jztech101
 */
public class ControllerCommands extends ListenerAdapter<PircBotX> {
		public class join extends Command{
    public join(){
        super("join", 10);
    }
    @Override
    public void onCommand(MessageEvent<?> event, String... args) throws Exception{
		event.getBot().sendIRC().joinChannel(args[1]);
				
    }
}
}