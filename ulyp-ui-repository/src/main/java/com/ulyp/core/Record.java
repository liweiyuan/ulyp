package com.ulyp.core;

import org.dizitart.no2.collection.Document;
import org.dizitart.no2.index.IndexType;
import org.dizitart.no2.mapper.Mappable;
import org.dizitart.no2.mapper.NitriteMapper;
import org.dizitart.no2.repository.annotations.Entity;
import org.dizitart.no2.repository.annotations.Id;
import org.dizitart.no2.repository.annotations.Index;

import java.util.ArrayList;
import java.util.List;

@Entity(value = "retired-employee",     // entity name (optional),
        indices = {
                @Index(value = "text", type = IndexType.Unique)
        })
public class Record implements Mappable {

    @Id
    private long id;
    private List<String> texts = new ArrayList<>();
    private String text;

    public long getId() {
        return id;
    }

    public Record setId(long id) {
        this.id = id;
        return this;
    }

    public String getText() {
        return text;
    }

    public Record setText(String text) {
        this.text = text;
        return this;
    }

    public List<String> getTexts() {
        return texts;
    }

    public Record setTexts(List<String> texts) {
        this.texts = texts;
        return this;
    }

    @Override
    public Document write(NitriteMapper mapper) {
        Document document = Document.createDocument();
        document.put("id", id);
        document.put("texts", texts);
        return document;
    }

    @Override
    public void read(NitriteMapper mapper, Document document) {
        this.id = (long) document.get("id");
        this.text = (String) document.get("text");
    }
}
