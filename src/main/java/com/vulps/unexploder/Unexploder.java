package com.vulps.unexploder;

import com.vulps.unexploder.handlers.CommandHandler;
import com.vulps.unexploder.handlers.ExplosionHandler;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public final class Unexploder extends JavaPlugin {
    public HashMap<Location, Material> restoreList;
    private int taskId;
    @Override
    public void onEnable() {
        log("==================== [Unexploder] ====================");
        saveDefaultConfig();
        restoreList = new HashMap<>();

        // Schedule the restoration task to run every x seconds
        long interval = 5l; // Number of ticks (20 ticks = 1 second)
        new Restorer().runTaskTimer(this, interval, interval);

        // Plugin startup logic
        new ExplosionHandler(this);
        getCommand("unexploder").setExecutor(new CommandHandler());
        log("======================================================");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
   }

    public static void log(String message){
        if(message.equals("")) return;
        Bukkit.getLogger().info("[Unexploder] " + message);
    }

    public static void warn(String message){
        if(message.equals("")) return;
        Bukkit.getLogger().warning("[Unexploder] " + message);
    }

   private class Restorer extends BukkitRunnable {
       public void run() {
           if (restoreList.isEmpty()) return;

           Location originalLocation = restoreList.keySet().iterator().next();
           World world = originalLocation.getWorld();
           Block originalBlock = world.getBlockAt(originalLocation);
           Boolean isStable = isBlockStable(originalBlock, restoreList.get(originalLocation));
           Boolean shouldKeepSearching = true;

           //set the default block and location to the originals
           Location location = originalLocation;
           Block block = originalBlock;

           while(!isStable && shouldKeepSearching){ //if the original block was not stable, run this until it is
               location = block.getRelative(BlockFace.DOWN).getLocation(); //get the block below the previous iteration
               if(restoreList.containsKey(location)) {//if the block needs to be restored
                   block = world.getBlockAt(location);//get the block
                   isStable = isBlockStable(block, restoreList.get(location));//make sure block is stable
               }else{ //the current block is below the explosion. Restore the original and let physics do the rest
                   block = originalBlock;
                   location = originalLocation;
                   shouldKeepSearching = false;
               }
           }
           //restore the block
           Material material = restoreList.get(location);
           block.setType(material);

           //block has been restored. remove it
           restoreList.remove(location);
       }

       private boolean isBlockStable(Block block, Material type) {
           if(type == Material.SAND || type == Material.GRAVEL){ //block may fall.
               Block blockBelow = block.getRelative(BlockFace.DOWN);
               if(blockBelow.getType() == Material.AIR || blockBelow.getType() == Material.WATER || blockBelow.getType() == Material.LAVA) { //block is not stable
                   return false;
               }
           }
           return true;
       }
   }

}
