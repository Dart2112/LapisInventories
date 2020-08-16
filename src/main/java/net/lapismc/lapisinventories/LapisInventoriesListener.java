/*
 * Copyright 2020 Benjamin Martin
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

package net.lapismc.lapisinventories;

import net.lapismc.lapisinventories.playerdata.Permission;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.block.Container;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class LapisInventoriesListener implements Listener {

    private final LapisInventories plugin;

    public LapisInventoriesListener(LapisInventories plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    /*
    Process player quit
     */

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        plugin.getPlayer(e.getPlayer().getUniqueId()).savePlayer();
    }

    /*
    Processing gamemode changes
     */

    @EventHandler
    public void onGamemodeChange(PlayerGameModeChangeEvent e) {
        if (plugin.perms.isPermitted(e.getPlayer().getUniqueId(), Permission.InventoryControl.getPermission())) {
            plugin.getPlayer(e.getPlayer().getUniqueId()).processGamemodeChange(e.getNewGameMode());
        }
    }

    /*
    Process creative controls
     */

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent e) {
        //TODO: Add permissions checks here
        if (e.getPlayer().getGameMode().equals(GameMode.CREATIVE))
            e.setCancelled(true);
        //TODO: Send messages about what happened
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent e) {
        if (e.getInventory().getHolder() instanceof Container) {
            e.setCancelled(true);
            //TODO: Send messages about what happened
        }
    }

    /*
    Track block place/break
     */

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        if (e.getPlayer().getGameMode().equals(GameMode.CREATIVE)) {
            if (plugin.perms.isPermitted(e.getPlayer().getUniqueId(), Permission.TrackBlockPlacing.getPermission())) {
                plugin.blocksManager.trackBlock(e.getBlockPlaced(), e.getPlayer());
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        if (plugin.blocksManager.checkBlock(e.getBlock())) {
            //Block is creative
            e.setDropItems(false);
            plugin.blocksManager.unTrackBlock(e.getBlock());
        }
    }

}
