package haveric.recipeManager.recipes;

import org.spongepowered.api.item.recipe.Recipe;

import haveric.recipeManager.ErrorReporter;
import haveric.recipeManager.flags.Args;
import haveric.recipeManager.flags.Flag;
import haveric.recipeManager.flags.FlagType;
import haveric.recipeManager.flags.Flaggable;
import haveric.recipeManager.flags.Flags;
import haveric.recipeManagerCommon.recipes.RMCRecipeType;

public class BaseRecipe implements Flaggable {
    protected String name;
    protected boolean customName;
    private Flags flags;
    protected int hash;
    protected Recipe recipe;

    public BaseRecipe(BaseRecipe newRecipe) {
        if (newRecipe.hasFlags()) {
            flags = newRecipe.getFlags().clone(this);
        } else {
            flags = null;
        }
        name = newRecipe.name;
        customName = newRecipe.customName;
        hash = newRecipe.hash;
        recipe = newRecipe.recipe;
    }

    public BaseRecipe(Flags newFlags) {
        flags = newFlags.clone(this);
    }
    /*
    public RecipeInfo getInfo() {
        return RecipeManager.getRecipes().getRecipeInfo(this);
    }
    */

    public RMCRecipeType getType() {
        return null;
    }

    public String getName() {
        if (name == null) {
            resetName();
        }

        return name;
    }

    public boolean hasCustomName() {
        return customName;
    }

    public void setName(String newName) {
        newName = newName.trim();

        if (newName.isEmpty()) {
            ErrorReporter.error("Recipe names can not be empty!");
            return;
        }

        while (newName.charAt(0) == '+') {
            ErrorReporter.error("Recipe names can not start with '+' character, removed!");
            newName = newName.substring(1);
        }

        name = newName;
        customName = true;
    }

    public void resetName() {
        name = "unknown recipe";
        customName = false;
    }

    public boolean isValid() {
        return false;
    }

    public int getIndex() {
        return hash;
    }

    @Override
    public String toString() {
        return getType() + "{" + getName() + "}";
    }

    @Override
    public int hashCode() {
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (!(obj instanceof BaseRecipe)) {
            return false;
        }

        return obj.hashCode() == hashCode();
    }
    /*
    public void register() {
        RecipeManager.getRecipes().registerRecipe(this);
    }
    */
    /*
    public Recipe remove() {
        return RecipeManager.getRecipes().removeRecipe(this);
    }
    */

    @Override
    public boolean hasFlag(FlagType type) {
        boolean  hasFlag = false;

        if (flags != null) {
            hasFlag = flags.hasFlag(type);
        }

        return hasFlag;
    }

    @Override
    public boolean hasFlags() {
        return flags != null;
    }

    @Override
    public Flag getFlag(FlagType type) {
        Flag flag = null;

        if (flags != null) {
            flag = flags.getFlag(type);
        }

        return flag;
    }

    @Override
    public <T extends Flag> T getFlag(Class<T> flagClass) {
        T t = null;

        if (flags != null) {
            t = flags.getFlag(flagClass);
        }

        return t;
    }

    @Override
    public Flags getFlags() {
        if (flags == null) {
            flags = new Flags(this);
        }

        return flags;
    }

    public void clearFlags() {
        flags = null;
    }

    @Override
    public void addFlag(Flag flag) {
        getFlags().addFlag(flag);
    }

    @Override
    public boolean hasNoShiftBit() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean checkFlags(Args a) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean sendCrafted(Args a) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean sendPrepare(Args a) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean sendFuelRandom(Args a) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean sendFuelEnd(Args a) {
        // TODO Auto-generated method stub
        return false;
    }
}
