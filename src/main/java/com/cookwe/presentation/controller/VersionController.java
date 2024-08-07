package com.cookwe.presentation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/version")
@Tag(name = "Version", description = "Version operations")
public class VersionController {

    @Value("${cook-we.version}")
    private String version;

    @GetMapping("")
    @Operation(summary = "Get the back version")
    public String getVersion() {
        return version;
    }
}
