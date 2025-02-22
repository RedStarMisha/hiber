package ru.s3connector.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.s3connector.client.S3Connector;
import ru.s3connector.service.BucketService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("bucket")
public class BucketController {
    private final S3Connector s3Connector;

    private final BucketService bucketService;

    @PostMapping("/create")
    public void createBucket(@RequestParam(required = false) String name) {
        if (name == null) {
            s3Connector.createBucket();
        } else {
            bucketService.addBucket(name);
        }
    }

    @PostMapping("rule")
    public void addRule() {
        s3Connector.addRule();
    }
    @GetMapping("/get-all")
    public List<String> getAllBuckets() {
        return s3Connector.getAllBuckets();
    }

    @GetMapping("/rules")
    public List<String> getRules(@RequestParam String name) {
        return s3Connector.getRules(name);
    }
}
