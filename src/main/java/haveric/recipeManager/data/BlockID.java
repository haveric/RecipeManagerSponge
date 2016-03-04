package haveric.recipeManager.data;

import java.util.Optional;
import java.util.UUID;

import org.apache.commons.lang3.Validate;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import haveric.recipeManagerCommon.data.AbstractBlockID;

public class BlockID extends AbstractBlockID {

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

    public BlockID(UUID id, String coords) {
        super(id, coords);
    }

    private void parseLocation(Location location) {
        Validate.notNull(location, "location argument must not be null!");

        wid = location.getExtent().getUniqueId();
        x = location.getBlockX();
        y = location.getBlockY();
        z = location.getBlockZ();

        buildHash();
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


    /**
     * Get world by the world ID stored
     *
     * @return world or null if world isn't loaded
     */
    public Optional<World> getWorld() {
        return Sponge.getServer().getWorld(wid);
    }
}
