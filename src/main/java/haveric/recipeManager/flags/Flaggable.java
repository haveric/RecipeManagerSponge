package haveric.recipeManager.flags;

public interface Flaggable {
    boolean hasFlag(FlagType type);

    boolean hasFlags();
    /*
    boolean hasNoShiftBit();
    */
    Flag getFlag(FlagType type);

    <T extends Flag> T getFlag(Class<T> flagClass);

    Flags getFlags();

    void clearFlags();

    void addFlag(Flag flag);
    /*
    boolean checkFlags(Args a);

    boolean sendCrafted(Args a);

    boolean sendPrepare(Args a);
    */

}
