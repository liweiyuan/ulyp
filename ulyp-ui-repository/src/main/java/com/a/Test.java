package com.a;

import com.ulyp.core.impl.OnDiskCallRecordInfo;
import org.dizitart.no2.Nitrite;
import org.dizitart.no2.mvstore.MVStoreModule;
import org.dizitart.no2.repository.ObjectRepository;

import java.io.File;

public class Test {

    public static void main(String[] args) {

        if (new File("C:/Temp/test.db").exists()) {
            new File("C:/Temp/test.db").delete();
        }

        MVStoreModule storeModule = MVStoreModule.withConfig()
                .filePath("C:/Temp/test.db")
                .compress(false)
                .build();

        Nitrite db = Nitrite.builder()
                .loadModule(storeModule)
//                .loadModule(new JacksonMapperModule())  // optional
                .openOrCreate("user", "password");

        ObjectRepository<OnDiskCallRecordInfo> repository = db.getRepository(OnDiskCallRecordInfo.class);

        OnDiskCallRecordInfo rcord = new OnDiskCallRecordInfo();
        rcord.setId(104L);
        repository.insert(rcord);

        System.out.println(repository.size());

        OnDiskCallRecordInfo byId = repository.getById(104L);

        System.out.println(byId.getId());

        rcord = byId;
        rcord.setId(104L);

        repository.update(rcord);

        byId = repository.getById(104L);

        System.out.println(byId.getId());
    }
}
