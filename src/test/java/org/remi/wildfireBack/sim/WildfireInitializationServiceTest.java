package org.remi.wildfireBack.sim;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class WildfireInitializationServiceTest {

    @Autowired
    private WildfireInitializationService wildfireInitializationService;

    @Test
    void wildfireInitializationService_withAllPropertiesCorrectlySetUp_loadAndReturnProperties() {
        assertThat(wildfireInitializationService.getForestWidth()).isEqualTo(40);
        assertThat(wildfireInitializationService.getForestHeight()).isEqualTo(22);
        assertThat(wildfireInitializationService.getFirePropagationProbability()).isEqualTo(100);
        assertThat(wildfireInitializationService.getInitialBurningTrees()).
                containsExactlyInAnyOrderElementsOf(Set.of(
                        Pair.of(14,9),
                        Pair.of(16,9)
                ));
    }

    @Test
    void fireSpread() {
        //could be tested with a seed
        assertThat(wildfireInitializationService.fireSpread()).isTrue();
    }
}