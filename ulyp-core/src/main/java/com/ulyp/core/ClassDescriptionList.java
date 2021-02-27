package com.ulyp.core;

import com.google.protobuf.ByteString;
import com.ulyp.core.printers.TypeInfo;
import com.ulyp.transport.TClassDescriptionDecoder;
import com.ulyp.transport.TClassDescriptionEncoder;

public class ClassDescriptionList extends AbstractBinaryEncodedList<TClassDescriptionEncoder, TClassDescriptionDecoder> {

    public ClassDescriptionList() {
    }

    public ClassDescriptionList(ByteString bytes) {
        super(bytes);
    }

    public void add(TypeInfo typeInfo) {
        super.add(encoder -> {
            encoder.id(typeInfo.getId());
            encoder.className(typeInfo.getName());
        });
    }
}
