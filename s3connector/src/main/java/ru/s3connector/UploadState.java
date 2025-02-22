package ru.s3connector;

import software.amazon.awssdk.services.s3.model.CompletedPart;

import java.util.HashMap;
import java.util.Map;

public class UploadState {
    public String bucket;
    public String filekey;
    public String uploadId;
    public int partCounter;
    public Map<Integer, CompletedPart> completedParts = new HashMap<>();
    public int buffered = 0;

    // ... getters/setters omitted
    public UploadState(String bucket, String filekey) {
        this.bucket = bucket;
        this.filekey = filekey;
    }
}
