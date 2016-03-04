package haveric.recipeManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.lang3.StringEscapeUtils;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandCallable;
import org.spongepowered.api.command.CommandManager;
import org.spongepowered.api.command.CommandMapping;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.service.permission.PermissionDescription;
import org.spongepowered.api.service.permission.PermissionService;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.text.Text;

import com.google.common.collect.Sets;

import haveric.recipeManager.commands.CommandSorter;
import haveric.recipeManager.flags.FlagType;
import haveric.recipeManager.flags.FlagType.Bit;
import haveric.recipeManager.tools.Tools;
import haveric.recipeManagerCommon.RMCChatColor;
import haveric.recipeManagerCommon.util.RMCUtil;

public class Files {
    public static final String NL = System.getProperty("line.separator");

    public static final String LASTCHANGED_CONFIG = "2.5";
    public static final String LASTCHANGED_MESSAGES = "2.4";
    public static final String LASTCHANGED_ITEM_ALIASES = "2.4";
    public static final String LASTCHANGED_ENCHANT_ALIASES = "2.3";

    public static final String FILE_CONFIG = "config.conf";
    public static final String FILE_MESSAGES = "messages.conf";

    public static final String FILE_ITEM_ALIASES = "item aliases.conf";
    public static final String FILE_ENCHANT_ALIASES = "enchant aliases.conf";

    public static final String FILE_USED_VERSION = "used.version";
    public static final String FILE_CHANGELOG = "changelog.txt";

    public static final String FILE_INFO_BASICS = "basic recipes.html";
    public static final String FILE_INFO_ADVANCED = "advanced recipes.html";
    public static final String FILE_INFO_COMMANDS = "commands & permissions.html";
    public static final String FILE_INFO_NAMES = "name index.html";
    public static final String FILE_INFO_FLAGS = "recipe flags.html";
    public static final String FILE_INFO_BOOKS = "recipe books.html";

    public static final Set<String> FILE_RECIPE_EXTENSIONS = Sets.newHashSet(".txt", ".rm");

    private CommandSource sender;
    private static String DIR_PLUGIN;
    private static final String SPONGE_DOCS = "";

    private RecipeManager plugin;

    public Files(RecipeManager recipeManager) {
        plugin = recipeManager;
        DIR_PLUGIN = plugin.getSettings().getDefaultFolderPath() + File.separator;
    }

    public void reload(CommandSource sender) {
        this.sender = sender;
        createDirectories();

        boolean overwrite = isNewVersion();

        createRecipeFlags(overwrite);
        createCommands(overwrite);
        createNameIndex(overwrite);
        createFile(FILE_INFO_BASICS, overwrite);
        createFile(FILE_INFO_ADVANCED, overwrite);
        createFile(FILE_INFO_BOOKS, overwrite);
        createFile(FILE_CHANGELOG, overwrite);

        if (overwrite) {
            Messages.sendAndLog(null, "<gray>New version installed, information files and changelog have been overwritten.");
        }
    }

    private void createDirectories() {
        File file = new File(DIR_PLUGIN + "recipes" + File.separator + "disabled");
        file.mkdirs();

        file = new File(file.getPath() + File.separator + "Recipe files in here are ignored!");

        if (!file.exists()) {
            Tools.saveTextToFile("In the disabled folder you can place recipe files you don't want to load, instead of deleting them.", file);
        }
    }

