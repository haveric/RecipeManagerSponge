package haveric.recipeManager.events;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.network.ClientConnectionEvent;

import haveric.recipeManager.RecipeManager;
import haveric.recipeManager.uuidFetcher.UUIDFetcher;

public class RMPlayerJoinQuitEvent {

    private RecipeManager plugin;

    public RMPlayerJoinQuitEvent(RecipeManager recipeManager) {
        plugin = recipeManager;
    }

    @Listener
    public void playerJoin(ClientConnectionEvent.Join event) {
        Player player = event.getTargetEntity();
        UUIDFetcher.addPlayerToCache(player.getName(), player.getUniqueId());
    }

    @Listener
    public void playerQuit(ClientConnectionEvent.Disconnect event) {
        Player player = event.getTargetEntity();
        UUIDFetcher.removePlayerFromCache(player.getName());
    }
}
