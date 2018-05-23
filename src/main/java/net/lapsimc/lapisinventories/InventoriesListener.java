/*
 * Copyright 2018 Benjamin Martin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.lapsimc.lapisinventories;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Container;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;

public class InventoriesListener implements Listener {

    private LapisInventories plugin;

    InventoriesListener(LapisInventories p) {
        plugin = p;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        //if there is a login plugin registered we wont give the player their inventory yet
        if (plugin.api.getHooks().size() == 0) {
            plugin.invManager.loadInventory(e.getPlayer(), e.getPlayer().getGameMode());
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        //we save the players inventory when they quit to make sure that any gamemode changes
        //when they join again wont glitch items between gamemodes
        plugin.invManager.saveInventory(e.getPlayer(), e.getPlayer().getGameMode());
    }

    @EventHandler
    public void onPlayerGamemodeChange(PlayerGameModeChangeEvent e) {
        //When a player changes game mode we save their current inventory for their current gamemode
        //Then we load their inventory for their new gamemode
        plugin.invManager.saveInventory(e.getPlayer(), e.getPlayer().getGameMode());
        plugin.invManager.loadInventory(e.getPlayer(), e.getNewGameMode());
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent e) {
        //If the player is in creative and isn't explicitly allowed to drop items, we will cancel the event and send them a message
        if (e.getPlayer().getGameMode() == GameMode.CREATIVE && !e.getPlayer().hasPermission("LapisInventories.canDrop")) {
            e.setCancelled(true);
            e.getPlayer().sendMessage(plugin.invConfigs.getColoredMessage("Denied.itemDrop"));
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        //if the player right clicks a container we want to check if they are in creative and are permitted to do that
        //if they are in creative but not permitted, then we cancel the event and send them a messsage
        if (e.getClickedBlock().getState() instanceof Container && e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            if (e.getPlayer().getGameMode() == GameMode.CREATIVE && !e.getPlayer().hasPermission("LapisInventories.containerAccess")) {
                e.setCancelled(true);
                e.getPlayer().sendMessage(plugin.invConfigs.getColoredMessage("Denied.containerAccess"));
            }
        }
        //if the player is using a blaze rod to inspect a block
        if (canInspect(e.getPlayer(), e.getItem())) {
            //check if the block was placed in creative
            if (plugin.blockLogger.checkBlock(e.getClickedBlock())) {
                //if it was we send a message with the players name inserted
                if (plugin.blockLogger.checkPlacer(e.getClickedBlock()) == plugin.blockLogger.importedUUID) {
                    e.getPlayer().sendMessage(plugin.invConfigs.getColoredMessage("BlockCheck.Positive").replace("%PLAYER%", "ImportedData"));
                } else {
                    e.getPlayer().sendMessage(plugin.invConfigs.getColoredMessage("BlockCheck.Positive").replace("%PLAYER%", Bukkit.getPlayer(plugin.blockLogger.checkPlacer(e.getClickedBlock())).getName()));
                }
            } else {
                //otherwise we send a negative response
                e.getPlayer().sendMessage(plugin.invConfigs.getColoredMessage("BlockCheck.Negative"));
            }
        }
    }

    private boolean canInspect(Player p, ItemStack item) {
        //checks if the player is in inspect mode while holding a blaze rod with tracking enabled and they are permitted
        return plugin.inspectingPlayers.contains(p.getUniqueId())
                && item != null && item.getType() == Material.BLAZE_ROD
                && plugin.getConfig().getBoolean("CreativeBlockTracking")
                && p.hasPermission("LapisInventories.checkBlocks");
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        //if the player is in creative and isn't excluded from tracking
        if (!e.getPlayer().hasPermission("LapisInventories.bypassLogging")
                && plugin.getConfig().getBoolean("CreativeBlockTracking") && e.getPlayer().getGameMode() == GameMode.CREATIVE) {
            //add the block to the list
            plugin.blockLogger.addBlock(e.getBlock(), e.getPlayer().getUniqueId());
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        //check that we have database settings and that we are tracking this
        if (plugin.getConfig().getBoolean("CreativeBlockTracking")) {
            //if the block was placed in creative, don't drop it and remove it from the system
            if (plugin.blockLogger.checkBlock(e.getBlock())) {
                e.setDropItems(false);
                plugin.blockLogger.removeBlock(e.getBlock());
            }
        }
    }

}
