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
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class InventoriesListener implements Listener {

    private LapisInventories plugin;

    public InventoriesListener(LapisInventories p) {
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

    //TODO: log place and break of blocks and attempted dropping of items and container access
    @EventHandler
    public void onItemDrop(PlayerDropItemEvent e){
        if(e.getPlayer().getGameMode() == GameMode.CREATIVE && !e.getPlayer().hasPermission("LapisInventories")){
            e.setCancelled(true);
            e.getPlayer().sendMessage(plugin.invConfigs.getColoredMessage("Denied.itemDrop"));
        }
    }

}
