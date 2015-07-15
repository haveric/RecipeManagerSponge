package haveric.recipeManager.flags;

import haveric.recipeManager.Messages;

import java.util.HashMap;
import java.util.Map;

public enum FlagType {
 // Shared flags
    COMMAND(FlagCommand.class, Bit.NONE, "cmd", "commands");

    private final Class<? extends Flag> flagClass;
    private final String[] names;
    private final int bits;

    private FlagType(Class<? extends Flag> newFlagClass, int newBits, String... aliases) {
        flagClass = newFlagClass;
        bits = newBits;

        names = new String[aliases.length + 1];
        names[0] = name().toLowerCase();

        for (int i = 0; i < aliases.length; i++) {
            names[i + 1] = aliases[i];
        }
    }

    /**
     * Checks if flag type has a special bit.
     *
     * @param bit
     *            See {@link Bit}
     * @return
     */
    public boolean hasBit(int bit) {
        return (bits & bit) == bit;
    }

    /**
     * @return the class assigned to this type (not the instance)
     */
    public Class<? extends Flag> getFlagClass() {
        return flagClass;
    }

    /**
     * @return array of flags names, index 0 is always the main name
     */
    public String[] getNames() {
        return names.clone();
    }

    /**
     * @return the first name of the flag
     */
    public String getName() {
        return names[0];
    }

    /**
     * @return a new instance of the class assigned to this type or null if failed and prints stack trace.
     */
    public Flag createFlagClass() {
        try {
            return flagClass.newInstance();
        } catch (Throwable e) {
            Messages.error(null, e, "");
        }

        return null;
    }
    /*
    public String[] getArguments() {
        return getField("A");
    }

    public String[] getExamples() {
        return getField("E");
    }

    public String[] getDescription() {
        return getField("D");
    }
    */

    /**
     * Gets the <code>@flag</code> style flag name
     */
    @Override
    public String toString() {
        return '@' + names[0];
    }

    private static final Map<String, FlagType> nameMap = new HashMap<String, FlagType>();
    private static final Map<Class<? extends Flag>, FlagType> classMap = new HashMap<Class<? extends Flag>, FlagType>();

    /**
     * You should not call this method.<br> <br>
     * It is used by the plugin to add the flags to an index map then create and add individual no-flag permissions.
     */
    public static void init() {
        /*
        Permission parent = Bukkit.getPluginManager().getPermission(Perms.FLAG_ALL);

        if (parent == null) {
            parent = new Permission(Perms.FLAG_ALL, PermissionDefault.TRUE);
            parent.setDescription("Allows use of flag.");

            Bukkit.getPluginManager().addPermission(parent);
        }

        Permission p;

        for (FlagType type : values()) {
            classMap.put(type.getFlagClass(), type);

            for (String name : type.names) {
                nameMap.put(name, type);

                if (type.hasBit(Bit.NO_SKIP_PERMISSION)) {
                    continue;
                }

                if (Bukkit.getPluginManager().getPermission(Perms.FLAG_PREFIX + name) != null) {
                    continue;
                }

                p = new Permission(Perms.FLAG_PREFIX + name, PermissionDefault.TRUE);
                p.setDescription("Allows use of the " + type + " flag.");
                p.addParent(parent, true);
                Bukkit.getPluginManager().addPermission(p);
            }
        }
        */
    }

    /**
     * Get the FlagType object for a flag name or alias.
     *
     * @param flag
     *            flag name or alias
     * @return FlagType if found or null
     */
    public static FlagType getByName(String flag) {
        if (flag == null) {
            return null;
        }

        if (flag.charAt(0) != '@') {
            throw new IllegalArgumentException("Flag string must start with @");
        }

        return nameMap.get(flag.substring(1).toLowerCase());
    }

    /**
     * Get the FlagType object for the specified class.
     *
     * @param flagClass
     *            flag's .class
     * @return FlagType if found or null
     */
    public static FlagType getByClass(Class<? extends Flag> flagClass) {
        return classMap.get(flagClass);
    }

    /**
     * Flag bits to configure special behavior
     */
    public class Bit {
        public static final byte NONE = 0;

        /**
         * Flag only works in recipes.
         */
        public static final byte RECIPE = 1 << 0;

        /**
         * Flag only works on results.
         */
        public static final byte RESULT = 1 << 1;

        /**
         * No value is allowed for this flag.
         */
        public static final byte NO_VALUE = 1 << 2;

        /**
         * Disables flag from being stored - used on flags that directly affect result's metadata.
         */
        public static final byte NO_FOR = 1 << 3;

        /**
         * Disables "false" or "remove" values from removing the flag.
         */
        public static final byte NO_FALSE = 1 << 4;

        /**
         * Disables shift+click on the recipe if there is at least one flag with this bit.
         */
        public static final byte NO_SHIFT = 1 << 5;

        /**
         * Disables generating a skip permission for this flag
         */
        public static final byte NO_SKIP_PERMISSION = 1 << 6;
    }
}
