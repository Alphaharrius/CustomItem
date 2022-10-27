package dev.customitem.custom;

import dev.customitem.util.ItemConsumer;
import dev.customitem.util.LoreReader;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import java.util.List;

public class ArrowGun extends CustomItemHandler {

    public static final String DISPLAY_NAME = "Arrow Gun";
    public static final String CAPACITY_KEY = "Capacity";

    public static final Object[] CUSTOM_LORE = { CAPACITY_KEY, 0 };

    @Override
    @Nonnull
    public ItemStack createCustomItemStack() {
        ItemStack itemStack = new ItemStack(Material.DISPENSER, 1);
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta != null) {
            itemMeta.setDisplayName(DISPLAY_NAME);
        }
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    @Override
    @Nonnull
    public Object[] getCustomLore() { return CUSTOM_LORE; }

    @Override
    public boolean isItemStackable() { return false; }

    @Override
    public boolean isItemPlaceable() { return false; }

    @Override
    public void handlesUseItemAttackEvent(EntityDamageByEntityEvent event) {

    }

    @Override
    public void handlesPlayerLeftClickInteractEvent(PlayerInteractEvent event) {
        ItemStack itemStack = event.getItem();
        if (itemStack == null) { return; }
        List<String> loreList = LoreReader.getLoreFromItemStack(itemStack);
        if (loreList == null) { return; }
        Player player = event.getPlayer();
        World world = player.getWorld();
        int capacity = LoreReader.getIntegerProperty(loreList, CAPACITY_KEY);
        if (capacity <= 0) {
            world.playSound(player.getLocation(), Sound.BLOCK_DISPENSER_FAIL, 1.0f, 1.0f);
            return;
        }
        String customItemId = LoreReader.getStringProperty(loreList, LoreReader.CUSTOM_ITEM_ID_KEY);
        if (customItemId == null) { return; }
        Runnable coolDownCallbackRunnable = () -> world.playSound(player.getLocation(), Sound.BLOCK_PISTON_EXTEND, 3.0f, 3.0f);
        BukkitTask coolDownTask = this.startCountdown(customItemId, 20, coolDownCallbackRunnable);
        if (coolDownTask == null) { return; }
        world.playSound(player.getLocation(), Sound.ENTITY_DRAGON_FIREBALL_EXPLODE, 1.0f, 1.0f);
        world.playEffect(player.getLocation().add(0.0f, 1.0f, 0.0f), Effect.SMOKE, 5);
        Arrow arrow = player.launchProjectile(Arrow.class);
        arrow.setTicksLived(100);
        arrow.setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);
        float randomOffset = (float) (0.05 * Math.exp(0.8 * player.getVelocity().length()));
        Vector randomVector = new Vector(Math.random() - 0.5, Math.random() - 0.5, Math.random() - 0.5).normalize().multiply(0.0005 + randomOffset);
        arrow.setVelocity(arrow.getVelocity().add(randomVector).multiply(2.0));
        LoreReader.setProperty(loreList, CAPACITY_KEY, capacity - 1);
        LoreReader.setLoreForItemStack(itemStack, loreList);
    }

    @Override
    public void handlesPlayerRightClickInteractEvent(PlayerInteractEvent event) {
        ItemStack itemStack = event.getItem();
        if (itemStack == null) { return; }
        List<String> loreList = LoreReader.getLoreFromItemStack(itemStack);
        if (loreList == null) { return; }
        Player player = event.getPlayer();
        World world = player.getWorld();
        int capacity = LoreReader.getIntegerProperty(loreList, CAPACITY_KEY);
        if (capacity != 0) { return; }
        ItemConsumer itemConsumer = new ItemConsumer(player.getInventory(), Material.ARROW, 10, Material.GUNPOWDER, 20);
        if (!itemConsumer.isConsumable()) { return; }
        world.playSound(player.getLocation(), Sound.BLOCK_GRASS_BREAK, 5.0f, 5.0f);
        itemConsumer.consume();
        LoreReader.setProperty(loreList, CAPACITY_KEY, 10);
        LoreReader.setLoreForItemStack(itemStack, loreList);
    }

    @Override
    public void handlesOnBlockPlacedEvent(BlockPlaceEvent event) {}

    @Override
    public void handlesEntityPickupItemEvent(EntityPickupItemEvent event) {}

}
