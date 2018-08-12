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

import org.bukkit.GameMode;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("deprecation")
class InventoryManager {

    private File inventoriesFile;

    InventoryManager(LapisInventories p) {
        inventoriesFile = new File(p.getDataFolder() + File.separator + "Inventories");
        inventoriesFile.mkdir();
    }

    void saveInventory(Player p, GameMode gm) {
        //if they are in adventure mode we don't care
        if (gm.getValue() == 3) return;
        //get the players inventory
        Inventory inv = p.getInventory();
        File playerDataFile = new File(inventoriesFile + File.separator + p.getUniqueId() + ".yml");
        if (!playerDataFile.exists()) {
            try {
                playerDataFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        YamlConfiguration playerData = YamlConfiguration.loadConfiguration(playerDataFile);
        try {
            //get a List of the inventory items and set it in the config
            playerData.set(gm.getValue() + ".inventory", Arrays.asList(inv.getContents()));
            //get the current EXP level and set it in the config
            playerData.set(gm.getValue() + ".exp", p.getExp());
            //save the file to disk
            playerData.save(playerDataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        p.getInventory().clear();
    }

    void loadInventory(Player p, GameMode gm) {
        //we don't store spectator mode inventories so we cant load it either, so we don't even try
        if (gm.getValue() == 3) return;
        File playerDataFile = new File(inventoriesFile + File.separator + p.getUniqueId() + ".yml");
        if (!playerDataFile.exists()) {
            try {
                playerDataFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        YamlConfiguration playerData = YamlConfiguration.loadConfiguration(playerDataFile);
        //if we don't have an inventory to load simply clear the players inventory and set EXP to 0
        if (!playerData.contains(gm.getValue() + ".inventory")) {
            p.getInventory().clear();
            p.setExp(0.0f);
            return;
        }
        //load the inventory items as a list and then convert it to an array
        @SuppressWarnings("unchecked") List<ItemStack> items = (List<ItemStack>) playerData.get(gm.getValue() + ".inventory");
        ItemStack[] itemsArray = new ItemStack[items.size()];
        itemsArray = items.toArray(itemsArray);
        //set the player inventory with the array
        p.getInventory().setContents(itemsArray);
        //get the EXP double from the config and convert it to a float and set the players EXP
        Double exp = playerData.getDouble(gm.getValue() + ".exp");
        p.setExp(exp.floatValue());
    }

    boolean hasInventory(Player p, GameMode gm) {
        if (gm.getValue() == 3) return true;
        File playerDataFile = new File(inventoriesFile + File.separator + p.getUniqueId() + ".yml");
        if (!playerDataFile.exists()) {
            try {
                playerDataFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        YamlConfiguration playerData = YamlConfiguration.loadConfiguration(playerDataFile);
        return playerData.contains(gm.getValue() + ".inventory");
    }


}
