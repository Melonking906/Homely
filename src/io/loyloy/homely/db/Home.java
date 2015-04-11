package io.loyloy.homely.db;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

public class Home
{
    private OfflinePlayer owner;
    private Location location;
    private String name;
    private boolean open;
    private Date dateCreated;
    private Date lastAccessed;

    public Home( OfflinePlayer owner, Location location, String name, boolean open, Date dateCreated, Date lastAccessed )
    {
        this.owner = owner;
        this.location = location;
        this.name = name;
        this.open = open;
        this.dateCreated = dateCreated;
        this.lastAccessed = lastAccessed;
    }

    public OfflinePlayer getOwner()
    {
        return owner;
    }

    public Location getLocation()
    {
        return location;
    }

    public void setLocation( Location location )
    {
        this.location = location;
    }

    public String getName()
    {
        return name;
    }

    public boolean isOpen()
    {
        return open;
    }

    public void setOpen( boolean open )
    {
        this.open = open;
    }

    public Date getDateCreated()
    {
        return dateCreated;
    }

    public Date getLastAccessed()
    {
        return lastAccessed;
    }

    public void updateLastAccessed()
    {
        this.lastAccessed = new Date();
    }

    public static Home parseHome( HashMap<String,String> row )
    {
        OfflinePlayer owner;
        Location location;
        String name;
        boolean open;
        Date dateCreated;
        Date lastAccessed;

        SimpleDateFormat format = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );

        try
        {
            owner = Bukkit.getOfflinePlayer( UUID.fromString( row.get( "uuid" ) ) );
            location = new Location( Bukkit.getWorld( row.get( "world" ) ), Double.parseDouble( row.get( "x" ) ), Double.parseDouble( row.get( "y" ) ), Double.parseDouble( row.get( "z" ) ) );
            name = row.get( "name" );
            open = Integer.parseInt( row.get( "is_open" ) ) == 1;
            dateCreated = format.parse( row.get( "date_created" ) );
            lastAccessed = format.parse( row.get( "last_accessed" ) );
        }
        catch( Exception e )
        {
            return null;
        }

        return new Home( owner, location, name, open, dateCreated, lastAccessed );
    }
}
