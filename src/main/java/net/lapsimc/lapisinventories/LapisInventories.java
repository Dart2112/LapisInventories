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

import org.bukkit.plugin.java.JavaPlugin;

public final class LapisInventories extends JavaPlugin {

    public InventoryManager invManager;
    public InventoriesConfigs invConfigs;
    public InventoriesBlockLogger blockLogger;
    public LapisInventoriesAPI api;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        blockLogger = new InventoriesBlockLogger(this);
        invConfigs = new InventoriesConfigs(this);
        invManager = new InventoryManager(this);
        api = new LapisInventoriesAPI(this);
        new InventoriesListener(this);
        getLogger().info("LapisInventories v." + getDescription().getVersion() + " has been enabled");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
