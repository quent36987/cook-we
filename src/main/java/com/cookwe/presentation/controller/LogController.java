package com.cookwe.presentation.controller;


import com.cookwe.domain.entity.LogAutoServiceDTO;
import com.cookwe.domain.service.LogAutoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/logs")
@Tag(name = "Auth", description = "Authentication operations")
public class LogController {


    private final LogAutoService logAutoService;

    public LogController(LogAutoService logAutoService) {
        this.logAutoService = logAutoService;
    }


    @GetMapping("/auto/{page}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get the logs")
    public List<LogAutoServiceDTO> getAutoLogs(@PathVariable int page) {
        return logAutoService.getLogs(page);
    }

}
