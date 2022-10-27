package dev.customitem.command;

import dev.customitem.core.CurrentPlugin;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;
import org.reflections.Reflections;

import java.io.InvalidClassException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Set;

/**
 * <p>
 * This utility is used to load all child classes of
 * {@link org.bukkit.command.CommandExecutor} from the
 * same package, which ease the need to register each
 * individual command in the main instance.
 * </p>
 *
 * @author Alphaharrius
 */
public class CommandLoader {

    private static final String COMMAND_PROPERTY = "COMMAND";

    Set<Class<? extends CommandExecutor>> commandExecutorsSet;

    /**
     * <p>
     * This method utilizes the Reflections library - {@link org.reflections.Reflections}
     * to load all sub types of {@link org.bukkit.command.CommandExecutor} within the
     * package of the loader.
     * </p>
     */
    public void loadCommandExecutors() {

        String packageName = this.getClass().getPackage().getName();

        Reflections reflections = new Reflections(packageName);

        commandExecutorsSet = reflections.getSubTypesOf(CommandExecutor.class);
    }

    public void registerCommandExecutors() throws InvalidClassException {

        if (commandExecutorsSet.isEmpty()) { return; }

        JavaPlugin currentPlugin = CurrentPlugin.getCurrentPlugin();

        for (Class<? extends CommandExecutor> classObject : commandExecutorsSet) {

            Field commandField; try { commandField = classObject.getField(COMMAND_PROPERTY); }
            catch (NoSuchFieldException e) {

                throw new InvalidClassException(
                        classObject.getName(),
                        String.format("\"%s\" is not defined. %s", COMMAND_PROPERTY, e.getMessage())
                );
            }

            if (commandField.getType() == String.class) {

                String command; try { command = (String) commandField.get(null); }
                catch (IllegalAccessException e) {

                    throw new InvalidClassException(
                            classObject.getName(),
                            String.format("\"%s\" is not accessible. %s", COMMAND_PROPERTY, e.getMessage())
                    );
                }

                PluginCommand pluginCommand = currentPlugin.getCommand(command);
                if (pluginCommand == null) { continue; }

                Object instanceObject;
                try {

                    Constructor<?> classConstructor = classObject.getConstructor();
                    instanceObject = classConstructor.newInstance();

                } catch (Exception e) {

                    throw new InvalidClassException(
                            classObject.getName(),
                            String.format("{ %s.class } cannot be instantiated. %s", classObject.getName(), e.getMessage())
                    );
                };

                pluginCommand.setExecutor((CommandExecutor) instanceObject);

            } else {

                throw new InvalidClassException(
                        classObject.getName(),
                        String.format("\"%s\" is not type of { String.class }.", COMMAND_PROPERTY)
                );
            }
        }
    }

}
