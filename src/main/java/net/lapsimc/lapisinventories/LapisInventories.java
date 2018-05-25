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

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.UUID;

public final class LapisInventories extends JavaPlugin {

    LapisUpdater updater;
    LapisInventoriesAPI api;
    InventoryManager invManager;
    InventoriesConfigurations invConfigs;
    InventoriesBlockLogger blockLogger;
    ArrayList<UUID> inspectingPlayers = new ArrayList<>();

    @Override
    public void onEnable() {
        saveDefaultConfig();
        updater = new LapisUpdater(this, "LapisInventories", "LapisPlugins", "LapisInventories", "master");
        blockLogger = new InventoriesBlockLogger(this);
        invConfigs = new InventoriesConfigurations(this);
        invManager = new InventoryManager(this);
        api = new LapisInventoriesAPI(this);
        new InventoriesListener(this);
        new InventoriesCommand(this);
        getLogger().info("LapisInventories v." + getDescription().getVersion() + " has been enabled");
        checkForUpdates();
    }

    @Override
    public void onDisable() {
        getLogger().info("LapisInventories has been disabled");
    }

    private void checkForUpdates() {
        Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
            if (getConfig().getBoolean("Update.check")) {
                if (updater.checkUpdate()) {
                    if (!getConfig().getBoolean("Update.download")) {
                        getLogger().info("An update is available, use /lapisinventories update to install it or download it from spigot");
                    }
                } else {
                    getLogger().info("No update available");
                }
            } else if (getConfig().getBoolean("Update.download")) {
                if (updater.checkUpdate()) {
                    getLogger().info("An update is available, it is being downloaded now and will be installed when the server restarts");
                    updater.downloadUpdate();
                }
            }
        });

    }
}
