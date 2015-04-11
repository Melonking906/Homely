package io.loyloy.homely;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerListener implements Listener
{
    private API api;

    public PlayerListener( Homely plugin )
    {
        this.api = plugin.getApi();
    }

    @EventHandler
    public void onJoin( PlayerJoinEvent event )
    {
        if( event == null )
        {
            return;
        }

        api.updateUser( event.getPlayer() );
    }
}
