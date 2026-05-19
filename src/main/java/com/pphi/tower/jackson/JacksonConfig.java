package com.pphi.tower.jackson;

import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.pphi.tower.model.TowerNumber;
import com.pphi.tower.model.battlehistory.BattleHistory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {

    @Bean
    public SimpleModule towerModule() {
        SimpleModule module = new SimpleModule("TowerModule");
        module.addSerializer(TowerNumber.class, new TowerNumberSerializer());
        module.addDeserializer(BattleHistory.class, new BattleHistoryDeserializer());
        return module;
    }

    @Bean
    public JavaTimeModule javaTimeModule() {
        return new JavaTimeModule();
    }
}
