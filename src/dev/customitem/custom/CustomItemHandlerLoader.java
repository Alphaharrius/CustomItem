package dev.customitem.custom;

import dev.customitem.core.CurrentPlugin;
import dev.customitem.util.ClassUtils;
import org.reflections.Reflections;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Set;

/**
 * This utility provides method to load {@link dev.customitem.custom.CustomItemHandler} classes.
 *
 * @author Alphaharrius
 */
public class CustomItemHandlerLoader {

    /**
     * This method loads all {@link dev.customitem.custom.CustomItemHandler}
     * class objects, and put their instances into the handler map with
     * class name as keys.
     * @param handlerMap {@link java.util.Map} The handler map which handler instances will be loaded.
     */
    public void loadCustomItemHandlers(Map<String, CustomItemHandler> handlerMap) {
        String packageName = this.getClass().getPackage().getName();
        Reflections reflections = new Reflections(packageName);
        Set<Class<? extends CustomItemHandler>> classSet = reflections.getSubTypesOf(CustomItemHandler.class);
        for (Class<? extends CustomItemHandler> classObject : classSet) {
            Constructor<?> constructor; try { constructor = classObject.getConstructor(); }
            catch (NoSuchMethodException e) {
                CurrentPlugin.warn(
                        this.getClass(),
                        String.format(
                                "skipping invalid handler { %s.class }. %s",
                                classObject.getName(),
                                e.getMessage()));
                continue;
            }
            Object instance; try { instance = constructor.newInstance(); }
            catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                CurrentPlugin.warn(
                        this.getClass(),
                        String.format(
                                "unable to instantiate handler { %s.class }. %s",
                                classObject.getName(),
                                e.getMessage()));
                continue;
            }
            handlerMap.put(ClassUtils.getClassName(classObject), (CustomItemHandler) instance);
            CurrentPlugin.info(
                    this.getClass(),
                    String.format("instantiated handler { %s.class }.", classObject.getName()));
        }
    }
}
