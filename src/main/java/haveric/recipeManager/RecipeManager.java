package haveric.recipeManager;

import java.io.File;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.event.EventManager;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameAboutToStartServerEvent;
import org.spongepowered.api.event.game.state.GameStartingServerEvent;
import org.spongepowered.api.event.game.state.GameStoppingServerEvent;
import org.spongepowered.api.item.recipe.Recipe;
import org.spongepowered.api.item.recipe.RecipeRegistry;
import org.spongepowered.api.item.recipe.ShapedRecipe;
import org.spongepowered.api.item.recipe.ShapelessRecipe;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;

import com.google.inject.Inject;

import haveric.recipeManager.commands.Commands;
import haveric.recipeManager.events.RMPlayerJoinQuitEvent;

@Plugin(id = "RecipeManager", name = "Recipe Manager", version = "3.0")
public class RecipeManager {

    @Inject
    @DefaultConfig(sharedRoot = false)
    private File defaultConfig;

    @Inject
    private Logger log;

    private Settings settings;
    private Files files;
    private RecipeProcessor recipeProcessor;

    private PluginContainer pluginContainer;

    private Commands commands;
    private static RecipeManager plugin;

    @Listener
    public void preStartup(GameAboutToStartServerEvent event) {
        plugin = this;

        Optional<PluginContainer> optionalPluginContainer = Sponge.getGame().getPluginManager().fromInstance(this);
        if (optionalPluginContainer.isPresent()) {
            pluginContainer = optionalPluginContainer.get();
        }
    }

    @Listener
    public void onStartup(GameStartingServerEvent event) {
        EventManager em = Sponge.getEventManager();
        commands = new Commands(this);

        settings = new Settings(this, defaultConfig);
        files = new Files(this);

        recipeProcessor = new RecipeProcessor(this);

        reload(null);

        em.registerListeners(this, new RMPlayerJoinQuitEvent(this));

        // Attempt to read vanilla recipes
        Messages.send(null, "Read recipes");
        try {
            Messages.send(null, "Recipe Registry: " + Sponge.getRegistry().getRecipeRegistry());
            RecipeRegistry recipeRegistry = Sponge.getRegistry().getRecipeRegistry();
            Set<Recipe> recipes = recipeRegistry.getRecipes();
            Messages.send(null, "Num recipes: " + recipes.size());
            Iterator<Recipe> iter = recipes.iterator();
            while(iter.hasNext()) {
                Recipe recipe = iter.next();
                if (recipe instanceof ShapedRecipe) {
                    ShapedRecipe shaped = (ShapedRecipe) recipe;
                    Messages.send(null, "Shaped " + shaped.getWidth() + "x" + shaped.getHeight() + " " + shaped.getResultTypes());
                } else if (recipe instanceof ShapelessRecipe) {
                    ShapelessRecipe shapeless = (ShapelessRecipe) recipe;
                    Messages.send(null, "Shapeless " + shapeless.getResultTypes());
                } else {
                    Messages.send(null, "Unknown " + recipe.getResultTypes());
                }
            }
        } catch (Exception e) {
            Messages.sendAndLog(null, "Error loading recipes");
            //e.printStackTrace();
        }
    }



    @Listener
    public void onShutdown(GameStoppingServerEvent event) {

    }

    public static RecipeManager getPlugin() {
        return plugin;
    }

    public Logger getLog() {
        return log;
    }

    public PluginContainer getPluginContainer() {
        if (pluginContainer == null) {
            Optional<PluginContainer> optionalPluginContainer = Sponge.getPluginManager().fromInstance(plugin);
            if (optionalPluginContainer.isPresent()) {
                pluginContainer = optionalPluginContainer.get();
            }
        }

        return pluginContainer;
    }

    public Settings getSettings() {
        return settings;
    }

    public String getVersion() {
        return pluginContainer.getVersion();
    }

    public void reload(CommandSource source) {
        if (source == null) {
            source = Sponge.getServer().getConsole();
        }
        files.reload(source);

        //recipeProcessor.reload(source);
    }
}
