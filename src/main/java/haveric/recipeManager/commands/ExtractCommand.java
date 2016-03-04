package haveric.recipeManager.commands;

import java.util.List;
import java.util.Optional;

import org.spongepowered.api.command.CommandCallable;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;



public class ExtractCommand implements CommandCallable{

    @Override
    public CommandResult process(CommandSource source, String arguments) throws CommandException {
        //source.sendMessage(Texts.of(TextColors.YELLOW, "------ ", TextColors.WHITE, "Recipe Manager", TextColors.GRAY, " by haveric ", TextColors.YELLOW, "------"));
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
        return Optional.of((Text) Text.of("makes all recipes from other plugins or mods into a text file to allow overriding and editing"));
    }

    @Override
    public Optional<Text> getHelp(CommandSource source) {
        return Optional.of((Text) Text.of(""));
    }

    @Override
    public Text getUsage(CommandSource source) {
        return Text.of("/<command> [special]");
    }

}
