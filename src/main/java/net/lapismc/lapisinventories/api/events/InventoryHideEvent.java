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

package net.lapismc.lapisinventories.api.events;

import net.lapismc.lapiscore.events.LapisCoreEvent;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;

/**
 * This event is fired when a players inventory is hidden
 */
public class InventoryHideEvent extends LapisCoreEvent {

    private final Player p;
    private final PlayerInventory inv;

    public InventoryHideEvent(Player p, PlayerInventory inv) {
        this.p = p;
        this.inv = inv;
    }

    /**
     * Get the player
     *
     * @return the player who's inventory is being hidden
     */
    public Player getPlayer() {
        return p;
    }

    /**
     * Get the inventory
     *
     * @return the inventory that is being hidden
     */
    public PlayerInventory getPlayerInventory() {
        return inv;
    }


}
