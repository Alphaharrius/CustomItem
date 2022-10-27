package dev.customitem.custom;

import javax.annotation.Nonnull;
import dev.customitem.util.HashSetTimer;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

/**
 * This class defines a type for all custom item handlers,
 * for the plugin to recognise the handler, a class must
 * extends this class.
 *
 * @author Alphaharrius
 */
public abstract class CustomItemHandler extends HashSetTimer {

    /**
     * This method will be called in {@link CustomItemManager#createCustomItemStack(Class)},
     * defines the default {@link ItemStack} of this custom item type.
     * @return {@link ItemStack}: A new item stack of this custom item type.
     */
    @Nonnull
    public abstract ItemStack createCustomItemStack();

    /**
     * <p>
     * This method will be called in {@link dev.customitem.util.LoreReader#createCustomLoreList(String)},
     * the object list returned from here will be used by all custom item stacks of this custom item type
     * as their custom lore data storage, please refer to {@link dev.customitem.util.LoreReader} for details.
     * </p><br>
     * <b>Please follow this format for the definition:</b><br>
     *     <p>{@code { KEY, VALUE, KEY, VALUE, ... }}</p><br>
     * @return {@link java.lang.Object}{@code []}: The custom defined lore array.
     */
    @Nonnull
    public abstract Object[] getCustomLore();

    /**
     * This method defines whether this custom item type is stackable.
     * @return {@link java.lang.Boolean}:   If set to {@code false}, a unique custom id will be
     *                                      added to the lore data storage, else none will be set.
     */
    public abstract boolean isItemStackable();

    /**
     * This method defines whether this custom item type is placeable as a block.
     * @return {@link java.lang.Boolean}:   If set to {@code false}, the item cannot
     *                                      be placed as a block, else it could.
     */
    public abstract boolean isItemPlaceable();

    public abstract void handlesUseItemAttackEvent(EntityDamageByEntityEvent event);

    public abstract void handlesPlayerLeftClickInteractEvent(PlayerInteractEvent event);

    public abstract void handlesPlayerRightClickInteractEvent(PlayerInteractEvent event);

    public abstract void handlesOnBlockPlacedEvent(BlockPlaceEvent event);

    public abstract void handlesEntityPickupItemEvent(EntityPickupItemEvent event);

}
