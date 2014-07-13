/*
 * Copyright 2014 danidemi.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.danidemi.jlubricant.embeddable.h2;

import com.danidemi.jlubricant.contracts.DatabaseContractTest;
import com.danidemi.jlubricant.embeddable.Database;

/**
 *
 * @author danidemi
 */
public class DatabaseContract extends DatabaseContractTest {

    @Override
    public void startTest() {
    }

    @Override
    public void stopTest() throws Exception {
    }

    @Override
    protected Database buildADatabaseWithUsernameAndPassword(String username, String password) throws Exception {
        H2Dbms h2Dbms = new H2Dbms();
        H2Storage storage = new MemoryStorage();
        String dbName = "test-db";
        H2DatabaseDescription db = new H2DatabaseDescription(dbName, storage, username, password);
        h2Dbms.add( db );
        h2Dbms.start();
        return h2Dbms.dbByName(dbName);
    }
    
}
