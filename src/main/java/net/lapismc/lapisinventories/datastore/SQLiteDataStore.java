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

package net.lapismc.lapisinventories.datastore;

import net.lapismc.datastore.SQLite;
import net.lapismc.datastore.util.LapisURL;
import net.lapismc.lapiscore.LapisCorePlugin;

import java.sql.Connection;

public class SQLiteDataStore extends SQLite {

    public CreativeBlocksTable table;

    public SQLiteDataStore(LapisCorePlugin core, LapisURL url) {
        super(core, url);
        initialiseDataStore();
    }

    @Override
    public void createTables(Connection conn) {
        table = new CreativeBlocksTable();
        table.createTable(conn);
    }
}

