package haveric.recipeManager;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.spongepowered.api.item.Enchantments;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.util.command.CommandSource;

import com.google.common.io.Resources;
import com.google.common.reflect.TypeToken;

import haveric.recipeManager.tools.Tools;
import haveric.recipeManagerCommon.util.RMCUtil;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;

public class Settings {
    private static final boolean SPECIAL_REPAIR_DEFAULT = true;
    private static final boolean SPECIAL_REPAIR_METADATA_DEFAULT = false;

    private static final boolean SPECIAL_LEATHER_DYE_DEFAULT = true;
    private static final boolean SPECIAL_FIREWORKS_DEFAULT = true;
    private static final boolean SPECIAL_MAP_CLONING_DEFAULT = true;
    private static final boolean SPECIAL_MAP_EXTENDING_DEFAULT = true;
    private static final boolean SPECIAL_BOOK_CLONING_DEFAULT = true;
    private static final boolean SPECIAL_BANNER_DEFAULT = true;

    private static final boolean SOUNDS_REPAIR_DEFAULT = true;
    private static final boolean SOUNDS_FAILED_DEFAULT = true;
    private static final boolean SOUNDS_FAILED_CLICK_DEFAULT = true;

    private static final boolean FIX_MOD_RESULTS_DEFAULT = false;
    private static final boolean UPDATE_BOOKS_DEFAULT = true;
    private static final boolean COLOR_CONSOLE_DEFAULT = true;

    private static final String FURNACE_SHIFT_CLICK_DEFAULT = "f";

    private static final boolean MULTITHREADING_DEFAULT = true;

    private static final boolean CLEAR_RECIPES_DEFAULT = false;

    private static final boolean UPDATE_CHECK_ENABLED_DEFAULT = true;
    private static final int UPDATE_CHECK_FREQUENCY_DEFAULT = 6;

    private static final boolean METRICS_DEFAULT = true;

    private static final ItemType MATERIAL_FAIL_DEFAULT = ItemTypes.BARRIER;
    private static final ItemType MATERIAL_SECRET_DEFAULT = ItemTypes.CHEST;
    private static final ItemType MATERIAL_MULTIPLE_RESULTS_DEFAULT = ItemTypes.CHEST;

    private static final boolean DISABLE_OVERRIDE_WARNINGS_DEFAULT = false;

    private static List<String> RECIPE_COMMENT_CHARACTERS_DEFAULT;
    /*
    private static FileConfiguration fileConfig;
    private static FileConfiguration itemAliasesConfig;
    private static FileConfiguration enchantAliasesConfig;
    private static Settings instance;
    */
    private static Map<String, ItemType> materialNames;
    private static Map<ItemType, Map<String, Short>> materialDataNames;
    private static Map<String, Enchantments> enchantNames;

    private static Map<ItemType, String> materialPrint;
    private static Map<ItemType, Map<Short, String>> materialDataPrint;
    private static Map<Enchantments, String> enchantPrint;

    private RecipeManager plugin;
    private File defaultConfigFile;
    private File itemAliasesConfigFile;
    private File enchantAliasesConfigFile;
    private ConfigurationLoader<CommentedConfigurationNode> fileConfigManager;
    private ConfigurationLoader<CommentedConfigurationNode> itemAliasesConfigManager;
    private ConfigurationLoader<CommentedConfigurationNode> enchantAliasesConfigManager;

    private File defaultFolder;

    private static CommentedConfigurationNode fileConfig;
    private static CommentedConfigurationNode itemAliasesConfig;
    private static CommentedConfigurationNode enchantAliasesConfig;

    public Settings(RecipeManager recipeManager, File defaultConfig) {
        plugin = recipeManager;
        defaultFolder = defaultConfig.getParentFile();

        String defaultPath = defaultFolder.getPath() + File.separator;
        defaultConfigFile = new File(defaultPath + Files.FILE_CONFIG);
        itemAliasesConfigFile = new File(defaultPath + Files.FILE_ITEM_ALIASES);
        enchantAliasesConfigFile = new File(defaultPath + Files.FILE_ENCHANT_ALIASES);

        init();
    }

