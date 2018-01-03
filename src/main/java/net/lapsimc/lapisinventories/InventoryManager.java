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

public class InventoryManager {

    private File inventoriesFile;

    InventoryManager(LapisInventories p) {
        inventoriesFile = new File(p.getDataFolder() + File.separator + "Inventories");
        inventoriesFile.mkdir();
    }

    public void saveInventory(Player p, GameMode gm) {
        //TODO: store EXP with inventories
        Inventory inv = p.getInventory();
        File playerdataFile = new File(inventoriesFile + File.separator + p.getUniqueId() + ".yml");
        if (!playerdataFile.exists()) {
            try {
                playerdataFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        YamlConfiguration playerdata = YamlConfiguration.loadConfiguration(playerdataFile);
        if (playerdata.contains(gm.getValue() + "")) {
            return;
        }
        try {
            playerdata.set(gm.getValue() + "", Arrays.asList(inv.getContents()));
            playerdata.save(playerdataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        p.getInventory().clear();
    }

    public void loadInventory(Player p, GameMode gm) {
        File playerdataFile = new File(inventoriesFile + File.separator + p.getUniqueId() + ".yml");
        if (!playerdataFile.exists()) {
            try {
                playerdataFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        YamlConfiguration playerdata = YamlConfiguration.loadConfiguration(playerdataFile);
        if (!playerdata.contains(gm.getValue() + "")) {
            return;
        }
        @SuppressWarnings("unchecked") List<ItemStack> items = (List<ItemStack>) playerdata.get(gm.getValue() + "");
        ItemStack[] itemsArray = new ItemStack[items.size()];
        itemsArray = items.toArray(itemsArray);
        p.getInventory().setContents(itemsArray);
        playerdata.set(gm.getValue() + "", null);
    }


}
