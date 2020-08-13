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

package net.lapsimc.lapisinventories;

import net.lapismc.lapiscore.LapisCoreConfiguration;
import net.lapismc.lapiscore.LapisCorePlugin;
import net.lapismc.lapiscore.utils.Metrics;
import net.lapsimc.lapisinventories.playerdata.LapisInventoriesPlayer;

import java.util.HashMap;
import java.util.UUID;

public final class LapisInventories extends LapisCorePlugin {

    private final HashMap<UUID, LapisInventoriesPlayer> players = new HashMap<>();

    @Override
    public void onEnable() {
        registerConfiguration(new LapisCoreConfiguration(this, 1, 1));
        registerPermissions(new LapisInventoriesPermissions(this));
        new LapisInventoriesListener(this);
        new Metrics(this);
    }

    @Override
    public void onDisable() {
        for (LapisInventoriesPlayer player : players.values()) {
            player.savePlayer();
        }
    }

    public LapisInventoriesPlayer getPlayer(UUID uuid) {
        if (!players.containsKey(uuid)) {
            players.put(uuid, new LapisInventoriesPlayer(uuid));
        }
        return players.get(uuid);
    }

}
