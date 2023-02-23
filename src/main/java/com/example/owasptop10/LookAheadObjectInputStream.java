package com.example.owasptop10;

import java.io.*;
import java.util.Set;

public class LookAheadObjectInputStream extends ObjectInputStream {

    Set<String> whiteList;

    public LookAheadObjectInputStream(InputStream in, Set whiteList) throws IOException {
        super(in);
        this.whiteList = whiteList;
    }

    @Override
    protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException
    {
        String name = desc.getName();

        if(!whiteList.contains(name)) {
            throw new InvalidClassException("Unsupported class", name);
        }
        return super.resolveClass(desc);
    }
}
