package haveric.recipeManager.flags;

import haveric.recipeManager.ErrorReporter;
import haveric.recipeManager.Files;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class Flags {
    private Map<FlagType, Flag> flags = new LinkedHashMap<FlagType, Flag>();
    protected Flaggable flaggable;

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder(flags.size() * 24);
        boolean first = true;

        for (Flag f : flags.values()) {
            if (first) {
                first = false;
            } else {
                s.append(", ");
            }

            s.append(f.getType());
        }

        String toReturn;
        if (s.length() > 0) {
            toReturn = s.toString();
        } else {
            toReturn = "empty";
        }

        return toReturn;
    }

    public Flags() { }

    public Flags(Flaggable newFlaggable) {
        flaggable = newFlaggable;
    }

    public Flag getFlag(FlagType type) {
        return flags.get(type);
    }

    public <T extends Flag> T getFlag(Class<T> flagClass) {
        return flagClass.cast(flags.get(FlagType.getByClass(flagClass)));
    }

    public boolean hasFlag(FlagType type) {
        return flags.containsKey(type);
    }

    public boolean canAdd(Flag flag) {
        return flag != null && flag.validate();
    }

    public void addFlag(Flag flag) {
        Flags prevContainer = flag.flagsContainer;
        flag.flagsContainer = this;

        if (canAdd(flag)) {
            flags.put(flag.getType(), flag);
        } else {
            flag.flagsContainer = prevContainer;
        }
    }

    public void parseFlag(String value) {
        // Input value must not be null!
        if (value == null) {
            return;
        }

        value = value.trim();

        if (value.charAt(0) != '@') {
            ErrorReporter.warning("Flags must start with @ character!");
            return;
        }

        String[] split = value.split("[:\\s]+", 2); // split by space or : char
        String flagString = split[0].trim(); // format flag name
        FlagType type = FlagType.getByName(flagString); // Find the current flag

        // If no valid flag was found
        if (type == null) {
            ErrorReporter.warning("Unknown flag: " + flagString, "Name might be different, check '" + Files.FILE_INFO_FLAGS + "' for flag list.");
            return;
        }

        Flag flag = flags.get(type); // get existing flag, if any

        if (flag == null) {
            flag = type.createFlagClass(); // create a new instance of the flag does not exist
        }

        flag.flagsContainer = this; // set container before hand to allow checks
        if (split.length > 1) {
            value = split[1].trim();
        } else {
            value = null;
        }

        // make sure the flag can be added to this flag list
        if (!flag.validateParse(value)) {
            return;
        }

        // check if parsed flag had valid values and needs to be added to flag list
        if (flag.onParse(value)) {
            flags.put(flag.getType(), flag);
        }
    }

    public void removeFlag(Flag flag) {
        if (flag == null) {
            return;
        }

        removeFlag(flag.getType());
    }

    public void removeFlag(FlagType type) {
        if (type == null) {
            return;
        }

        Flag flag = flags.remove(type);

        if (flag != null) {
            flag.onRemove();
            flag.flagsContainer = null;
        }
    }

    public Flaggable getFlaggable() {
        return flaggable;
    }
    /*
    public boolean checkFlags(Args a) {
        a.clear();

        for (Flag flag : flags.values()) {
            flag.check(a);
        }

        return !a.hasReasons();
    }
    */

    public Collection<Flag> get() {
        return flags.values();
    }
    /*
    public boolean sendPrepare(Args a) {
        a.clear();

        for (Flag flag : flags.values()) {
            flag.prepare(a);
        }

        return !a.hasReasons();
    }
    */
    /**
     * Applies all flags to player/location/result and compiles a list of failure reasons while returning if the list is empty (no errors). Note: not all arguments are used, you may use null wherever
     * you don't have anything to give.
     *
     * @param a
     *            arguments class
     * @return false if something was absolutely required and crafting should be cancelled
     */
    /*
    public boolean sendCrafted(Args a) {
        a.clear();

        for (Flag flag : flags.values()) {
            flag.crafted(a);
        }

        return !a.hasReasons();
    }
    */

    /**
     * Sends failure notification to all flags
     *
     * @param a
     *            arguments class
     */
    /*
    public void sendFailed(Args a) {
        a.clear();

        for (Flag flag : flags.values()) {
            flag.failed(a);
        }
    }
    */
    /**
     * Notifies all flags that the recipe was registered.<br>
     * Shouldn't really be triggered manually.
     */
    /*
    public void sendRegistered() {
        for (Flag flag : flags.values()) {
            flag.registered();
        }
    }
    */

    /**
     * Copy this flag storage and give it a new container.
     *
     * @param newContainer
     * @return
     */
    public Flags clone(Flaggable newContainer) {
        Flags clone = clone();
        clone.flaggable = newContainer;
        return clone;
    }

    @Override
    public Flags clone() {
        Flags clone = new Flags();

        for (Flag f : flags.values()) {
            f = f.clone();
            f.flagsContainer = clone;
            clone.flags.put(f.getType(), f);
        }

        return clone;
    }
}
