package dev.customitem.custom;

import dev.customitem.core.CurrentPlugin;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EntityEquipment;

public class CustomItemListener implements Listener {

    @EventHandler
    public void onUseItemAttack(EntityDamageByEntityEvent event) {
        Entity attacker = event.getDamager();
        if (attacker instanceof LivingEntity) {
            EntityEquipment equipment = ((LivingEntity) attacker).getEquipment();
            if (equipment == null) { return; }
            CustomItemHandler customItemHandler = CustomItemManager.getCustomItemHandler(equipment.getItemInMainHand());
            if (customItemHandler == null) { return; }
            customItemHandler.handlesUseItemAttackEvent(event);
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        CustomItemHandler customItemHandler = CustomItemManager.getCustomItemHandler(event.getItem());
        if (customItemHandler == null) { return; }
        switch (event.getAction()) {
            case LEFT_CLICK_AIR:
            case LEFT_CLICK_BLOCK:
                customItemHandler.handlesPlayerLeftClickInteractEvent(event);
                break;
            case RIGHT_CLICK_AIR:
            case RIGHT_CLICK_BLOCK:
                customItemHandler.handlesPlayerRightClickInteractEvent(event);
                break;
            default: break;
        }
    }

    @EventHandler
    public void onBlockPlaced(BlockPlaceEvent event) {
        CustomItemHandler customItemHandler = CustomItemManager.getCustomItemHandler(event.getItemInHand());
        if (customItemHandler == null) { return; }
        if (!customItemHandler.isItemPlaceable()) { event.setCancelled(true); }
    }

    @EventHandler
    public void onEntityPickupItem(EntityPickupItemEvent event) {}

}
