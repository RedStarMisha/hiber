package ru.s3connector.model;

import software.amazon.awssdk.http.ContentStreamProvider;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

public class MyContentStreamProvider implements ContentStreamProvider {
    private InputStream contentStream;

    public MyContentStreamProvider(InputStream contentStream) {
        this.contentStream = new BufferedInputStream(contentStream);
        this.contentStream.mark(128 * 1024);
    }
    @Override
    public InputStream newStream() {
//        if (contentStream != null) {
//            try {
//                contentStream.close();
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//        }
        try {
            contentStream.reset();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return contentStream;
    }
}
