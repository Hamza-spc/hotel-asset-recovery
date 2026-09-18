package com.hotel.lostfound.inventory.storage;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.storage")
record MinioProperties(String endpoint, String accessKey, String secretKey, String bucket, String region) {}
