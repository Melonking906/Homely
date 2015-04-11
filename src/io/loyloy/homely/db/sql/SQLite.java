package io.loyloy.homely.db.sql;

import io.loyloy.homely.Homely;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;

public class SQLite extends SQL
{
    private final Homely plugin;

    public SQLite( Homely plugin )
    {
        super(plugin);

        this.plugin = plugin;
    }

    protected Connection getNewConnection()
    {
        try
        {
            Class.forName("org.sqlite.JDBC");

            return DriverManager.getConnection( "jdbc:sqlite:" + new File( plugin.getDataFolder(), "homely.db" ).getAbsolutePath() );
        }
        catch (Exception e)
        {
            return null;
        }
    }

    public String getName()
    {
        return "SQLite";
    }
}
