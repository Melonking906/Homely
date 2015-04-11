package io.loyloy.homely;

import io.loyloy.homely.db.Home;
import io.loyloy.homely.db.sql.SQL;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class API
{
    private SQL db;

    public API( Homely plugin )
    {
        this.db = plugin.getDB();
    }

    public Home getHome( UUID playerUUID, String homeName )
    {
        List<Home> homes = getUsersHomes( playerUUID );

        for( Home home : homes )
        {
            if( home.getName().equals( homeName ) )
            {
                return home;
            }
        }

        return null;
    }

    public List<Home> getUsersHomes( UUID playerUUID )
    {
        return db.downloadUsersHomes( playerUUID );
    }

    public void setHome( Home home )
    {
        db.uploadHome( home );
    }

    public void removeHome( Home home )
    {
        db.deleteHome( home );
    }

    public void updateUser( Player user )
    {
        db.uploadUser( user );
    }
}
