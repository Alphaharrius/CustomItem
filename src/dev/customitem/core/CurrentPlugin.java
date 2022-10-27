package dev.customitem.core;

import javax.annotation.Nonnull;
import dev.customitem.command.CommandLoader;
import dev.customitem.custom.CustomItemListener;
import dev.customitem.custom.CustomItemManager;
import dev.customitem.util.ClassUtils;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nullable;
import java.io.InvalidClassException;

/**
 * <p>
 * The main class of the CustomItem plugin for Spigot Server.<br>
 * Provides the necessary methods for the current plugin development.
 * </p>
 * @author Alphaharrius
 */
public class CurrentPlugin extends JavaPlugin {

    private static JavaPlugin currentPlugin;

    /**
     * @return {@link org.bukkit.plugin.java.JavaPlugin}: The current plugin instance.
     */
    @Nonnull
    public static JavaPlugin getCurrentPlugin() { return currentPlugin; }

    /**
     * Log a message to the server console as type "info", in format of "[%PLUGIN_NAME%] %MESSAGE.
     * @param message {@link java.lang.String} The message to be logged.
     */
    public static void info(@Nullable Class<?> callerClass, @Nonnull String message) {
        String header = currentPlugin.getName();
        if (callerClass != null) {
            header += "." + ClassUtils.getClassName(callerClass);
        }
        currentPlugin.getServer().getLogger().info(String.format("[%s] %s", header, message));
    }

    /**
     * Log a message to the server console as type "warn", in format of "[%PLUGIN_NAME%] %MESSAGE.
     * @param message {@link java.lang.String} The message to be logged.
     */
    public static void warn(@Nullable Class<?> callerClass, @Nonnull String message) {
        String header = currentPlugin.getName();
        if (callerClass != null) {
            header += "." + ClassUtils.getClassName(callerClass);
        }
        currentPlugin.getServer().getLogger().warning(String.format("[%s] %s", header, message));
    }

    @Override
    public void onEnable() {
        currentPlugin = this;
        // Load all command executors.
        CommandLoader commandLoader = new CommandLoader();
        commandLoader.loadCommandExecutors();
        try { commandLoader.registerCommandExecutors(); }
        catch (InvalidClassException e) { warn(this.getClass(), e.getMessage()); }
        // Register custom item listener to server event listener list.
        this.getServer().getPluginManager().registerEvents(new CustomItemListener(), this);
        CustomItemManager.loadCustomItemHandlers();
    }

    @Override
    public void onDisable() {}

}
