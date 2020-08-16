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

package net.lapismc.lapisinventories.api;

import net.lapismc.lapisinventories.LapisInventories;
import net.lapismc.lapisinventories.playerdata.LapisInventoriesPlayer;

import java.util.UUID;

public class InventoryPlayerAPI {

    /**
     * Get the player object used by the plugin, this should only be used if you need something from the object that isn't exposed in this class
     *
     * @param uuid The UUID of the player you wish to fetch
     * @return the {@link LapisInventoriesPlayer} object for the UUID given
     */
    public LapisInventoriesPlayer getPlayer(UUID uuid) {
        return ((LapisInventories) LapisInventories.getInstance()).getPlayer(uuid);
    }

    /**
     * Check if the players inventory is currently hidden
     *
     * @param uuid the UUID of the player to check
     * @return true if inventory hidden, otherwise false
     */
    public boolean isHidden(UUID uuid) {
        return getPlayer(uuid).getIsHidden();
    }

    /**
     * Hide the players inventory, this will save their inventory to config and then clear it
     * use {@link #restoreInventory(UUID)} to bring back the players inventory
     *
     * @param uuid The UUID of the player
     */
    public void hideInventory(UUID uuid) {
        getPlayer(uuid).hideInventory();
    }

    /**
     * Restore the players inventory that was saved in {@link #hideInventory(UUID)}
     *
     * @param uuid The UUID of the player
     */
    public void restoreInventory(UUID uuid) {
        getPlayer(uuid).restoreInventory();
    }

    /**
     * Saves the players current gamemode inventory to their config file
     *
     * @param uuid The UUID of the player
     */
    public void saveInventory(UUID uuid) {
        getPlayer(uuid).saveInventory();
    }

    /**
     * Replaces their current gamemode inventory with the one stored in their config
     *
     * @param uuid The UUID of the player
     */
    public void loadInventory(UUID uuid) {
        getPlayer(uuid).loadInventory();
    }

}