    private void init() {
        RECIPE_COMMENT_CHARACTERS_DEFAULT = new ArrayList<String>();
        RECIPE_COMMENT_CHARACTERS_DEFAULT.add("//");
        RECIPE_COMMENT_CHARACTERS_DEFAULT.add("#");

        materialNames = new HashMap<String, ItemType>();
        materialDataNames = new HashMap<ItemType, Map<String, Short>>();
        enchantNames = new HashMap<String, Enchantments>();
        materialPrint = new HashMap<ItemType, String>();
        materialDataPrint = new HashMap<ItemType, Map<Short, String>>();
        enchantPrint = new HashMap<Enchantments, String>();

        if (!defaultFolder.exists()) {
            defaultFolder.mkdirs();
        }

        try {
            if (!defaultConfigFile.exists()) {
                defaultConfigFile.createNewFile();

                URL fileConfigDefaultUrl = RecipeManager.class.getResource("/" + Files.FILE_CONFIG);
                fileConfigManager = HoconConfigurationLoader.builder()
                        .setSource(Resources.asCharSource(fileConfigDefaultUrl, StandardCharsets.UTF_8))
                        .setSink(com.google.common.io.Files.asCharSink(defaultConfigFile, StandardCharsets.UTF_8)).build();
                fileConfig = fileConfigManager.load();

                fileConfigManager.save(fileConfig);
            } else {
                fileConfigManager = HoconConfigurationLoader.builder().setFile(defaultConfigFile).build();
            }

            if (!itemAliasesConfigFile.exists()) {
                itemAliasesConfigFile.createNewFile();

                URL itemAliasesConfigDefaultUrl = RecipeManager.class.getResource("/" + Files.FILE_ITEM_ALIASES);
                itemAliasesConfigManager = HoconConfigurationLoader.builder()
                        .setSource(Resources.asCharSource(itemAliasesConfigDefaultUrl, StandardCharsets.UTF_8))
                        .setSink(com.google.common.io.Files.asCharSink(itemAliasesConfigFile, StandardCharsets.UTF_8)).build();
                itemAliasesConfig = itemAliasesConfigManager.load();

                itemAliasesConfigManager.save(itemAliasesConfig);
            } else {
                itemAliasesConfigManager = HoconConfigurationLoader.builder().setFile(itemAliasesConfigFile).build();
            }

            if (!enchantAliasesConfigFile.exists()) {
                enchantAliasesConfigFile.createNewFile();

                URL enchantAliasesConfigDefaultUrl = RecipeManager.class.getResource("/" + Files.FILE_ENCHANT_ALIASES);
                enchantAliasesConfigManager = HoconConfigurationLoader.builder()
                        .setSource(Resources.asCharSource(enchantAliasesConfigDefaultUrl, StandardCharsets.UTF_8))
                        .setSink(com.google.common.io.Files.asCharSink(enchantAliasesConfigFile, StandardCharsets.UTF_8)).build();
                enchantAliasesConfig = enchantAliasesConfigManager.load();

                enchantAliasesConfigManager.save(enchantAliasesConfig);
            } else {
                enchantAliasesConfigManager = HoconConfigurationLoader.builder().setFile(enchantAliasesConfigFile).build();
            }

            fileConfig = fileConfigManager.load();
            itemAliasesConfig = itemAliasesConfigManager.load();
            enchantAliasesConfig = enchantAliasesConfigManager.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void reload(CommandSource sender) {
        init();

        String lastChanged = fileConfig.getNode("aalastchanged").getString();

        if (!Files.LASTCHANGED_CONFIG.equals(lastChanged)) {
            Messages.sendAndLog(sender, "<yellow>NOTE: <reset>'" + Files.FILE_CONFIG + "' file is outdated, please delete it to allow it to be generated again.");
        }

        Messages.info("config.yml settings:");
        Messages.info("    special-recipes.repair: " + getSpecialRepair());
        Messages.info("    special-recipes.repair-metadata: " + getSpecialRepairMetadata());
        Messages.info("    special-recipes.leather-dye: " + getSpecialLeatherDye());
        Messages.info("    special-recipes.fireworks: " + getSpecialFireworks());
        Messages.info("    special-recipes.map-cloning: " + getSpecialMapCloning());
        Messages.info("    special-recipes.map-extending: " + getSpecialMapExtending());
        Messages.info("    special-recipes.book-cloning: " + getSpecialBookCloning());
        Messages.info("    special-recipes.banner: " + getSpecialBanner());
        Messages.info("    sounds.failed: " + getSoundsFailed());
        Messages.info("    sounds.failed_click: " + getSoundsFailedClick());
        Messages.info("    sounds.repair: " + getSoundsRepair());
        Messages.info("    update-books: " + getUpdateBooks());
        Messages.info("    color-console: " + getColorConsole());
        Messages.info("    furnace-shift-click: " + getFurnaceShiftClick());
        Messages.info("    multithreading: " + getMultithreading());
        Messages.info("    fix-mod-results: " + getFixModResults());
        Messages.info("    clear-recipes: " + getClearRecipes());
        Messages.info("    update-check.enabled: " + getUpdateCheckEnabled());
        Messages.info("    update-check.frequency: " + getUpdateCheckFrequency());
        Messages.info("    metrics: " + getMetrics());
        Messages.info("    material.fail: " + getFailMaterial());
        Messages.info("    material.secret: " + getSecretMaterial());
        Messages.info("    material.multiple-results: " + getMultipleResultsMaterial());
        Messages.info("    disable-override-warnings: " + getDisableOverrideWarnings());
        Messages.info("    recipe-comment-characters: " + getRecipeCommentCharacters());

        if (!Files.LASTCHANGED_ITEM_ALIASES.equals(itemAliasesConfig.getNode("aalastchanged").getString())) {
            Messages.sendAndLog(sender, "<yellow>NOTE: <reset>'" + Files.FILE_ITEM_ALIASES + "' file is outdated, please delete it to allow it to be generated again.");
        }
        /*
        for (String arg : itemAliasesConfig.getKeys(false)) {
            if (arg.equals("aalastchanged")) {
                continue;
            }

            Material material = Material.matchMaterial(arg);

            if (material == null) {
                Messages.sendAndLog(sender, "<yellow>WARNING: <reset>'" + Files.FILE_ITEM_ALIASES + "' has invalid material definition: " + arg);
                continue;
            }

            Object value = itemAliasesConfig.get(arg);

            if (value instanceof String) {
                parseMaterialNames(sender, (String) value, material);
            } else if (value instanceof ConfigurationSection) {
                ConfigurationSection section = (ConfigurationSection) value;

                for (String key : section.getKeys(false)) {
                    if (key.equals("names")) {
                        parseMaterialNames(sender, section.getString(key), material);
                    } else {
                        try {
                            parseMaterialDataNames(sender, section.getString(key), Short.valueOf(key), material);
                        } catch (NumberFormatException e) {
                            Messages.sendAndLog(sender, "<yellow>WARNING: <reset>'" + Files.FILE_ITEM_ALIASES + "' has invalid data value number: " + key + " for material: " + material);
                            continue;
                        }
                    }
                }
            } else {
                Messages.sendAndLog(sender, "<yellow>WARNING: <reset>'" + Files.FILE_ITEM_ALIASES + "' has invalid data type at: " + arg);
                continue;
            }
        }
        */

        if (!Files.LASTCHANGED_ENCHANT_ALIASES.equals(enchantAliasesConfig.getNode("aalastchanged").getString())) {
            Messages.sendAndLog(sender, "<yellow>NOTE: <reset>'" + Files.FILE_ENCHANT_ALIASES + "' file is outdated, please delete it to allow it to be generated again.");
        }
        /*
        for (String arg : enchantAliasesConfig.getKeys(false)) {
            if (arg.equals("aalastchanged")) {
                continue;
            }

            Enchantment enchant = Enchantment.getByName(arg.toUpperCase());

            if (enchant == null) {
                Messages.sendAndLog(sender, "<yellow>WARNING: <reset>'" + Files.FILE_ENCHANT_ALIASES + "' has invalid enchant definition: " + arg);
                continue;
            }

            String names = enchantAliasesConfig.getString(arg);
            String[] split = names.split(",");

            for (String str : split) {
                str = str.trim();
                String parsed = RMCUtil.parseAliasName(str);

                if (enchantNames.containsKey(parsed)) {
                    Messages.sendAndLog(sender, "<yellow>WARNING: <reset>'" + Files.FILE_ENCHANT_ALIASES + "' has duplicate enchant alias '" + str + "' for enchant " + enchant);
                    continue;
                }

                enchantNames.put(parsed, enchant);

                if (!enchantPrint.containsKey(enchant)) {
                    enchantPrint.put(enchant, Tools.parseAliasPrint(str));
                }
            }
        }
        */

        String failString = fileConfig.getNode("material", "fail").getString(MATERIAL_FAIL_DEFAULT.toString());
        /*
        Material failMaterial = Material.matchMaterial(failString);
        if (failMaterial == null) {
            Messages.sendAndLog(sender, "<yellow>WARNING: <reset>'" + "material.fail has invalid material definition: " + failString + ". Defaulting to " + MATERIAL_FAIL_DEFAULT.toString() + ".");
        }
        */

        String secretString = fileConfig.getNode("material", "secret").getString(MATERIAL_SECRET_DEFAULT.toString());
        /*
        Material secretMaterial = Material.matchMaterial(secretString);
        if (secretMaterial == null) {
            Messages.sendAndLog(sender, "<yellow>WARNING: <reset>'" + "material.secret has invalid material definition: " + secretString + ". Defaulting to " + MATERIAL_SECRET_DEFAULT.toString() + ".");
        }
        */
        String multipleResultsString = fileConfig.getNode("material", "multiple-results").getString(MATERIAL_MULTIPLE_RESULTS_DEFAULT.toString());
        /*
        Material multipleResultsMaterial = Material.matchMaterial(multipleResultsString);
        if (multipleResultsMaterial == null) {
            Messages.sendAndLog(sender, "<yellow>WARNING: <reset>'" + "material.multiple-results has invalid material definition: " + multipleResultsString + ". Defaulting to " + MATERIAL_MULTIPLE_RESULTS_DEFAULT.toString() + ".");
        }
        */
    }

    public File getDefaultFolder() {
        return defaultFolder;
    }

    public String getDefaultFolderPath() {
        return defaultFolder.getPath();
    }

    private void parseMaterialNames(CommandSource sender, String names, ItemType material) {
        if (names == null) {
            return;
        }

        String[] split = names.split(",");

        for (String str : split) {
            str = str.trim();
            String parsed = RMCUtil.parseAliasName(str);

            if (materialNames.containsKey(parsed)) {
                Messages.sendAndLog(sender, "<yellow>WARNING: <reset>'" + Files.FILE_ITEM_ALIASES + "' has duplicate material alias '" + str + "' for material " + material);
                continue;
            }

            materialNames.put(parsed, material);

            if (!materialPrint.containsKey(material)) {
                materialPrint.put(material, Tools.parseAliasPrint(str));
            }
        }
    }

    private void parseMaterialDataNames(CommandSource sender, String names, short data, ItemType material) {
        if (names == null) {
            return;
        }

        String[] split = names.split(",");

        for (String str : split) {
            str = str.trim();
            Map<String, Short> dataMap = materialDataNames.get(material);

            if (dataMap == null) {
                dataMap = new HashMap<String, Short>();
                materialDataNames.put(material, dataMap);
            }

            String parsed = RMCUtil.parseAliasName(str);

            if (dataMap.containsKey(parsed)) {
                Messages.sendAndLog(sender, "<yellow>WARNING: <reset>'" + Files.FILE_ITEM_ALIASES + "' has duplicate data alias '" + str + "' for material " + material + " and data value " + data);
                continue;
            }

            dataMap.put(parsed, data);

            Map<Short, String> printMap = materialDataPrint.get(material);

            if (printMap == null) {
                printMap = new HashMap<Short, String>();
                materialDataPrint.put(material, printMap);
            }

            if (!printMap.containsKey(data)) {
                printMap.put(data, Tools.parseAliasPrint(str));
            }
        }
    }

    public boolean getSpecialRepair() {
        return fileConfig.getNode("special-recipes", "repair").getBoolean(SPECIAL_REPAIR_DEFAULT);
    }

    public boolean getSpecialRepairMetadata() {
        return fileConfig.getNode("special-recipes", "repair-metadata").getBoolean(SPECIAL_REPAIR_METADATA_DEFAULT);
    }

    public boolean getSpecialLeatherDye() {
        return fileConfig.getNode("special-recipes", "leather-armor-dye").getBoolean(SPECIAL_LEATHER_DYE_DEFAULT);
    }

    public boolean getSpecialFireworks() {
        return fileConfig.getNode("special-recipes", "fireworks").getBoolean(SPECIAL_FIREWORKS_DEFAULT);
    }

    public boolean getSpecialMapCloning() {
        return fileConfig.getNode("special-recipes", "map-cloning").getBoolean(SPECIAL_MAP_CLONING_DEFAULT);
    }

    public boolean getSpecialMapExtending() {
        return fileConfig.getNode("special-recipes", "map-extending").getBoolean(SPECIAL_MAP_EXTENDING_DEFAULT);
    }

    public boolean getSpecialBookCloning() {
        return fileConfig.getNode("special-recipes", "book-cloning").getBoolean(SPECIAL_BOOK_CLONING_DEFAULT);
    }

    public boolean getSpecialBanner() {
        return fileConfig.getNode("special-recipes", "banner").getBoolean(SPECIAL_BANNER_DEFAULT);
    }

    public boolean getSoundsRepair() {
        return fileConfig.getNode("sounds", "repair").getBoolean(SOUNDS_REPAIR_DEFAULT);
    }

    public boolean getSoundsFailed() {
        return fileConfig.getNode("sounds", "failed").getBoolean(SOUNDS_FAILED_DEFAULT);
    }

    public boolean getSoundsFailedClick() {
        return fileConfig.getNode("sounds", "failed_click").getBoolean(SOUNDS_FAILED_CLICK_DEFAULT);
    }

    public boolean getFixModResults() {
        return fileConfig.getNode("fix-mod-results").getBoolean(FIX_MOD_RESULTS_DEFAULT);
    }

    public boolean getUpdateBooks() {
        return fileConfig.getNode("update-books").getBoolean(UPDATE_BOOKS_DEFAULT);
    }

    public static boolean getColorConsole() {
        return fileConfig.getNode("color-console").getBoolean(COLOR_CONSOLE_DEFAULT);
    }

    public char getFurnaceShiftClick() {
        return fileConfig.getNode("furnace-shift-click").getString(FURNACE_SHIFT_CLICK_DEFAULT).charAt(0);
    }

    public boolean getMultithreading() {
        return fileConfig.getNode("multithreading").getBoolean(MULTITHREADING_DEFAULT);
    }

    public boolean getClearRecipes() {
        return fileConfig.getNode("clear-recipes").getBoolean(CLEAR_RECIPES_DEFAULT);
    }

    public boolean getUpdateCheckEnabled() {
        return fileConfig.getNode("update-check", "enabled").getBoolean(UPDATE_CHECK_ENABLED_DEFAULT);
    }

    public int getUpdateCheckFrequency() {
        return Math.max(fileConfig.getNode("update-check", "frequency").getInt(UPDATE_CHECK_FREQUENCY_DEFAULT), 0);
    }

    public boolean getMetrics() {
        return fileConfig.getNode("metrics").getBoolean(METRICS_DEFAULT);
    }

    public ItemType getFailMaterial() {
        String failString = fileConfig.getNode("material", "fail").getString(MATERIAL_FAIL_DEFAULT.toString());

        ItemType failMaterial = null; //TODO: Material.matchMaterial(failString);

        if (failMaterial == null) {
            failMaterial = MATERIAL_FAIL_DEFAULT;
        }

        return failMaterial;
    }

    public ItemType getSecretMaterial() {
        String secretString = fileConfig.getNode("material", "secret").getString(MATERIAL_SECRET_DEFAULT.toString());

        ItemType secretMaterial = null;//TODO: Material.matchMaterial(secretString);

        if (secretMaterial == null) {
            secretMaterial = MATERIAL_SECRET_DEFAULT;
        }

        return secretMaterial;
    }

    public ItemType getMultipleResultsMaterial() {
        String multipleResultsString = fileConfig.getNode("material", "multiple-results").getString(MATERIAL_MULTIPLE_RESULTS_DEFAULT.toString());

        ItemType multipleResultsMaterial = null;//TODO: Material.matchMaterial(multipleResultsString);

        if (multipleResultsMaterial == null) {
            multipleResultsMaterial = MATERIAL_MULTIPLE_RESULTS_DEFAULT;
        }

        return multipleResultsMaterial;
    }

    public boolean getDisableOverrideWarnings() {
        return fileConfig.getNode("disable-override-warnings").getBoolean(DISABLE_OVERRIDE_WARNINGS_DEFAULT);
    }

    public String getRecipeCommentCharacters() {
        String allComments = "";
        List<String> comments = getRecipeCommentCharactersAsList();

        for (int i = 0; i < comments.size(); i++) {
            if (i > 0) {
                allComments += ",";
            }

            allComments += comments.get(i);
        }

        return allComments;
    }

    public List<String> getRecipeCommentCharactersAsList() {
        List<String> comments;
        try {
            comments =  fileConfig.getNode("recipe-comment-characters").getList(TypeToken.of(String.class), RECIPE_COMMENT_CHARACTERS_DEFAULT);
        } catch (ObjectMappingException e) {
            comments = RECIPE_COMMENT_CHARACTERS_DEFAULT;
            e.printStackTrace();
        }

        return comments;
    }

    public Enchantments getEnchantment(String name) {
        return enchantNames.get(name);
    }

    public ItemType getMaterial(String name) {
        return materialNames.get(RMCUtil.parseAliasName(name));
    }

    public Map<String, Short> getMaterialDataNames(ItemType material) {
        return materialDataNames.get(material);
    }

    public String getMaterialPrint(ItemType material) {
        return materialPrint.get(material);
    }

    public Map<Short, String> getMaterialDataPrint(ItemType material) {
        return materialDataPrint.get(material);
    }

    public String getEnchantPrint(Enchantments enchant) {
        return enchantPrint.get(enchant);
    }

}
