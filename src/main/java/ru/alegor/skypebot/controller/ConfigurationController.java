package ru.alegor.skypebot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.alegor.skypebot.model.configuration.NodeDTO;
import ru.alegor.skypebot.service.BotConfigurationService;

import java.util.Collection;

@RestController
@RequestMapping("/api/configuration")
public class ConfigurationController {

    @Autowired
    private BotConfigurationService configurationService;

    @GetMapping("/plugins")
    public Collection<String> getPlugins() {
        return configurationService.getConfigurable();
    }

    @GetMapping("/{plugin}")
    public NodeDTO getConfiguration(@PathVariable("plugin") String plugin){
        return configurationService.getConfiguration(plugin);
    }

    @PostMapping("/{plugin}")
    public void applyConfiguration(@PathVariable("plugin") String plugin, @RequestBody NodeDTO node) {
        configurationService.applyConfiguration(plugin, node);
    }
}
