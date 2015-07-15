package haveric.recipeManager.recipes;

import haveric.recipeManager.flags.Flags;

import org.spongepowered.api.item.inventory.ItemStack;

public class CraftRecipe extends BaseRecipe {
    private ItemStack[] ingredients;
    private int width;
    private int height;
    private boolean mirror = false;

    public CraftRecipe(BaseRecipe newRecipe) {
        super(newRecipe);
    }

    public CraftRecipe(Flags flags) {
        super(flags);
    }
}
