package com.ulyp.core.impl;

import com.ulyp.core.CallRecord;
import com.ulyp.core.CallRecordDatabase;
import it.unimi.dsi.fastutil.longs.LongList;
import org.dizitart.no2.Nitrite;
import org.dizitart.no2.mvstore.MVStoreModule;

import java.util.List;

public class NitriteCallRecordDatabase implements CallRecordDatabase {

    private final Nitrite db;

    public NitriteCallRecordDatabase() {
        MVStoreModule storeModule = MVStoreModule.withConfig()
                .filePath("/tmp/test.db")
                .compress(true)
                .build();

        db = Nitrite.builder()
                .loadModule(storeModule)
//                .loadModule(new JacksonMapperModule())
                .openOrCreate("sa", "");
    }

    @Override
    public CallRecord find(long id) {
        return null;
    }

    @Override
    public void deleteSubtree(long id) {

    }

    @Override
    public List<CallRecord> getChildren(long id) {
        return null;
    }

    @Override
    public LongList getChildrenIds(long id) {
        return null;
    }

    @Override
    public void persist(CallRecord node) {

    }

    @Override
    public void linkChild(long parentId, long childId) {

    }
}
