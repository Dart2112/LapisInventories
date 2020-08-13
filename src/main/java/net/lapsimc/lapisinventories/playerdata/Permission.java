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

package net.lapsimc.lapisinventories.playerdata;

import net.lapismc.lapiscore.permissions.LapisPermission;

public enum Permission {

    InventoryControl(new InventoryControl()), TrackBlockPlacing(new TrackBlockPlacing());

    private final LapisPermission permission;

    Permission(LapisPermission permission) {
        this.permission = permission;
    }

    public LapisPermission getPermission() {
        return this.permission;
    }

    private static class InventoryControl extends LapisPermission {
        //Players will have different inventories for each gamemode if this is enabled
        InventoryControl() {
            super("InventoryControl");
        }
    }

    private static class TrackBlockPlacing extends LapisPermission {
        //Blocks placed by this player in creative mode will be tracked when enabled
        TrackBlockPlacing() {
            super("TrackBlockPlacing");
        }
    }

}
