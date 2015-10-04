/*
 *
 * Copyright (c) 2011, Xiufeng Liu (xiliu@cs.aau.dk) and the eGovMon Consortium
 * Permission to use, copy, modify, and distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
 * ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
 * OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 *
 *
 */

package xlsystem.load.bdb;

import java.io.File;


import xlsystem.common.Configure;

import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.StoreConfig;

public class MyDbEnv {

    private Environment myEnv;
    private EntityStore store;


    // Our constructor does nothing
    public MyDbEnv() {}

    // The setup() method opens the environment and store
    // for us.
    public void setup(File envHome) 
        throws DatabaseException {

        EnvironmentConfig myEnvConfig = new EnvironmentConfig();
        myEnvConfig.setCacheSize(Configure.getInstance().getBdbBufSize()*1024*1024);
        //myEnvConfig.setCacheSize(300*1024*1024);
        myEnvConfig.setReadOnly(false);
        myEnvConfig.setAllowCreate(true);
        
        myEnvConfig.setSharedCache(true);
      //  myEnvConfig.setTransactional(false);

        myEnvConfig.setConfigParam("je.nodeMaxEntries", "512");
        

        StoreConfig storeConfig = new StoreConfig();
        storeConfig.setTemporary(true);
        storeConfig.setReadOnly(false);
        storeConfig.setAllowCreate(true);
      

        // Open the environment and entity store
        myEnv = new Environment(envHome, myEnvConfig);
        store = new EntityStore(myEnv, "EntityStore", storeConfig);
    }

    // Return a handle to the entity store
    public EntityStore getEntityStore() {
        return store;
    }


    // Return a handle to the environment
    public Environment getEnv() {
        return myEnv;
    }


    // Close the store and environment
    public void close() {

        if (store != null) {
            try {
            	store.sync();
                store.close();
            } catch(DatabaseException dbe) {
                System.err.println("Error closing store: " + 
                                    dbe.toString());
               System.exit(-1);
            }
        }
    	
        if (myEnv != null) {
            try {
                // Finally, close the store and environment.
                myEnv.close();
            } catch(DatabaseException dbe) {
                System.err.println("Error closing MyDbEnv: " + 
                                    dbe.toString());
               System.exit(-1);
            }
        }
    }
}

