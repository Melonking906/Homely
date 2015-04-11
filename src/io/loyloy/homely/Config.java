package io.loyloy.homely;

import org.bukkit.configuration.file.FileConfiguration;

public class Config
{
    private Homely plugin;

    private String homelyPrefix;

    private String dbType;
    private String dbHost;
    private String dbPort;
    private String dbUser;
    private String dbPass;
    private String dbName;

    public Config( Homely plugin )
    {
        this.plugin = plugin;

        setupConfig();

        reloadConfig();
    }

    public String getHomelyPrefix()
    {
        return homelyPrefix;
    }

    public String getDbType()
    {
        return dbType;
    }

    public String getDbHost()
    {
        return dbHost;
    }

    public String getDbPort()
    {
        return dbPort;
    }

    public String getDbUser()
    {
        return dbUser;
    }

    public String getDbPass()
    {
        return dbPass;
    }

    public String getDbName()
    {
        return dbName;
    }

    private void setupConfig()
    {
        plugin.saveDefaultConfig();

        FileConfiguration config = plugin.getConfig();

        // Update header.
        config.options().copyHeader();

        // Settings
        if( ! config.isSet( "homely_prefix" ) )
        {
            config.set( "homely_prefix", "[Homely]" );
        }

        // Database config
        if( ! config.isSet( "type" ) )
        {
            config.set( "type", "sqlite" );
        }
        if( ! config.isSet( "host" ) )
        {
            config.set( "host", "localhost" );
        }
        if( ! config.isSet( "port" ) )
        {
            config.set( "port", "3306" );
        }
        if( ! config.isSet( "user" ) )
        {
            config.set( "user", "root" );
        }
        if( ! config.isSet( "password" ) )
        {
            config.set( "password", "password" );
        }
        if( ! config.isSet( "database" ) )
        {
            config.set( "database", "homely" );
        }

        plugin.saveConfig();
    }

    public void reloadConfig()
    {
        plugin.reloadConfig();

        FileConfiguration config = plugin.getConfig();

        homelyPrefix = config.getString( "homely_prefix" );

        dbType = config.getString( "type" );
        dbHost = config.getString( "host" );
        dbPort = config.getString( "port" );
        dbUser = config.getString( "user" );
        dbPass = config.getString( "password" );
        dbName = config.getString( "database" );
    }
}
