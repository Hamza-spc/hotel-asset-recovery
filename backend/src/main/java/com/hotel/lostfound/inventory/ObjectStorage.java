package com.hotel.lostfound.inventory;

import java.io.InputStream;

public interface ObjectStorage {

    String store(String objectKey, String contentType, InputStream content, long size);

    StoredObject load(String objectKey);

    record StoredObject(String contentType, byte[] bytes) {}
}
