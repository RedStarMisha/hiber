package ru.get.caffeinetest;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class CacheController {
    private final FileService fileService;

    @PostMapping
    public void addFile(@RequestParam MultipartFile[] files) {
        fileService.putFile(files);
    }

    @GetMapping
    public byte[] getFileByName(@RequestParam String fileName) {
        return fileService.getFileByName(fileName);
    }

    @PostMapping("/gs")
    public void initializeGS() {
        fileService.initializeGS();
    }
}
