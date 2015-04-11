package io.loyloy.homely.db.sql;

import io.loyloy.homely.Config;
import io.loyloy.homely.Homely;

import java.sql.Connection;
import java.sql.DriverManager;

public class MySQL extends SQL
{
    public MySQL( Homely plugin )
    {
        super(plugin);
    }

    protected Connection getNewConnection()
    {
        Config config = plugin.getHomelyConfig();

        try
        {
            Class.forName("com.mysql.jdbc.Driver");

            String url = "jdbc:mysql://" + config.getDbHost() + ":" + config.getDbPort() + "/" + config.getDbName();

            return DriverManager.getConnection( url, config.getDbUser(), config.getDbPass() );
        }
        catch (Exception e)
        {
            return null;
        }
    }

    public String getName()
    {
        return "MySQL";
    }
}