package dev.customitem.custom;

import dev.customitem.core.CurrentPlugin;
import dev.customitem.util.ClassUtils;
import dev.customitem.util.LoreReader;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.security.spec.InvalidParameterSpecException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomItemManager {

    private static final Map<String, CustomItemHandler> HANDLER_MAP = new HashMap<>();

    public static boolean isCustomItemStack(@Nonnull ItemStack itemStack) {
        List<String> loreList = LoreReader.getLoreFromItemStack(itemStack);
        if (loreList != null) {
            return LoreReader.containsKey(loreList, LoreReader.CUSTOM_ITEM_CLASS_NAME_KEY);
        }
        return false;
    }

    public static void loadCustomItemHandlers() {
        new CustomItemHandlerLoader().loadCustomItemHandlers(HANDLER_MAP);
    }

    @Nullable
    public static CustomItemHandler getCustomItemHandler(@Nonnull String className) {
        if (HANDLER_MAP.containsKey(className)) { return HANDLER_MAP.get(className); }
        return null;
    }

    @Nullable
    public static CustomItemHandler getCustomItemHandler(@Nullable ItemStack itemStack) {
        if (itemStack == null || !CustomItemManager.isCustomItemStack(itemStack)) { return null; }
        String handlerClassName = LoreReader.getCustomItemClassName(itemStack);
        if (handlerClassName == null) { return null; }
        return CustomItemManager.getCustomItemHandler(handlerClassName);
    }

    @Nullable
    public static ItemStack createCustomItemStack(@Nonnull Class<? extends CustomItemHandler> classObject) {
        String className = ClassUtils.getClassName(classObject);
        if (!HANDLER_MAP.containsKey(className)) { return null; }
        CustomItemHandler customItemHandler = HANDLER_MAP.get(className);
        ItemStack itemStack = customItemHandler.createCustomItemStack();
        List<String> loreList = null;
        try { loreList = LoreReader.createCustomLoreList(className); }
        catch (InvalidParameterSpecException e) {

            CurrentPlugin.warn(CustomItemManager.class, String.format("failed to initialize custom lore for { %s.class }. %s", className, e.getMessage()));
        }
        LoreReader.setLoreForItemStack(itemStack, loreList);
        return itemStack;
    }

}
