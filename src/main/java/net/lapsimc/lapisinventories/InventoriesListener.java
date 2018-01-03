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
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.*;

public class InventoriesListener implements Listener {

    private LapisInventories plugin;

    InventoriesListener(LapisInventories p) {
        plugin = p;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        if (plugin.api.getHooks().size() == 0) {
            plugin.invManager.loadInventory(e.getPlayer(), e.getPlayer().getGameMode());
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        plugin.invManager.saveInventory(e.getPlayer(), e.getPlayer().getGameMode());
    }

    @EventHandler
    public void onPlayeGamemodeChange(PlayerGameModeChangeEvent e) {
        plugin.invManager.saveInventory(e.getPlayer(), e.getPlayer().getGameMode());
        plugin.invManager.loadInventory(e.getPlayer(), e.getNewGameMode());
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent e) {
        if (e.getPlayer().getGameMode() == GameMode.CREATIVE && !e.getPlayer().hasPermission("LapisInventories")) {
            e.setCancelled(true);
            e.getPlayer().sendMessage(plugin.invConfigs.getColoredMessage("Denied.itemDrop"));
        }
    }

    @EventHandler
    public void onContainerOpen(PlayerInteractEvent e) {
        if (e.getClickedBlock() instanceof Container) {
            if (e.getPlayer().getGameMode() == GameMode.CREATIVE && !e.getPlayer().hasPermission("LapisInventories")) {
                e.setCancelled(true);
                e.getPlayer().sendMessage(plugin.invConfigs.getColoredMessage("Denied.containerAccess"));
            }
        }
        if (e.getItem().getType() == Material.BLAZE_ROD && e.getPlayer().hasPermission("CheckBlocks")) {
            if (plugin.blockLogger.checkBlock(e.getClickedBlock())) {
                e.getPlayer().sendMessage(plugin.invConfigs.getColoredMessage("BlockCheck.Positive").replace("%PLAYER%", Bukkit.getPlayer(plugin.blockLogger.checkPlacer(e.getClickedBlock())).getName()));
            } else {
                e.getPlayer().sendMessage(plugin.invConfigs.getColoredMessage("BlockCheck.Negative"));
            }
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        if (!e.getPlayer().hasPermission("BypassLogging") && e.getPlayer().getGameMode() == GameMode.CREATIVE) {
            plugin.blockLogger.addBlock(e.getBlock(), e.getPlayer().getUniqueId());
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        if (plugin.blockLogger.checkBlock(e.getBlock())) {
            e.setDropItems(false);
            plugin.blockLogger.removeBlock(e.getBlock());
        }
    }

}
