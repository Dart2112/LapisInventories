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

package net.lapsimc.lapisinventories.api.events;

import net.lapismc.lapiscore.events.LapisCoreEvent;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;

/**
 * This event is fired when a players inventory is loaded from file
 */
public class InventoryLoadEvent extends LapisCoreEvent {

    private final Player p;
    private final PlayerInventory inv;
    private final GameMode gm;

    public InventoryLoadEvent(Player p, PlayerInventory inv, GameMode gm) {
        this.p = p;
        this.inv = inv;
        this.gm = gm;
    }

    /**
     * Get the player
     *
     * @return the player who's inventory is being loaded
     */
    public Player getPlayer() {
        return p;
    }

    /**
     * Get the inventory
     *
     * @return the inventory that is being loaded
     */
    public PlayerInventory getPlayerInventory() {
        return inv;
    }

    /**
     * Get the gamemode
     *
     * @return the gamemode that the players inventory is being loaded from
     */
    public GameMode getGameMode() {
        return gm;
    }

}
