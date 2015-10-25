package haveric.recipeManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.TextBuilder;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

import haveric.recipeManager.tools.Tools;

/**
 * This class is used by RecipeManager to display recipe errors.<br>
 * Errors can be caught to be displayed in a single chunk along with file name and lines.<br>
 * When errors are not caught they'll be directly displayed to console.
 */
public class ErrorReporter {
    private static HashMap<String, List<Text>> fileErrors;
    private static String currentFile;
    private static int currentLine;
    private static boolean ignore = false;

    /**
     * Starts catching reported errors and stores them in a list for later printing.<br>
     * This also resets file to null and line to 0
     */
    public static void startCatching() {
        stopCatching();
        fileErrors = new HashMap<String, List<Text>>();
    }

    /**
     * Stops catching the errors and ditches any caught errors so far!<br>
     * Calling this requires calling {@link #startCatching()} again to queue errors.
     */
    public static void stopCatching() {
        fileErrors = null;
        currentFile = null;
        currentLine = 0;
        ignore = false;
    }

    /**
     * Check if class caught any errors.
     *
     * @return true if catching, false otherwise
     */
    public static boolean isCatching() {
        return fileErrors != null;
    }

    /**
     * Gets the amount of queued errors.
     *
     * @return 0 if no errors, -1 if not catching at all
     */
    public static int getCaughtAmount() {
        int caught;

        if (isCatching()) {
            caught = fileErrors.size();
        } else {
            caught = -1;
        }

        return caught;
    }

    /**
     * Print the queued errors (if any)
     *
     * @param logFile
     */
    public static void print(String logFile) {
        if (!isCatching() || fileErrors.isEmpty()) {
            stopCatching();
            return;
        }

        String lastError;
        int lastErrorNum;
        int similarErrors;

        TextBuilder buffer;
        TextBuilder text = Texts.builder();

        for (Entry<String, List<Text>> entry : fileErrors.entrySet()) {
            buffer = Texts.builder();
            buffer.append(Texts.of(TextStyles.BOLD, TextColors.AQUA, "File: " + entry.getKey() + Files.NL));

            lastError = "";
            lastErrorNum = 0;
            similarErrors = 0;

            for (Text error : entry.getValue()) {
                String errorString = Texts.toPlain(error);
                if (errorString.startsWith(lastError, 10)) {
                    if (++lastErrorNum > 3) {
                        similarErrors++;
                        continue;
                    }
                } else {
                    if (similarErrors > 0) {
                        buffer.append(Texts.of(TextColors.RED, "... and " + similarErrors + " more similar errors." + Files.NL));
                    }

                    lastErrorNum = 0;
                    similarErrors = 0;
                }

                buffer.append(Texts.of(TextColors.WHITE, error + Files.NL));
                lastError = errorString.substring(10);
            }

            if (similarErrors > 0) {
                buffer.append(Texts.of(TextColors.RED, "... and " + similarErrors + " more similar errors." + Files.NL));
            }

            buffer.append(Texts.of(Files.NL));
            text.append(Texts.of(buffer));
            Messages.info(buffer.build());
        }

        text.append(Texts.of(Files.NL + Files.NL));


        if (logFile != null && Tools.saveTextToFile(Texts.toPlain(text.build()), logFile)) {
            Messages.info(Texts.of(TextColors.YELLOW, "Error messages saved in" + logFile + '.'));
        }

        stopCatching();
    }

    /**
     * Set the current file path/name - printed in queued errors.<br>
     * This also resets line to 0.
     *
     * @param line
     */
    public static void setFile(String file) {
        currentFile = file;
        currentLine = 0;
    }

    /**
     * @return the current file the parser is at.
     */
    public static String getFile() {
        return currentFile;
    }

    /**
     * Set the current line - printed in queued errors.<br>
     * This will be reset to 0 after calling {@link #setFile()}
     *
     * @param line
     */
    public static void setLine(int line) {
        currentLine = line;
    }

    /**
     * @return the current line the parser is at.
     */
    public static int getLine() {
        return currentLine;
    }

    /**
     * This can be used to temporarily ignore any errors that are stored.<br>
     * <b>NOTE: Only works when catching errors, use with care.</b>
     *
     * @param set
     */
    protected static void setIgnoreErrors(boolean set) {
        if (isCatching()) {
            ignore = set;
        }
    }

    protected static boolean getIgnoreErrors() {
        return ignore;
    }

    public static void warning(String warning) {
        warning(warning, null);
    }

    public static void warning(String warning, String tip) {
        entry(Texts.of(TextColors.YELLOW, TextStyles.UNDERLINE, "Warning"), warning, tip);
    }

    /**
     * Queue error or print it directly if queue was not started.
     *
     * @param error
     * @return always returns false, useful for quick returns
     */
    public static boolean error(String error) {
        return error(error, null);
    }

    /**
     * Queue error or print it directly if queue was not started.
     *
     * @param error
     *            the error message
     * @param tip
     *            optional tip, use null to avoid
     * @return always returns false, useful for quick returns
     */
    public static boolean error(String error, String tip) {
        entry(Texts.of(TextColors.RED, TextStyles.UNDERLINE, "Warning"), error, tip);
        return false;
    }

    private static void entry(Text type, String message, String tip) {
        if (!isCatching()) {
            type = Texts.of(type, ":", TextColors.RESET, " " + message);

            if (tip != null) {
                type = Texts.of(type, TextColors.DARK_GREEN, " TIP: ", TextColors.GRAY, tip);
            }

            Messages.info(type);
        } else if (!ignore) {
            List<Text> errors = fileErrors.get(currentFile);

            if (errors == null) {
                errors = new ArrayList<Text>();
            }

            type = Texts.of("line " + String.format("%-5d", currentLine), type, ": ", TextColors.RESET, message);

            if (tip != null) {
                type = Texts.of(type, Files.NL, TextColors.DARK_GREEN, "          TIP: ", TextColors.GRAY, tip);
            }
            errors.add(type);

            fileErrors.put(currentFile, errors);
        }
    }
}
