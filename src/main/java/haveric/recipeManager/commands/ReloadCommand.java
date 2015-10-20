package haveric.recipeManager.commands;

import java.util.List;
import java.util.Optional;

import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.util.command.CommandCallable;
import org.spongepowered.api.util.command.CommandException;
import org.spongepowered.api.util.command.CommandResult;
import org.spongepowered.api.util.command.CommandSource;

import haveric.recipeManager.RecipeManager;


public class ReloadCommand implements CommandCallable {

    @Override
    public CommandResult process(CommandSource source, String arguments) throws CommandException {
        RecipeManager.getPlugin().reload(source);
        return CommandResult.success();
    }

    @Override
    public List<String> getSuggestions(CommandSource source, String arguments) throws CommandException {
        return null;
    }

    @Override
    public boolean testPermission(CommandSource source) {
        return true;
    }

    @Override
    public Optional<Text> getShortDescription(CommandSource source) {
        return Optional.of((Text) Texts.of("reload recipes/settings/books/etc."));
    }

    @Override
    public Optional<Text> getHelp(CommandSource source) {
        return Optional.of((Text) Texts.of(""));
    }

    @Override
    public Text getUsage(CommandSource source) {
        return Texts.of("/<command>");
    }

}
