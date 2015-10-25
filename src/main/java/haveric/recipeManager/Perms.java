package haveric.recipeManager;

import org.spongepowered.api.entity.living.player.Player;

public class Perms {
    public static final String FLAG_PREFIX = "recipemanager.flag.";
    public static final String FLAG_ALL = FLAG_PREFIX + "*";

    public static boolean hasFlagAll(Player player) {
        return player.hasPermission(FLAG_ALL);
    }

    public static boolean hasFlagPrefix(Player player, String name) {
        return player.hasPermission(FLAG_PREFIX + name);
    }
}
