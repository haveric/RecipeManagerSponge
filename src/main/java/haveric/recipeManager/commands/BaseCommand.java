package haveric.recipeManager.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandCallable;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandManager;
import org.spongepowered.api.command.CommandMapping;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import haveric.recipeManager.Messages;
import haveric.recipeManager.RecipeManager;

public class BaseCommand implements CommandCallable{

    @Override
    public CommandResult process(CommandSource source, String arguments) throws CommandException {
        Messages.send(source, Text.of(TextColors.YELLOW, "------ ", TextColors.WHITE, "Recipe Manager", TextColors.GRAY, " by haveric ", TextColors.YELLOW, "------"));

        CommandManager service = Sponge.getCommandManager();
        PluginContainer pluginContainer = RecipeManager.getPlugin().getPluginContainer();

        Set<CommandMapping> commands = service.getOwnedBy(pluginContainer);
        ArrayList<CommandMapping> commandsList = new ArrayList<CommandMapping>();
        commandsList.addAll(commands);
        Collections.sort(commandsList, new CommandSorter());

        for (CommandMapping command : commandsList) {
            CommandCallable callable = command.getCallable();

            Text usage = callable.getUsage(source);
            String primaryUsage = usage.toPlain().replace("<command>", command.getPrimaryAlias());

            Text description = callable.getShortDescription(source).get();

            if (callable.testPermission(source)) {
                Messages.send(source, Text.of(TextColors.GOLD, primaryUsage, TextColors.RESET, " ", description));
            }
        }

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
        return Optional.of((Text) Text.of("plugin info and available commands"));
    }

    @Override
    public Optional<Text> getHelp(CommandSource source) {
        return Optional.of((Text) Text.of(""));
    }

    @Override
    public Text getUsage(CommandSource source) {
        return Text.of("/<command>");
    }

}
