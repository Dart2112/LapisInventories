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

import me.eccentric_nz.gamemodeinventories.GameModeInventoriesRequestAPI;
import net.lapsimc.lapisinventories.LapisInventories;
import org.bukkit.Location;

import java.util.List;

public class GameModeInventoriesHook {

    private LapisInventories plugin;

    public GameModeInventoriesHook(LapisInventories p) {
        plugin = p;
        injectCode();
    }

    private void injectCode() {

    }

    private void retrieveData() {
        List<Location> locations = new GameModeInventoriesRequestAPI().getBlocks();
        for (Location l : locations) {
            plugin.blockLogger.addBlock(l.getBlock(), plugin.blockLogger.importedUUID);
        }
        plugin.getLogger().info(locations.size() + " creative block Location records imported from GameModeInventories");
        plugin.getLogger().info("Unfortunately GameModeInventories doesn't support import of inventories!");
    }


}
