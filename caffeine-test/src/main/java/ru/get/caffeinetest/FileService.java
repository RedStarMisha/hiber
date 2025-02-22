package ru.get.caffeinetest;

import com.github.benmanes.caffeine.cache.Cache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileService {
    private final Cache<String, byte[]> caffeineCache;
    public void putFile(MultipartFile[] files) {
        Arrays.stream(files).forEach(file -> {
            try {
                caffeineCache.put(file.getOriginalFilename(), file.getBytes());
                log.info("file with name {} was saved", file.getOriginalFilename());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void initializeGS() {
        System.gc();
    }

    public byte[] getFileByName(String name) {
        return caffeineCache.getIfPresent(name);
    }
}
