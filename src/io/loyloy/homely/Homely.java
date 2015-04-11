package io.loyloy.homely;

import io.loyloy.homely.cmd.MainCmd;
import io.loyloy.homely.db.sql.MySQL;
import io.loyloy.homely.db.sql.SQL;
import io.loyloy.homely.db.sql.SQLite;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.mcstats.Metrics;

import java.util.HashSet;

public class Homely extends JavaPlugin
{
    private final HashSet<SQL> dbs;
    private SQL DB;

    private Config config;
    private API api;

    public Homely()
    {
        this.dbs = new HashSet<SQL>();
    }

    @Override
    public void onEnable()
    {
        config = new Config( this );

        dbs.add( new MySQL( this ) );
        dbs.add( new SQLite( this ) );
        setupDatabase();

        MainCmd cmd = new MainCmd(this);
        getCommand( "home" ).setExecutor( cmd );
        getCommand( "sethome" ).setExecutor( cmd );
        getCommand( "delhome" ).setExecutor( cmd );
        getCommand( "homely" ).setExecutor( cmd );

        PluginManager pm = getServer().getPluginManager();

        pm.registerEvents( new PlayerListener( this ), this );

        if( ! DB.checkConnection() )
        {
            log( "Error with DATABASE" );
            pm.disablePlugin( this );
        }

        api = new API( this );

        loadMetrics();
    }

    @Override
    public void onDisable()
    {
        DB.disconnect();
    }

    public SQL getDB()
    {
        return DB;
    }

    public Config getHomelyConfig()
    {
        return config;
    }

    public API getApi()
    {
        return api;
    }

    private void setupDatabase()
    {
        String type = config.getDbType();

        DB = null;

        for ( SQL db : dbs )
        {
            if ( type.equalsIgnoreCase( db.getConfigName() ) )
            {
                DB = db;
                log( "Database set to " + db.getConfigName() + "." );
                break;
            }
        }

        if ( DB == null)
        {
            log( "Database type does not exist!" );
        }
    }

    private void loadMetrics()
    {
        try
        {
            Metrics metrics = new Metrics(this);

            Metrics.Graph graphDatabaseType = metrics.createGraph( "Database Type" );

            graphDatabaseType.addPlotter( new Metrics.Plotter( DB.getConfigName() )
            {
                @Override
                public int getValue()
                {
                    return 1;
                }
            } );

            metrics.start();
        }
        catch (Exception e) {}
    }

    public static void log( String message )
    {
        Bukkit.getLogger().info( message );
    }
}
