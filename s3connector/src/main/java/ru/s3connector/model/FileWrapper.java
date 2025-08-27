package ru.s3connector.model;

import lombok.Data;
import org.springframework.core.io.buffer.DataBuffer;
import reactor.core.publisher.Flux;

import java.nio.ByteBuffer;
import java.util.OptionalLong;

@Data
public class FileWrapper {
    private Flux<ByteBuffer> dataBufferFlux;
    private OptionalLong contentLength;
}
