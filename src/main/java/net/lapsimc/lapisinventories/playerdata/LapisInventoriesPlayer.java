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

package net.lapsimc.lapisinventories.playerdata;

import net.lapsimc.lapisinventories.LapisInventories;
import net.lapsimc.lapisinventories.api.events.InventoryHideEvent;
import net.lapsimc.lapisinventories.api.events.InventoryLoadEvent;
import net.lapsimc.lapisinventories.api.events.InventoryRestoreEvent;
import net.lapsimc.lapisinventories.api.events.InventorySaveEvent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * This class processes all inventory based tasks performed on players
 */
public class LapisInventoriesPlayer {

    private final UUID uuid;
    private boolean isHidden;
    private File dataFile;
    private YamlConfiguration dataYaml;

    public LapisInventoriesPlayer(UUID uuid) {
        this.uuid = uuid;
        loadConfig();
    }

    /**
     * Handles the loading and saving of inventories during a gamemode change
     *
     * @param newGamemode The gamemode the player is changing to
     */
    public void processGamemodeChange(GameMode newGamemode) {
        //Make sure the player is online
        if (isOffline())
            return;
        //We don't need to do anything if the players inventory is hidden, restoring the inventory will deal with that
        if (isHidden)
            return;
        saveInventory();
        Bukkit.getScheduler().runTaskLater(LapisInventories.getInstance(), () -> {
            //Check that the player is still online
            if (isOffline())
                return;
            //Check that the players gamemode change was succsesful
            if (Bukkit.getPlayer(uuid).getGameMode().equals(newGamemode)) {
                //Load the inventory for the new gamemode
                loadInventory();
            }
        }, 1L);
    }

    /**
     * Saves the players current gamemode inventory to their config file
     */
    public void saveInventory() {
        //Check if the player is offline
        if (isOffline())
            return;
        //Don't save the inventory if it currently hidden
        if (isHidden)
            return;
        Player p = Bukkit.getPlayer(uuid);
        PlayerInventory inv = p.getInventory();
        dataYaml.set("inventories." + p.getGameMode().name(), inv.getContents());
        Bukkit.getPluginManager().callEvent(new InventorySaveEvent(p, inv, p.getGameMode()));
        try {
            saveConfig();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Replaces their current gamemode inventory with the one stored in their config
     */
    public void loadInventory() {
        //Check if the player is offline
        if (isOffline())
            return;
        Player p = Bukkit.getPlayer(uuid);
        //Check that we have an inventory saved
        if (!dataYaml.contains("inventories." + p.getGameMode().name())) {
            return;
        }
        //Clear the players inventory
        PlayerInventory inv = p.getInventory();
        inv.clear();
        //Load in the saved one
        inv.addItem(parseItems(p.getGameMode()).toArray(new ItemStack[0]));
        Bukkit.getPluginManager().callEvent(new InventoryLoadEvent(p, inv, p.getGameMode()));
    }

    /**
     * Check if the players inventory is currently hidden
     *
     * @return true if inventory hidden, otherwise false
     */
    public boolean getIsHidden() {
        return isHidden;
    }

    /**
     * Hide the players inventory, this will save their inventory to config and then clear it
     * use restoreInventory to bring back the players inventory
     */
    public void hideInventory() {
        //Make sure the player is online
        if (isOffline())
            return;
        //Don't hide the inventory if it is already hidden, this would just overwrite the saved inventory
        if (isHidden)
            return;
        //Save and then clear the inventory
        saveInventory();
        Player p = Bukkit.getPlayer(uuid);
        PlayerInventory inv = p.getInventory();
        Bukkit.getPluginManager().callEvent(new InventoryHideEvent(p, inv));
        inv.clear();
        isHidden = true;
    }

    /**
     * Restore the players inventory that was saved in hideInventory
     */
    public void restoreInventory() {
        //Make sure the player is online
        if (isOffline())
            return;
        //Don't show the inventory if it is not already hidden, this would just overwrite the players inventory
        if (!isHidden)
            return;
        //Restore the players saved inventory
        loadInventory();
        Player p = Bukkit.getPlayer(uuid);
        Bukkit.getPluginManager().callEvent(new InventoryRestoreEvent(p, p.getInventory()));
        isHidden = false;
    }

    /**
     * Save all player data, this is used when the player leaves the server
     */
    public void savePlayer() {
        saveInventory();
        try {
            saveConfig();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadConfig() {
        dataFile = new File(LapisInventories.getInstance().getDataFolder() + File.separator + "PlayerData", uuid + ".yml");
        //Make sure the data file exists, create it if it doesn't
        if (!dataFile.exists()) {
            try {
                dataFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        dataYaml = YamlConfiguration.loadConfiguration(dataFile);
    }

    private void saveConfig() throws IOException {
        if (!dataFile.exists()) {
            dataFile.createNewFile();
        }
        dataYaml.save(dataFile);
    }

    private List<ItemStack> parseItems(GameMode gm) {
        List<ItemStack> items = new ArrayList<>();
        //Load the list from the config, we need to confirm that the data isn't malformed
        List<?> data = dataYaml.getList("inventories." + gm.name());
        for (Object item : data) {
            //Check that each item is in fact an ItemStack before adding it to the list
            if (item instanceof ItemStack) {
                items.add((ItemStack) item);
            }
        }
        return items;
    }

    private boolean isOffline() {
        return !Bukkit.getOfflinePlayer(uuid).isOnline();
    }

}
