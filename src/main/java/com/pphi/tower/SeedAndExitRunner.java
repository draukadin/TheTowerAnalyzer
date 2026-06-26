package com.pphi.tower;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile("seed")
@Component
public class SeedAndExitRunner implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(SeedAndExitRunner.class);

    @Override
    public void run(ApplicationArguments args) {
        log.info("Factory database seeding complete — exiting.");
        System.exit(0);
    }
}