package com.a;

import com.ulyp.core.Record;
import org.dizitart.no2.Nitrite;
import org.dizitart.no2.collection.NitriteCollection;
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

        NitriteCollection collection = db.getCollection("test");

        ObjectRepository<Record> repository = db.getRepository(Record.class);

        Record rcord = new Record();
        rcord.setId(104L);
        rcord.setText("asdasd");
        repository.insert(rcord);

        System.out.println(repository.size());

        Record byId = repository.getById(104L);

        System.out.println(byId.getId());
        System.out.println(byId.getText());

        rcord = new Record();
        rcord.setId(104L);
        rcord.setText("123");

        repository.update(rcord);

        byId = repository.getById(104L);

        System.out.println(byId.getId());
        System.out.println(byId.getText());
    }
}
