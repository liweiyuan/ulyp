package com.ulyp.core;

import com.google.protobuf.ByteString;
import com.ulyp.transport.TClassDescriptionDecoder;
import com.ulyp.transport.TClassDescriptionEncoder;

// Flexible SBE wrapper
public class ClassDescriptionList extends AbstractSbeRecordList<TClassDescriptionEncoder, TClassDescriptionDecoder> {

    public ClassDescriptionList() {
    }

    public ClassDescriptionList(ByteString bytes) {
        super(bytes);
    }

    public void add(ClassDescription classDescription) {
        super.add(encoder -> {
            encoder.id(classDescription.getId());
            encoder.className(classDescription.getName());
        });
    }
}
