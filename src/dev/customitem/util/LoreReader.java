package dev.customitem.util;

import dev.customitem.custom.CustomItemHandler;
import dev.customitem.custom.CustomItemManager;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.security.spec.InvalidParameterSpecException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * The custom items from this plugin made use of the lore context - {@link org.bukkit.inventory.meta.ItemMeta#getLore()} to store custom properties.
 * The storage method is described as an {@link java.util.ArrayList} which keys and values are stored in sequence, { KEY, VALUE, KEY, VALUE }.
 * For the content to be better displayed, a single ':' character is automatically added to the end of each key.
 *
 * THis utility provides methods to create, lookup and update the custom lore data storage.
 *
 * @author Alphaharrius
 */
public class LoreReader {

    public static final String PROPERTY_KEY_ENDING = ":";

    /**
     * Each custom {@link org.bukkit.inventory.ItemStack} is not known to the plugin before it have been interacted,
     * this key stores the class name of each {@link dev.customitem.custom.CustomItemHandler} child instances for custom behaviors.
     * This key will also be used to determine whether the {@link org.bukkit.inventory.ItemStack} is a custom item stack.
     */
    public static final String CUSTOM_ITEM_CLASS_NAME_KEY = "Custom Item Class";
    /**
     * {@link org.bukkit.inventory.ItemStack} with the same nature cannot be distinguished as they are the same in their serialized form,
     * This key references a custom id which is unique to each custom item stack, thus making them distinguishable.
     */
    public static final String CUSTOM_ITEM_ID_KEY = "Custom Item Id";

    /**
     * This method wraps the lore retrieval process from the item stack.
     * @param itemStack {@link org.bukkit.inventory.ItemStack}: The item stack which lore to be retrieved from.
     * @return          {@link java.util.List}: The lore of the item stack, returns {@code null} if item stack does not have item meta or lore.
     */
    @Nullable
    public static List<String> getLoreFromItemStack(@Nonnull ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) { return null; }
        return itemMeta.getLore();
    }

    public static void setLoreForItemStack(@Nonnull ItemStack itemStack, @Nullable List<String> loreList) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) { return; }
        itemMeta.setLore(loreList);
        itemStack.setItemMeta(itemMeta);
    }

    @Nullable
    public static String getCustomItemId(@Nonnull ItemStack itemStack) {
        List<String> loreList = getLoreFromItemStack(itemStack);
        if (loreList == null) { return null; }
        return getStringProperty(loreList, CUSTOM_ITEM_ID_KEY);
    }

    @Nullable
    public static String getCustomItemClassName(@Nonnull ItemStack itemStack) {
        List<String> loreList = getLoreFromItemStack(itemStack);
        if (loreList == null) { return null; }
        return getStringProperty(loreList, CUSTOM_ITEM_CLASS_NAME_KEY);
    }

    @Nonnull
    public static List<String> createCustomLoreList(@Nonnull String className) throws InvalidParameterSpecException {
        CustomItemHandler customItemHandler = CustomItemManager.getCustomItemHandler(className);
        assert customItemHandler != null;
        Object[] loreArray = customItemHandler.getCustomLore();
        if (loreArray.length % 2 != 0) {
            throw new InvalidParameterSpecException("invalid custom lore list is provided, list size must be even number. provided=" + loreArray.length);
        }
        List<String> loreList = new ArrayList<>();
        loreList.add(CUSTOM_ITEM_CLASS_NAME_KEY + PROPERTY_KEY_ENDING);
        loreList.add(className);
        if (!customItemHandler.isItemStackable()) {
            loreList.add(CUSTOM_ITEM_ID_KEY + PROPERTY_KEY_ENDING);
            loreList.add(Long.toHexString(System.currentTimeMillis()));
        }
        for (int i = 0; i < loreArray.length; i += 2) {
            loreList.add(loreArray[i] + PROPERTY_KEY_ENDING);
            loreList.add(Objects.toString(loreArray[i + 1]));
        }
        return loreList;
    }

    public static boolean containsKey(@Nonnull List<String> loreList, @Nonnull String key) {
        return loreList.contains(key + PROPERTY_KEY_ENDING);
    }

    @Nullable
    public static String getStringProperty(@Nonnull List<String> loreList, @Nonnull String key) {
        int keyIndex = loreList.indexOf(key + PROPERTY_KEY_ENDING);
        if (keyIndex >= 0 && loreList.size() > keyIndex + 1) {
            return loreList.get(keyIndex + 1);
        }
        return null;
    }

    public static int getIntegerProperty(@Nonnull List<String> loreList, @Nonnull String key) {
        String stringValue = getStringProperty(loreList, key);
        return stringValue != null ? Integer.parseInt(stringValue) : 0;
    }

    public static boolean getBooleanProperty(@Nonnull List<String> loreList, @Nonnull String key) {
        String stringValue = getStringProperty(loreList, key);
        return stringValue != null && Boolean.getBoolean(stringValue);
    }

    public static void setProperty(@Nonnull List<String> loreList, @Nonnull String key, @Nonnull Object value) {
        int keyIndex = loreList.indexOf(key + PROPERTY_KEY_ENDING);
        String stringValue = value instanceof String ? (String) value : String.valueOf(value);
        if (keyIndex < 0) {
            loreList.add(key + PROPERTY_KEY_ENDING);
            loreList.add(stringValue);
        } else {
            loreList.set(keyIndex + 1, stringValue);
        }
    }

}
