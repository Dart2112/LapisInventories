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

package net.lapsimc.lapisinventories.importers;

import me.eccentric_nz.gamemodeinventories.GameModeInventories;
import net.lapsimc.lapisinventories.LapisInventories;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class GameModeInventoriesHook {

    private LapisInventories plugin;

    public GameModeInventoriesHook(LapisInventories p) {
        plugin = p;
        if (Bukkit.getPluginManager().isPluginEnabled("GameModeInventories")) {
            retrieveData();
        }
    }

    private void retrieveData() {
        GameModeInventories gmi = (GameModeInventories) Bukkit.getPluginManager().getPlugin("GameModeInventories");
        Collection<List<String>> gmiLocations = gmi.getCreativeBlocks().values();
        List<Location> locations = new ArrayList<>();
        for (List<String> list : gmiLocations) {
            for (String s : list) {
                //"Location{" + "world=" + world + ",x=" + x + ",y=" + y + ",z=" + z + ",pitch=" + pitch + ",yaw=" + yaw + '}'
                String locFormatted = s.replace("Location{", "").replace("world=", "").replace("x=", "").replace("y=", "").replace("z=", "").replace("pitch=", "").replace("yaw=", "").replace("}", "");
                String[] locArray = locFormatted.split(",");
                if (Bukkit.getWorld(locArray[0]) != null) {
                    //if the world exists
                    Location loc = new Location(Bukkit.getWorld(locArray[0]), Double.valueOf(locArray[1]),
                            Double.valueOf(locArray[2]), Double.valueOf(locArray[3]),
                            Float.valueOf(locArray[4]), Float.valueOf(locArray[5]));
                    locations.add(loc);
                }
            }
        }
        for (Location l : locations) {
            plugin.blockLogger.addBlock(l.getBlock(), plugin.blockLogger.importedUUID);
        }
        plugin.getLogger().info(locations.size() + " creative block Location records imported from GameModeInventories");
        plugin.getLogger().info("Unfortunately GameModeInventories doesn't support import of inventories!");
    }


}
