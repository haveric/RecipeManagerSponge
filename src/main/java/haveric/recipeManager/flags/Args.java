package haveric.recipeManager.flags;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.world.Location;

import haveric.recipeManager.Messages;
import haveric.recipeManager.recipes.BaseRecipe;
import haveric.recipeManager.uuidFetcher.UUIDFetcher;
import haveric.recipeManagerCommon.flags.AbstractArgs;
import haveric.recipeManagerCommon.recipes.RMCRecipeType;

/**
 * Easily modifiable arguments for the flag classes without needing to re-edit all of them
 */
public class Args extends AbstractArgs {
    private Player player;
    private Location location;
    private BaseRecipe recipe;
    private RMCRecipeType recipeType;
    private Inventory inventory;
    //private ItemResult result;

    protected Args() {
    }

    public static void init() {
    }

    public void setPlayer(Player newPlayer) {
        player = newPlayer;
    }

    public void setLocation(Location newLocation) {
        location = newLocation;
    }

    public void setRecipe(BaseRecipe newRecipe) {
        recipe = newRecipe;
    }

    public void setRecipeType(RMCRecipeType newRecipeType) {
        recipeType = newRecipeType;
    }

    public void setInventory(Inventory newInventory) {
        inventory = newInventory;
    }
/*
    public void setResult(ItemResult newResult) {
        result = newResult;
    }
*/

    /**
     * Gets the Player object from either player() or playerName()
     *
     * @return player object or null if player just doesn't exist
     */
    public Player player() {
        return player;
    }

    public boolean hasPlayer() {
        return player != null;
    }

    /**
     * Gets a location from either location, player or playerName arguments.
     *
     * @return null in case no location could be generated
     */
    public Location location() {
        return location;
    }

    public boolean hasLocation() {
        return location != null;
    }

    public RMCRecipeType recipeType() {
        return recipeType;
    }

    public boolean hasRecipeType() {
        return recipeType != null;
    }

    public BaseRecipe recipe() {
        return recipe;
    }

    public boolean hasRecipe() {
        return recipe != null;
    }

    public Inventory inventory() {
        return inventory;
    }

    public boolean hasInventory() {
        return inventory != null;
    }
/*
    public ItemResult result() {
        return result;
    }

    public boolean hasResult() {
        return result != null;
    }
 */

    public void addReason(Messages globalMessage, String customMessage, Object... variables) {
        addCustomReason(globalMessage.getCustom(customMessage, variables));
    }

    public void sendReasons(CommandSource sender, String prefix) {
        sendList(sender, prefix, reasons());
    }

    public void addEffect(Messages globalMessage, String customMessage, Object... variables) {
        addCustomEffect(globalMessage.getCustom(customMessage, variables));
    }

    public void sendEffects(CommandSource sender, String prefix) {
        sendList(sender, prefix, effects());
    }

    private void sendList(CommandSource sender, String prefix, List<String> list) {
        if (sender == null || list == null) {
            return;
        }

        for (String s : list) {
            if (s != null) {
                Messages.send(sender, prefix + s);
            }
        }
    }

    public String parseVariables(String string) {
        String name;
        if (hasPlayerName()) {
            name = playerName();
        } else {
            name = "(nobody)";
        }

        string = string.replace("{player}", name);
        //string = string.replace("{playerdisplay}", (player != null ? player.getDisplayName() : name));
        //string = string.replace("{result}", ToolsItem.print(result()));
        string = string.replace("{recipename}", (hasRecipe() ? recipe().getName() : "(unknown)"));
        string = string.replace("{recipetype}", (hasRecipeType() ? recipeType().toString().toLowerCase() : "(unknown)"));
        //string = string.replace("{inventorytype}", (hasInventory() ? inventory().getType().toString().toLowerCase() : "(unknown)"));
        //string = string.replace("{world}", (hasLocation() ? location().getWorld().getName() : "(unknown)"));
        string = string.replace("{x}", (hasLocation() ? "" + location().getBlockX() : "(?)"));
        string = string.replace("{y}", (hasLocation() ? "" + location().getBlockY() : "(?)"));
        string = string.replace("{z}", (hasLocation() ? "" + location().getBlockZ() : "(?)"));

        return string;
    }

    /**
     * Start building an argument class for flag events
     *
     * @return linkable methods
     */
    public static ArgBuilder create() {
        return new ArgBuilder();
    }

    /**
     * Re-processes the arguments to assign them in as many places as possible.<br>
     * For example, if you only set player name, the player() will still be null, but by triggering this it will try to assign player() to a Player object.
     *
     * @return same instance
     */
    public Args processArgs() {
        Player player = player();
        String playerName = playerName();

        if (player == null && playerName != null) {
            UUID uuid;
            try {
                uuid = UUIDFetcher.getUUIDOf(playerName);

                Optional<Player> opPlayer = Sponge.getServer().getPlayer(uuid);
                if (opPlayer.isPresent()) {
                    setPlayer(opPlayer.get());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (playerName == null && player != null) {
            setPlayerName(player().getName());
        }

        if (location() == null && player != null) {
            setLocation(player().getLocation());
        }

        if (recipeType() == null && recipe() != null) {
            setRecipeType(recipe().getType());
        }

        return this;
    }
}
