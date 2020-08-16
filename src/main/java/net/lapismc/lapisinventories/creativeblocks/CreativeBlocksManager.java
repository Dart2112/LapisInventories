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

package net.lapismc.lapisinventories.creativeblocks;

import net.lapismc.datastore.util.LapisURL;
import net.lapismc.datastore.util.URLBuilder;
import net.lapismc.lapiscore.utils.LocationUtils;
import net.lapismc.lapisinventories.LapisInventories;
import net.lapismc.lapisinventories.datastore.SQLiteDataStore;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.HashMap;
import java.util.List;

public class CreativeBlocksManager {

    private final HashMap<Location, CreativeBlock> creativeBlocks = new HashMap<>();
    private final SQLiteDataStore blocksDatabase;
    public int taskID;

    public CreativeBlocksManager(LapisInventories plugin) {
        LapisURL url = new URLBuilder().setLocation(plugin.getDataFolder() + File.separator + "CreativeBlocks")
                .setDatabase("BlocksData").build();
        blocksDatabase = new SQLiteDataStore(plugin, url);
        Bukkit.getScheduler().runTaskAsynchronously(plugin, this::loadBlocks);
        taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this::saveBlocks, 20 * 60 * 60, 20 * 60 * 60);
    }

    public void trackBlock(Block b, Player p) {
        creativeBlocks.put(b.getLocation(), new CreativeBlock(b, p.getUniqueId()));
    }

    public void unTrackBlock(Block b) {
        creativeBlocks.remove(b.getLocation());
    }

    public boolean checkBlock(Block b) {
        return creativeBlocks.containsKey(b.getLocation());
    }

    public void loadBlocks() {
        List<String> list = blocksDatabase.getEntireColumn(blocksDatabase.table, "Data");
        for (String s : list) {
            CreativeBlock block = new CreativeBlock(s);
            creativeBlocks.put(block.getLocation(), block);
        }
    }

    public void saveBlocks() {
        //Async tasks cannot be generated while the plugin is disabling
        //This is either called from an async thread or from on disable so we dont need to be async
        blocksDatabase.setAsync(false);
        //Wipe the table clean so that we dont have to worry about old, removed data
        blocksDatabase.removeAllData(blocksDatabase.table);
        //Loop over blocks and save them to the database
        for (CreativeBlock block : creativeBlocks.values()) {
            blocksDatabase.addData(blocksDatabase.table, "Location",
                    new LocationUtils().parseLocationToString(block.getLocation()),
                    new LocationUtils().parseLocationToString(block.getLocation()) + blocksDatabase.valueSeparator +
                            block.toString());
        }
        //Flip this back for any other operations
        blocksDatabase.setAsync(true);
    }

}
