package haveric.recipeManager.recipes;

import haveric.recipeManager.flags.Flags;

import org.spongepowered.api.item.inventory.ItemStack;

public class BrewRecipe extends BaseRecipe {
    private ItemStack ingredient;
    private ItemStack potion;

    public BrewRecipe(BaseRecipe newRecipe) {
        super(newRecipe);
    }

    public BrewRecipe(Flags flags) {
        super(flags);
    }

}
