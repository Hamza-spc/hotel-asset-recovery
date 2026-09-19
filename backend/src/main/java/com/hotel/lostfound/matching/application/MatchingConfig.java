package com.hotel.lostfound.matching.application;

import com.hotel.lostfound.matching.HybridScorer;
import com.hotel.lostfound.matching.MatchingProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class MatchingConfig {

    @Bean
    HybridScorer hybridScorer(MatchingProperties properties) {
        return new HybridScorer(properties.textWeight(), properties.spatialWeight(), properties.spatialScale());
    }
}
