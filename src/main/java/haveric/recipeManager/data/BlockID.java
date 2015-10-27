package haveric.recipeManager.data;

import java.util.Optional;
import java.util.UUID;

import org.apache.commons.lang3.Validate;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import haveric.recipeManager.RecipeManager;

public class BlockID {
    private transient int hash;

    private UUID wid;
    private int x;
    private int y;
    private int z;

    public BlockID(Location location) {
        parseLocation(location);
    }

    public BlockID(World world, int newX, int newY, int newZ) {
        wid = world.getUniqueId();
        x = newX;
        y = newY;
        z = newZ;

        buildHash();
    }

    /**
     * @param id
     * @param coords
     * @throws IllegalArgumentException
     *             if coordinate string isn't valid or id is null
     */
    public BlockID(UUID id, String coords) {
        Validate.notNull(id, "id argument must not be null!");
        Validate.notNull(coords, "coords argument must not be null!");

        wid = id;

        try {
            String[] s = coords.split(",", 3);

            x = Integer.parseInt(s[0]);
            y = Integer.parseInt(s[1]);
            z = Integer.parseInt(s[2]);
        } catch (Throwable e) {
            throw new IllegalArgumentException("Coords argument must have 3 numbers separated by commas!");
        }

        buildHash();
    }

    private void parseLocation(Location location) {
        Validate.notNull(location, "location argument must not be null!");

        wid = location.getExtent().getUniqueId();
        x = location.getBlockX();
        y = location.getBlockY();
        z = location.getBlockZ();

        buildHash();
    }

    private void buildHash() {
        hash = (wid.toString() + ":" + x + ":" + y + ":" + z + ":").hashCode();
    }

    public static BlockID fromString(UUID id, String coords) {
        return new BlockID(id, coords);
    }

    public static BlockID fromLocation(Location location) {
        return new BlockID(location);
    }

    /**
     * Gets the block at the stored coordinates
     *
     * @return
     */
    public Optional<BlockState> toBlock() {
        Optional<World> opWorld = getWorld();
        World world = null;

        if (opWorld.isPresent()) {
            world = opWorld.get();
        }

        return Optional.of(world.getBlock(x, y, z));
    }

    public UUID getWorldID() {
        return wid;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    /**
     * @return coordinates in x,y,z format string
     */
    public String getCoordsString() {
        return x + "," + y + "," + z;
    }

    /**
     * Get world by the world ID stored
     *
     * @return world or null if world isn't loaded
     */
    public Optional<World> getWorld() {
        return RecipeManager.getPlugin().getGame().getServer().getWorld(wid);
    }

    @Override
    public int hashCode() {
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        if (!(obj instanceof BlockID)) {
            return false;
        }

        BlockID b = (BlockID) obj;

        return (b.x == x && b.y == y && b.z == z && b.wid.equals(wid));
    }
}
