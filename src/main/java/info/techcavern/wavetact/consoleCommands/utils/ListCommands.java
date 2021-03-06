package info.techcavern.wavetact.consoleCommands.utils;

import info.techcavern.wavetact.annot.ConCMD;
import info.techcavern.wavetact.objects.CommandIO;
import info.techcavern.wavetact.objects.ConsoleCommand;
import info.techcavern.wavetact.utils.GeneralUtils;
import info.techcavern.wavetact.utils.Registry;
import info.techcavern.wavetact.annot.ConCMD;
import info.techcavern.wavetact.objects.CommandIO;
import info.techcavern.wavetact.objects.ConsoleCommand;
import info.techcavern.wavetact.utils.GeneralUtils;
import info.techcavern.wavetact.utils.Registry;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@ConCMD
public class ListCommands extends ConsoleCommand {

    public ListCommands() {
        super(GeneralUtils.toArray("listcommands commands list cmds"), "list", "lists all ircCommands");
    }

    @Override
    public void onCommand(String command, String[] args, CommandIO commandIO) throws Exception {
        Set<String> commands = Registry.consoleCommandList.stream().map(ConsoleCommand::getCommand).collect(Collectors.toSet());
    //    Collections.sort(commands);
        commandIO.getPrintStream().println(StringUtils.join(commands, ", "));
    }

}