    private boolean isNewVersion() {
        boolean newVersion = true;

        File file = new File(DIR_PLUGIN + FILE_USED_VERSION);
        String currentVersion = plugin.getVersion();

        try {
            if (file.exists()) {
                BufferedReader b = new BufferedReader(new FileReader(file));
                String version = b.readLine();
                b.close();
                newVersion = (version == null || !version.equals(currentVersion));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (newVersion || file.exists()) {
            Tools.saveTextToFile(currentVersion, file);
        }

        return newVersion;
    }

    private boolean fileExists(String file, boolean overwrite) {
        if (overwrite) {
            return false;
        }

        return new File(DIR_PLUGIN + file).exists();
    }

    private void createFile(String file, boolean overwrite) {
        if (fileExists(file, overwrite)) {
            return;
        }

        try {
            exportResource("/" + file, DIR_PLUGIN);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    /**
     * Export a resource embedded into a Jar file to the local file path.
     *
     * @param resourceName ie.: "/SmartLibrary.dll"
     * @return The path to the exported resource
     * @throws Exception
     */
    private String exportResource(String resourceName, String folder) throws Exception {
        InputStream stream = null;
        OutputStream resStreamOut = null;
        String destFolder;
        try {
            stream = RecipeManager.class.getResourceAsStream(resourceName);//note that each / is a directory down in the "jar tree" been the jar the root of the tree
            if(stream == null) {
                throw new Exception("Cannot get resource \"" + resourceName + "\" from Jar file.");
            }

            int readBytes;
            byte[] buffer = new byte[4096];
            destFolder = new File(folder).getPath().replace('\\', '/');
            resStreamOut = new FileOutputStream(destFolder + resourceName);
            while ((readBytes = stream.read(buffer)) > 0) {
                resStreamOut.write(buffer, 0, readBytes);
            }
        } catch (Exception ex) {
            throw ex;
        } finally {
            if (stream != null) {
                stream.close();
            }
            if (resStreamOut != null) {
                resStreamOut.close();
            }
        }

        return destFolder + resourceName;
    }

    private void createRecipeFlags(boolean overwrite) {
        if (fileExists(FILE_INFO_FLAGS, overwrite)) {
            return;
        }

        StringBuilder s = new StringBuilder(32000);
        Map<String, List<FlagType>> flags = new LinkedHashMap<String, List<FlagType>>();

        String[] category = new String[] { "SHARED FLAGS", "RECIPE ONLY FLAGS", "RESULT ONLY FLAGS" };
        String[] description = new String[] { "Usable on anything - file header, recipe header or result items.", "Usable only on file headers or recipe headers. Can not be used on result items.", "Usable only on recipe's result items. Can not be used on recipes or file header." };

        int size = FlagType.values().length;

        for (String c : category) {
            flags.put(c, new ArrayList<FlagType>(size));
        }

        for (FlagType flag : FlagType.values()) {
            if (flag.hasBit(Bit.RECIPE)) {
                flags.get(category[1]).add(flag);
            } else if (flag.hasBit(Bit.RESULT)) {
                flags.get(category[2]).add(flag);
            } else {
                flags.get(category[0]).add(flag);
            }
        }

        s.append("<title>Recipe Flags</title><pre style='font-family:Lucida Console;font-size:16px;width:100%;'>");
        s.append(NL).append("<a href='basic recipes.html'>Basic Recipes</a> | <a href='advanced recipes.html'>Advanced Recipes</a> | <b>Recipe Flags</b> | <a href='recipe books.html'>Recipe Books</a> | <a href='name index.html'>Name Index</a> | <a href='commands & permissions.html'>Commands &amp; Permissions</a>");
        s.append(NL).append("<h1>Recipe flags</h1>");
        s.append(NL);
        s.append(NL).append("<b>WHAT ARE FLAGS ?</b>");
        s.append(NL).append("  Flags are the stuff that make a recipe very special! You can add various features to a recipe by using flags.");
        s.append(NL).append("  For examples see <a href='advanced recipes.html'><b>advanced recipes.html</b></a>.");
        s.append(NL);
        s.append(NL).append("<b>USING FLAGS</b>");
        s.append(NL).append("  Flags can be added in 3 'zones':");
        s.append(NL).append("  - at the beginning of the file - which are copied to all recipes from that file");
        s.append(NL).append("  - after recipe type (CRAFT, COMBINE, etc) - where they affect that specific recipe, you may even overwrite file flags for that specific recipe!");
        s.append(NL).append("  - after recipe's individual results - to apply flags for the result items.");
        s.append(NL);
        s.append(NL).append("<b>ABOUT ARGUMENTS</b>");
        s.append(NL).append("  Flags have arguments but not always are they all required.");
        s.append(NL).append("  Arguments enclosed between &lt; and &gt; are required and those enclosed between [ and ] are optional.");
        s.append(NL).append("  Some arguments may have 'or false', that means you can just type false in there to make it do something special (most likely disable the flag or a feature)");
        s.append(NL);
        s.append(NL).append("<b>ALIASES</b>");
        s.append(NL).append("  They're just other names for the flag that you can use, they have no special effect if used, only for your preference.");
        s.append(NL);
        s.append(NL);
        s.append(NL);
        s.append("<hr>");
        s.append(NL);
        s.append(NL).append("<a name='contents'></a><h3>CONTENTS</h3>");

        for (String c : category) {
            String key = c.replace(' ', '_').toLowerCase();

            s.append(NL).append("<a href='#").append(key).append("'><b>").append(c).append("</b></a>");

            for (FlagType flag : flags.get(c)) {
                s.append(NL).append("- <a href='#").append(flag.getName()).append("'><b>@").append(flag.getName()).append("</b></a>");
            }

            s.append(NL);
        }

        s.append(NL);

        int categoryLength = category.length;
        for (int t = 0; t < categoryLength; t++) {
            String key = category[t].replace(' ', '_').toLowerCase();

            s.append(NL).append("<a name='").append(key).append("'></a><hr>  <b>").append(category[t]).append("</b>");
            s.append(NL).append("    ").append(description[t]);

            for (FlagType flag : flags.get(category[t])) {
                String[] args = flag.getArguments();
                String[] desc = flag.getDescription();
                String[] ex = flag.getExamples();

                s.append(NL);
                s.append("<hr><a href='#contents' style='font-size:12px;'>^ Contents</a><a name='").append(flag.getName()).append("'></a>");
                s.append(NL);
                s.append(NL);

                if (args != null) {
                    for (String a : args) {
                        s.append(NL).append("  <b>").append(StringEscapeUtils.escapeHtml4(a.replace("{flag}", flag.toString()))).append("</b>");
                    }
                }

                if (desc == null) {
                    desc = new String[] { "Flag not yet documented...", };
                }

                s.append(NL);

                for (String d : desc) {
                    s.append(NL);

                    if (d != null) {
                        s.append("    ").append(StringEscapeUtils.escapeHtml4(d));
                    }
                }

                if (!flag.hasBit(Bit.NO_FALSE)) {
                    s.append(NL).append(NL).append("    Setting to 'false' or 'remove' will disable the flag.");
                }

                if (ex != null) {
                    s.append(NL).append(NL).append("    <b>Examples:</b>");

                    for (String e : ex) {
                        s.append(NL).append("      ").append(StringEscapeUtils.escapeHtml4(e.replace("{flag}", flag.toString())));
                    }
                }

                int flagNamesLength = flag.getNames().length;
                if (flagNamesLength > 1) {
                    s.append(NL).append(NL).append("    <b>Aliases:</b> ");

                    for (int i = 1; i < flagNamesLength; i++) {
                        if (i != 1) {
                            s.append(", ");
                        }

                        s.append('@').append(flag.getNames()[i]);
                    }
                }

                s.append(NL);
                s.append(NL);
            }

            s.append(NL);
        }

        Tools.saveTextToFile(s.toString(), DIR_PLUGIN + FILE_INFO_FLAGS);

        Messages.sendAndLog(sender, RMCChatColor.GREEN + "Generated '" + FILE_INFO_FLAGS + "' file.");
    }

    private void createCommands(boolean overwrite) {
        if (fileExists(FILE_INFO_COMMANDS, overwrite)) {
            return;
        }

        StringBuilder s = new StringBuilder();

        s.append("<title>Commands &amp; permissions</title><pre style='font-family:Lucida Console;font-size:16px;width:100%;'>");
        s.append(NL).append("<a href='basic recipes.html'>Basic Recipes</a> | <a href='advanced recipes.html'>Advanced Recipes</a> | <a href='recipe flags.html'>Recipe Flags</a> | <a href='recipe books.html'>Recipe Books</a> | <a href='name index.html'>Name Index</a> | <b>Commands &amp; Permissions</b>");
        s.append(NL).append("<h1>Commands &amp; permissions</h1>");
        s.append(NL);
        s.append(NL);
        s.append(NL).append("<h2>Commands</h2>");
        s.append("<table style='border-collapse:collapse;' border='1' cellpadding='5'>");

        CommandManager manager = Sponge.getCommandManager();

        Set<CommandMapping> commands = manager.getOwnedBy(plugin);
        ArrayList<CommandMapping> commandsList = new ArrayList<CommandMapping>();
        commandsList.addAll(commands);
        Collections.sort(commandsList, new CommandSorter());

        for (CommandMapping command : commandsList) {
            CommandCallable callable = command.getCallable();

            String permission = null;


            String usage = callable.getUsage(null).toPlain();
            usage = usage.replace("<command>", command.getPrimaryAlias());

            String description = null;
            Optional<? extends Text> opDescription = callable.getShortDescription(null);
            if (opDescription.isPresent()) {
                description = opDescription.get().toPlain();
            }

            Set<String> aliases = command.getAllAliases();

            String aliasesString;
            if (aliases == null || aliases.isEmpty()) {
                aliasesString = "N/A";
            } else {
                aliasesString = RMCUtil.collectionToString(aliases);
            }

            s.append(NL).append("<tr>");
            s.append("<td width='40%'><b>");
            s.append(StringEscapeUtils.escapeHtml4(usage)).append("</b><span style='font-size:14px;'>");
            s.append("<br>Permission: ").append(permission);
            s.append("<br>Aliases: ").append(aliasesString);
            s.append("</span></td>");
            s.append("<td>").append(StringEscapeUtils.escapeHtml4(description)).append("</td>");
            s.append("</tr>");
        }

        s.append(NL).append("</table>");
        s.append(NL);
        s.append(NL);
        s.append(NL).append("<h2>Permissions</h2>");
        s.append("<table style='border-collapse:collapse;' border='1' cellpadding='5'>");
        s.append(NL).append("<tr>");
        s.append("<th>Permission node</th>");
        s.append("<th>Defaulted to</th>");
        s.append("<th>Description</th>");
        s.append("</tr>");

        List<PermissionDescription> perms = new ArrayList<PermissionDescription>();
        Optional<PermissionService> opPermissions = Sponge.getServiceManager().provide(PermissionService.class);
        if (opPermissions.isPresent()) {
            PermissionService permissions = opPermissions.get();

            Collection<PermissionDescription> desc = permissions.getDescriptions();
            perms.addAll(desc);

            Optional<PermissionDescription> opPrefix = permissions.getDescription(Perms.FLAG_PREFIX + "*");
            if (opPrefix.isPresent()) {
                perms.add(opPrefix.get());
            }

            for (FlagType type : FlagType.values()) {
                if (type.hasBit(Bit.NO_SKIP_PERMISSION)) {
                    continue;
                }

                Optional<PermissionDescription> opFlag = permissions.getDescription(Perms.FLAG_PREFIX + type.getName());
                if (opFlag.isPresent()) {
                    perms.add(opFlag.get());
                }
            }
        }

        for (PermissionDescription pd : perms) {
            if (pd.getOwner() == plugin.getPluginContainer() || pd.getId().startsWith("recipemanager.")) {
                s.append(NL).append("<tr>");
                s.append("<td>").append(pd.getId()).append("</td>");
                s.append("<td>");

                Map<Subject, Boolean> subjects = pd.getAssignedSubjects(PermissionService.SUBJECTS_ROLE_TEMPLATE);
                for (Entry<Subject, Boolean> e : subjects.entrySet()) {
                    s.append(e.getKey().getIdentifier() + " " + e.getValue());
                }

                s.append("</td>");
                s.append("<td>").append(pd.getDescription()).append("</td>");
                s.append("</tr>");
            }
        }

        s.append(NL).append("</table>");
        s.append(NL);
        s.append(NL).append("For the flag permissions you can use the flag's aliases as well, I filtered them from this list because it would've become too long, but the permissions are there.");
        s.append(NL).append("For example, <i>recipemanager.flag.modexp</i> and <i>recipemanager.flag.xp</i> both affect the same flag, the @modexp flag, since 'xp' is an alias for 'modexp'.");
        s.append(NL);
        s.append(NL);
        s.append("</pre>");

        Tools.saveTextToFile(s.toString(), DIR_PLUGIN + FILE_INFO_COMMANDS);

        Messages.sendAndLog(sender, RMCChatColor.GREEN + "Generated '" + FILE_INFO_COMMANDS + "' file.");
    }


    private void createNameIndex(boolean overwrite) {
        if (fileExists(FILE_INFO_NAMES, overwrite)) {
            return;
        }

        StringBuilder s = new StringBuilder(24000);

        s.append("<title>Name index</title><pre style='font-family:Lucida Console;font-size:16px;width:100%;'>");
        s.append(NL).append("<a href='basic recipes.html'>Basic Recipes</a> | <a href='advanced recipes.html'>Advanced Recipes</a> | <a href='recipe flags.html'>Recipe Flags</a> | <a href='recipe books.html'>Recipe Books</a> | <b>Name Index</b> | <a href='commands & permissions.html'>Commands &amp; Permissions</a>");
        s.append(NL).append("<h1>Name index</h1>");
        s.append(NL).append("Data extracted from your server and it may contain names added by other plugins/mods!");
        s.append(NL).append("If you want to update this file just delete it and use '<i>rmreload</i>' or start the server.");
        s.append(NL);
        s.append(NL).append("<hr>");
        s.append(NL);
        s.append(NL).append("<a name='contents'></a><h3>CONTENTS</h3>");
        s.append(NL).append("- <a href='#itemtypes'><b>ITEM TYPES</b></a>");


        s.append(NL);
        s.append(NL);
        s.append(NL).append("<hr>");
        s.append(NL);
        s.append(NL).append("<a name='itemtypes'></a><a href='#contents'>^ Contents</a><h3>ITEM TYPES</h3>");
        //s.append("<a href='" + SPONGE_DOCS + "Material.html'>BukkitAPI / Material</a>");
        s.append(NL).append("Data/damage/durability values are listed at <a href='http://www.minecraftwiki.net/wiki/Data_value#Data'>Minecraft Wiki / Data Value</a>");
        s.append(NL);
        s.append(NL).append(String.format(" %-34s %-24s %-24s %-5s", "Id", "Name", "Alias", "Stack"));
        /*
        for (Field field : ItemTypes.class.getDeclaredFields()) {
            if (field.getType().equals(ItemType.class)) {
                try {
                    ItemType item = (ItemType) field.get(field);

                    String alias = null;//Settings.getInstance().getMaterialPrint(m);

                    String aliasString;
                    if (alias == null) {
                        aliasString = "";
                    } else {
                        aliasString = alias;
                    }

                    s.append(NL).append(String.format(" %-34s %-24s %-24s %-5d", item.getId(), field.getName(), aliasString, item.getMaxStackQuantity()));
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        */
        /*
        GameRegistry registry = RecipeManager.getGame().getRegistry();
        Collection<ItemType> allItems = registry.getAllOf(ItemType.class);
        for (ItemType item: allItems) {
            String alias = null;//Settings.getInstance().getMaterialPrint(m);

            String aliasString;
            if (alias == null) {
                aliasString = "";
            } else {
                aliasString = alias;
            }

            s.append(NL).append(String.format(" %-34s %-24s %-5d", item.getId(), aliasString, item.getMaxStackQuantity()));
        }
        */


        Tools.saveTextToFile(s.toString(), DIR_PLUGIN + FILE_INFO_NAMES);
    }
}
