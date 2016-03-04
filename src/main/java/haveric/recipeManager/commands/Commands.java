package haveric.recipeManager.commands;


import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandManager;

import haveric.recipeManager.RecipeManager;

public class Commands {

    private RecipeManager plugin;

    public Commands(RecipeManager recipeManager) {
        plugin = recipeManager;

        CommandManager service = Sponge.getCommandManager();

        service.register(plugin, new BaseCommand(), "rm", "recipemanager", "rmhelp");

        service.register(plugin, new BooksCommand(), "rmbooks", "recipebooks");
        service.register(plugin, new CheckCommand(), "rmcheck", "checkrecipes");
        service.register(plugin, new ExtractCommand(), "rmextract", "rmimport", "importrecipes", "extractrecipes");
        service.register(plugin, new CreateRecipeCommand(), "rmcreaterecipe", "createrecipe");
        service.register(plugin, new FindItemCommand(), "rmfinditem", "finditem");
        service.register(plugin, new GetBookCommand(), "rmgetbook", "getrecipebook");
        service.register(plugin, new RecipeCommand(), "rmrecipes", "recipe", "recipes");
        service.register(plugin, new ReloadBooksCommand(), "rmreloadbooks", "reloadbooks");
        service.register(plugin, new ReloadCommand(), "rmreload");
        service.register(plugin, new UpdateCommand(), "rmupdate", "rmcheckupdates");
    }

}
