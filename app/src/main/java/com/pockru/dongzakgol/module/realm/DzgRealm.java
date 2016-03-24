package com.pockru.dongzakgol.module.realm;

import android.content.Context;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by rhpark on 2016. 3. 24..
 * JIRA: MWP-
 */
public class DzgRealm {

    public static final String REALM_NAME = "dzgol_main_db";
    public static final int REALM_SCHEMA_VER = 1;

    private static RealmConfiguration mRealmConfig;

    public static Realm getInstance(Context context) {
        if (mRealmConfig == null) {
            mRealmConfig = new RealmConfiguration.Builder(context)
                    .name(REALM_NAME)
                    .schemaVersion(REALM_SCHEMA_VER)
                    .migration(new DzgMigration())
                    .build();
        }
        return Realm.getInstance(mRealmConfig);
    }
}
