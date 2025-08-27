package ru.s3connector.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.s3connector.service.BucketService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("bucket")
public class BucketController {
    private final BucketService bucketService;

    @PostMapping("/create")
    public void createBucket(@RequestParam(required = false) String name) {
            bucketService.createBucket();
    }

    @PostMapping("policy")
    public void createPolicy() {
        bucketService.addPolicyToBucker();
    }
    @GetMapping("policy")
    public void getPolicy() {
        bucketService.getPolicy();
    }

    @PostMapping("rule")
    public void addRule() {
        bucketService.addRule();
    }
    @GetMapping("/get-all")
    public List<String> getAllBuckets() {
        return bucketService.getAllBuckets();
    }

    @GetMapping("/rules")
    public List<String> getRules(@RequestParam String name) {
        return bucketService.getRules(name);
    }
}
