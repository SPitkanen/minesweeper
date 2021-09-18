package minesweeper.bot;

/**
 * This class allows selecting the bot among multiple implementations
 * The bot is selected by changing the static method to return the
 * desired Bot implementation
 */
public class BotSelect {
    /**
     * Returns the currently used Bot implementation
     * @return An object implementing the Bot interface
     */
    public static Bot getBot() {
        // CHANGE THIS LINE TO USE YOUR OWN BOT
        return new ProjectBot();
    }
}
