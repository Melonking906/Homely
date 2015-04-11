package io.loyloy.homely.cmd;

import org.bukkit.command.CommandSender;

public abstract class Cmd
{
    private final String name;
    private final String permission;

    public Cmd( String name, String permission )
    {
        this.name = name;
        this.permission = permission;
    }

    public String getName()
    {
        return name;
    }

    public String getPermission()
    {
        return permission;
    }

    public abstract boolean onCommand(CommandSender sender, Cmd cmd, String commandLabel, String[] args);
}