package com.vulps.unexploder.handlers;

import com.vulps.unexploder.Unexploder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.vulps.unexploder.Unexploder.log;

public class ExplosionHandler implements Listener {

    private Unexploder plugin;
    private Boolean restore_tnt;
    private Boolean restore_beds;
    private Boolean restore_end_crystals;
    private List<String> mobList;

    public ExplosionHandler(Unexploder plugin){
        Bukkit.getPluginManager().registerEvents(this, plugin);
        this.plugin = plugin;
        restore_tnt = plugin.getConfig().getBoolean("restore_tnt");
        restore_beds = plugin.getConfig().getBoolean("restore_bed");
        restore_end_crystals = plugin.getConfig().getBoolean("restore_end_crystal");
        mobList = plugin.getConfig().getStringList("restore_mobs");

        Unexploder.log("Restore TNT: " + restore_tnt);
        Unexploder.log("Restore Beds: " + restore_beds);
        Unexploder.log("Restore End Crystals: " + restore_end_crystals);
        Unexploder.log("Restoring explosions from the following sources: ");
        for (String mob : mobList) {
            Unexploder.log("  - " + mob);
        }

    }

    @EventHandler
    public void onEntityWillExplode(EntityExplodeEvent event){

        String type = event.getEntityType().name();
        Unexploder.log(type);
        if(shouldRegenerate(type)) {

            List<Block> affectedBlocks = event.blockList();
            for (Block block : affectedBlocks) {
                Location location = block.getLocation();
                plugin.restoreList.put(location, block.getType());
                block.setType(Material.AIR);
            }

            event.blockList().clear();
        }
    }
    @EventHandler
    public void onBlockWillExplode(BlockExplodeEvent event){

        String type = event.getBlock().getType().name();
        Unexploder.log(type);
        if(shouldRegenerate(type)) {

            List<Block> affectedBlocks = event.blockList();
            for (Block block : affectedBlocks) {
                Location location = block.getLocation();
                plugin.restoreList.put(location, block.getType());
                block.setType(Material.AIR);
            }

            event.blockList().clear();
        }
    }

    private Boolean shouldRegenerate(String type){

        switch (type) {
            case "AIR": //assume this is a bed
                return restore_beds;
            case "ENDER_CRYSTAL":
                return restore_end_crystals;
            case "PRIMED_TNT":
                return restore_tnt;
            default:
                return mobList.contains(type); //if the type is in the moblist, restore it
        }



    }

}
