package com.pockru.dongzakgol.module.realm;

import io.realm.DynamicRealm;
import io.realm.FieldAttribute;
import io.realm.RealmMigration;
import io.realm.RealmObjectSchema;
import io.realm.RealmSchema;

/**
 * Created by rhpark on 2016. 3. 24..
 * JIRA: MWP-
 */
public class DzgMigration implements RealmMigration {

    @Override
    public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {

        RealmSchema schema = realm.getSchema();

        schema.get("Category").setNullable("id", true);

        /**
         * Version 0 -> Version 1
         *
         * 자주가는 카테고리 order 필드 추가
         */
        if (oldVersion == 0) {
            RealmObjectSchema cateSchema = schema.get("Category");
            cateSchema.addField("favOrder", Long.class, FieldAttribute.REQUIRED);
            oldVersion++;
        }

    }
}
