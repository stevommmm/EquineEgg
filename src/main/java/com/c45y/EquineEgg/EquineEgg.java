package com.c45y.EquineEgg;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Horse.Color;
import org.bukkit.entity.Horse.Style;
import org.bukkit.entity.Horse.Variant;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

public class EquineEgg extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        this.getConfig().options().copyDefaults(true);
        this.getConfig().addDefault("test", 60);
        this.saveConfig();
        this.getServer().getPluginManager().registerEvents(this, this);

        System.out.println(getServer().getVersion());
    }

    @Override
    public void onDisable() {

    }

    private int getInventorySpace(Player player) {
        int free = 0;
        for (ItemStack i : player.getInventory().getContents()) {
            if (i == null) {
                free++;
            }
        }
        return free;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        if (!event.getPlayer().isSneaking() || event.getRightClicked().getType() != EntityType.HORSE) {
            return;
        }
        Horse horse = (Horse) event.getRightClicked();
        if (horse.getPassenger() != null) {
            return;
        }
        if (getInventorySpace(event.getPlayer()) == 0) {
            event.getPlayer().sendMessage("Your inventory is full, please make some space and try again.");
            return;
        }
        
        // Handle horse interaction, cancel the event to stop defaults
        event.setCancelled(true);

        ItemStack egg = new ItemStack(Material.MONSTER_EGG, 1, (short) 100);
        ItemMeta meta = egg.getItemMeta();
        
        // Assign all our horse metadata as lore strings, makes the stats viewable within the egg
        List<String> lore = new ArrayList<String>();
        lore.add("Jump: " + horse.getJumpStrength());
        lore.add("Speed: " + new SpeedUtil(horse).getSpeed());
        lore.add("Variant: " + horse.getVariant().name());
        lore.add("Color: " + horse.getColor().name());
        lore.add("Style: " + horse.getStyle().name());
        meta.setLore(lore);
        egg.setItemMeta(meta);
        
        // Remove the 'egged' horse from the world
        horse.remove();
        event.getPlayer().getInventory().addItem(egg);
    }
     @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK || event.getMaterial() != Material.MONSTER_EGG) {
            return;
        }
        
        ItemStack egg = event.getItem();
        ItemMeta meta = egg.getItemMeta();
        
        // If it is a default spawn egg then just let it go
        if (!meta.hasLore()) {
            return;
        }
        
        // Handle monster eggs containing lore ( can players set this somehow? )
        event.setCancelled(true);
        
        Location location = event.getClickedBlock().getLocation();
        location.setY(location.getY() + 1);
        World world = event.getClickedBlock().getWorld();
        Horse horse = (Horse) world.spawnEntity(location, EntityType.HORSE);
        
        for (String lore : meta.getLore()) {
            if (lore.startsWith("Jump:")) {
                horse.setJumpStrength(Double.valueOf(lore.split(": ")[1]));
            }
            else if (lore.startsWith("Speed:")) {
                new SpeedUtil(horse).setSpeed(Double.valueOf(lore.split(": ")[1]));
            }
            else if (lore.startsWith("Variant:")) {
                horse.setVariant(Variant.valueOf(lore.split(": ")[1]));
            }
            else if (lore.startsWith("Color:")) {
                horse.setColor(Color.valueOf(lore.split(": ")[1]));
            }
            else if (lore.startsWith("Style:")) {
                horse.setStyle(Style.valueOf(lore.split(": ")[1]));
            }
        }
        
        // Remove the egg from the player inventory
        event.getPlayer().getInventory().remove(event.getItem());
                
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("ee")) {
            return true;
        }
        return false;
    }
}
