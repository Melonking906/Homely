package io.loyloy.homely.cmd;

import io.loyloy.homely.Homely;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class MainCmd implements CommandExecutor
{
    private final Homely plugin;

    public MainCmd( Homely plugin )
    {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand( CommandSender commandSender, Command command, String s, String[] strings )
    {
        return false;
    }
}
