package ru.alegor.skypebot.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.alegor.skypebot.model.configuration.NodeDTO;
import ru.alegor.skypebot.service.plugins.AbstractPlugin;
import ru.alegor.skypebot.service.plugins.Configurable;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Service
@Slf4j
public class BotConfigurationService {

    @Autowired
    private BotService botService;
    private Set<String> pluginNames;

    public Collection<String> getConfigurable() {
        return pluginNames;
    }

    public NodeDTO getConfiguration(String pluginName) {
        if (!pluginNames.contains(pluginName)) {
            throw new IllegalStateException("Плагин " + pluginName + " не помечен как конфигурируемый");
        }
        return ((Configurable)botService.getPlugins().get(pluginName)).getConfiguration();
    }

    public void applyConfiguration(String pluginName, NodeDTO configuration) {
        if (!pluginNames.contains(pluginName)) {
            throw new IllegalStateException("Плагин " + pluginName + " не помечен как конфигурируемый");
        }
        ((Configurable)botService.getPlugins().get(pluginName)).applyConfiguration(configuration);
        log.info("Обновлена конфигурация плагина " + pluginName);
    }

    public void registerConfiguration(AbstractPlugin plugin) {
        if (!(plugin instanceof Configurable)) {
            throw new IllegalArgumentException("Плагин " + plugin.getPluginName() + " не является конфигурируемым");
        }
        pluginNames.add(plugin.getPluginName());
        log.info("Плагин " + plugin.getPluginName() + " зарегистрирован как конфигурируемый");
    }

    @PostConstruct
    private void init() {
        this.pluginNames = new HashSet<>();
    }

}
