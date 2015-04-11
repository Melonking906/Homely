package io.loyloy.homely.db.sql;

import io.loyloy.homely.Homely;
import io.loyloy.homely.db.Home;
import org.bukkit.entity.Player;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

public abstract class SQL
{
    private Connection connection;

    private HashMap<UUID,List<Home>> userHomesCache;
    private HashMap<UUID,String> userIdsCache;

    protected Homely plugin;

    public SQL( Homely plugin )
    {
        this.plugin = plugin;

        this.userHomesCache = new HashMap<UUID,List<Home>>();
        this.userIdsCache = new HashMap<UUID,String>();

        plugin.getServer().getScheduler().runTaskTimerAsynchronously( plugin, new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    if( connection != null && ! connection.isClosed() )
                    {
                        connection.createStatement().execute( "/* ping */ SELECT 1" );
                        updateTables();
                    }
                }
                catch( SQLException e )
                {
                    connection = getNewConnection();
                }
            }
        }, 60 * 20, 60 * 20 );
    }

    protected abstract Connection getNewConnection();

    protected abstract String getName();

    public String getConfigName()
    {
        return getName().toLowerCase().replace(" ", "");
    }

    private ArrayList<HashMap<String,String>> query( String sql, boolean hasReturn )
    {
        if( ! checkConnection() )
        {
            plugin.getLogger().info( "Error with database" );
            return null;
        }

        PreparedStatement statement;
        try
        {
            statement = connection.prepareStatement( sql );

            if( ! hasReturn )
            {
                statement.execute();
                return null;
            }

            ResultSet set = statement.executeQuery();

            ResultSetMetaData md = set.getMetaData();
            int columns = md.getColumnCount();

            ArrayList<HashMap<String,String>> list = new ArrayList<HashMap<String,String>>( 50 );

            while( set.next() )
            {
                HashMap<String,String> row = new HashMap<String,String>( columns );
                for( int i = 1; i <= columns; ++i )
                {
                    row.put( md.getColumnName( i ), set.getObject( i ).toString() );
                }
                list.add( row );
            }

            if( list.isEmpty() )
            {
                return null;
            }

            return list;
        }
        catch( SQLException e )
        {
            e.printStackTrace();
        }

        return null;
    }

    public boolean checkConnection()
    {
        try
        {
            if( connection == null || connection.isClosed() )
            {
                connection = getNewConnection();

                if( connection == null || connection.isClosed() )
                {
                    return false;
                }
            }
        }
        catch( SQLException e )
        {
            e.printStackTrace();

            return false;
        }

        return true;
    }

    private void updateTables()
    {
        int version;

        query( "CREATE TABLE IF NOT EXISTS homely_homes (home_id int(11) unsigned NOT NULL AUTO_INCREMENT,user_id int(11) NOT NULL,name varchar(64) DEFAULT NULL,x int(11) DEFAULT NULL,y int(11) DEFAULT NULL,z int(11) DEFAULT NULL,world varchar(64) DEFAULT NULL,is_open tinyint(1) DEFAULT NULL,date_created datetime DEFAULT NULL,last_accessed datetime DEFAULT NULL,PRIMARY KEY (home_id)) DEFAULT CHARSET=utf8;", false );
        query( "CREATE TABLE IF NOT EXISTS homely_users (user_id int(11) unsigned NOT NULL AUTO_INCREMENT,uuid varchar(36) DEFAULT NULL,name varchar(64) DEFAULT NULL,last_online datetime DEFAULT NULL,PRIMARY KEY (user_id)) DEFAULT CHARSET=utf8;", false );
        query( "CREATE TABLE IF NOT EXISTS homely_version (version int(11) unsigned NOT NULL,PRIMARY KEY (version));", false );

        ArrayList<HashMap<String,String>> results;
        results = query( "SELECT version FROM homely_version", true );
        if( results == null )
        {
            query( "INSERT INTO homely_version (version) VALUES (1);", false );
            version = 1;
        }
        else
        {
            version = Integer.parseInt( results.get( 0 ).get( "version" ) );
        }
    }

    public void disconnect()
    {
        try
        {
            if (connection != null)
            {
                connection.close();
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    public List<Home> downloadUsersHomes( UUID uuid )
    {
        List<Home> homes = new ArrayList<Home>();

        if( userHomesCache.containsKey( uuid ) )
        {
            return userHomesCache.get( uuid );
        }

        ArrayList<HashMap<String,String>> data = query( "SELECT * FROM homely_homes AS h JOIN homely_users AS u ON h.user_id = u.user_id WHERE u.uuid = '" + uuid + "';", true );
        if( data == null )
        {
            return null;
        }

        for( HashMap<String,String> row : data )
        {
            if( row == null )
            {
                continue;
            }

            Home home = Home.parseHome( row );
            if( home == null )
            {
                continue;
            }

            homes.add( home );
        }

        userHomesCache.put( uuid, homes );

        return homes;
    }

    public void uploadHome( Home home )
    {
        userHomesCache.remove( home.getOwner().getUniqueId() );

        SimpleDateFormat format = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
        Date date = new Date();
        String userId = getUserId( home.getOwner().getUniqueId() );

        if( query( "SELECT * FROM homely_homes AS h JOIN homely_users AS u ON h.user_id = u.user_id WHERE u.uuid = '"+home.getOwner().getUniqueId().toString()+"' AND h.name = '"+home.getName()+"'", true ) == null )
        {
            query( "INSERT INTO homely_homes (user_id, name, x, y, z, world, is_open, date_created, last_accessed) VALUES ("+userId+", '"+home.getName()+"', "+home.getLocation().getBlockX()+", "+home.getLocation().getBlockY()+", "+home.getLocation().getBlockZ()+", "+home.getLocation().getWorld().getName()+", "+String.valueOf(home.isOpen())+", "+format.format( date )+", "+format.format( date )+");", false );
        }
        else
        {
            query( "UPDATE homely_homes SET x = "+home.getLocation().getBlockX()+", y = "+home.getLocation().getBlockY()+", z = "+home.getLocation().getBlockZ()+", world = '"+home.getLocation().getWorld().getName()+"', is_open = '"+String.valueOf(home.isOpen())+"', last_accessed = '"+format.format(date)+"' WHERE user_id = '"+userId+"' AND name = '"+home.getName()+"';", false );
        }

        downloadUsersHomes( home.getOwner().getUniqueId() );
    }

    public void deleteHome( Home home )
    {
        userHomesCache.remove( home.getOwner().getUniqueId() );

        String userId = getUserId( home.getOwner().getUniqueId() );
        query( "DELETE FROM homely_homes WHERE user_id = "+userId+" AND name = '"+home.getName()+"';", false );
    }

    public void uploadUser( Player user )
    {
        SimpleDateFormat format = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
        Date date = new Date();

        if( getUserId( user.getUniqueId() ) == null )
        {
            query( "INSERT INTO homely_users (uuid,name,last_online) VALUES ('"+user.getUniqueId().toString()+"', '"+user.getName()+"', '"+format.format( date )+"');", false );
        }
        else
        {
            query( "UPDATE homely_users SET name = '"+user.getName()+"', last_online = '"+format.format( date )+"' WHERE uuid = '"+user.getUniqueId().toString()+"';", false );
        }
    }

    private String getUserId( UUID uuid )
    {
        if( userIdsCache.containsKey( uuid ) )
        {
            return userIdsCache.get( uuid );
        }

        ArrayList<HashMap<String,String>> data = query( "SELECT user_id FROM homely_users WHERE uuid = '"+uuid.toString()+"'", true );
        if( data == null )
        {
            return null;
        }

        String user_id = data.get( 0 ).get( "user_id" );

        userIdsCache.put( uuid, user_id );

        return user_id;
    }
}
