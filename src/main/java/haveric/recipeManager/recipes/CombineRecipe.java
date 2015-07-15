package haveric.recipeManager.recipes;

import haveric.recipeManager.flags.Flags;

import java.util.List;

import org.spongepowered.api.item.inventory.ItemStack;

public class CombineRecipe extends BaseRecipe{
    private List<ItemStack> ingredients;

    public CombineRecipe(BaseRecipe newRecipe) {
        super(newRecipe);
    }

    public CombineRecipe(Flags flags) {
        super(flags);
    }
}
