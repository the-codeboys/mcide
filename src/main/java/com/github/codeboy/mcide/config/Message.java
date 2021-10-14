package com.github.codeboy.mcide.config;

import ml.codeboy.bukkitbootstrap.config.ConfigScope;
import ml.codeboy.bukkitbootstrap.config.Configurable;
import org.bukkit.ChatColor;

@Configurable(comments = "Edit these messages however you like\n" +
        "It should be self explanatory where they are used",scope = ConfigScope.PUBLIC)
public class Message {

    // region GUI
    public static String
            RUN_OPTION_TITLE = "Run options",
            RUN_CHAT_OPTION = "Chat output",
            RUN_CHAT_OPTION_LORE = "Prints the output to chat",
            RUN_BOOK_OPTION = "Book output",
            RUN_BOOK_OPTION_LORE = "Will redirect the output to written book",
            RUN_AND_EXECUTE_OPTION = "Execute output as command",
            RUN_AND_EXECUTE_OPTION_LORE = "Will execute output of the code as command";
    // endregion
    // region info
    public static String PROJECTS = "projects",
            EXECUTION_START = ChatColor.BLUE + "Running project {0}",
            RIGHT_CLICK_TO_EDIT = ChatColor.BLUE + "Right click to edit. Move to cancel",
            BACK = ChatColor.BLUE + "back",
            RUN = ChatColor.GREEN + "run",
            SELECT_LANGUAGE = "select a language",
            CREATE_FILE = ChatColor.GREEN + "create file",
            DELETE_FILE = ChatColor.RED + "delete file",
            EDIT_CANCELLED = ChatColor.DARK_RED + "Cancelled editing file {0}";
    //region error
    public static String CMD_PLAYER_ONLY = ChatColor.RED + "Only players can use this command",
            LANGUAGE_AND_NAME_REQUIRED = ChatColor.RED + "You need to specify a language and a name for the project",
            NAME_REQUIRED = ChatColor.RED + "You need to specify a name!",
            RUN_PROJECT_ERROR_OUTPUT = ChatColor.RED + "Your project \"{0}\" had an error:",
            NOT_PROJECT_OWNER = ChatColor.RED + "You are not the owner of this project";
    // endregion
    //region success
    public static String PROJECT_CREATE_SUCCESS = ChatColor.GREEN + "Project created successfully",
            RUN_PROJECT_SUCCESS = ChatColor.GREEN + "Ran project \"{0}\" successfully. Here is your output:",
            EDIT_SUCCESS = ChatColor.GREEN + "Edited file {0} successfully";
    //endregion

    public static String createMessage(String template, String... toInsert) {
        for (int i = 0; i < toInsert.length; i++) {
            template = template.replace("{" + i + "}", toInsert[i]);
        }
        return template;
    }
    //endregion
}
