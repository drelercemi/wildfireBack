package org.remi.wildfireBack.sim;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.remi.wildfireBack.dto.ForestPlot;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.remi.wildfireBack.dto.ForestPlot.*;

@ExtendWith(MockitoExtension.class)
@Slf4j
class WildfireSimulationServiceTest {

    @InjectMocks
    WildfireSimulationService wildfireSimulationService;

    @Mock
    WildfireInitializationService wildfireInitializationService;


    @Test
    void spreadFireAndTurnToAshesForEachBurntTrees_midllePlotWithFullPropagation_shouldBurnFourTree() {
        ForestPlot[][] forestAtT0 = new ForestPlot[3][3];
        ForestPlot[][] forestT1 = {{TREE, FIRE, TREE}, {FIRE, ASHES, FIRE}, {TREE, FIRE, TREE}};
        Stream.of(forestAtT0).forEach(forestLine -> Arrays.fill(forestLine, TREE));
        forestAtT0[1][1] = FIRE;
        ForestPlot[][] forestAtT1 = WildfireSimulationService.deepClone(forestAtT0);

        Set<Pair<Integer, Integer>> burningTrees = new HashSet<>();

        when(wildfireInitializationService.fireSpread()).thenReturn(true);

        wildfireSimulationService.spreadFireAndTurnToAshesForEachBurntTrees(Set.of(Pair.of(1,1)), forestAtT1, burningTrees, 3, 3);

        verify(wildfireInitializationService, times(4)).fireSpread();

        assertThat(forestAtT1).isEqualTo(forestT1);

        assertThat(burningTrees).containsExactlyInAnyOrder(
                Pair.of(1,0),
                Pair.of(1,2),
                Pair.of(0,1),
                Pair.of(2,1)
        );
    }

    @Test
    void preRunSim_simple4x3SimWith100PropagationRate_everythingBurnIn5Rounds() {
        when(wildfireInitializationService.getForestWidth()).thenReturn(4);
        when(wildfireInitializationService.getForestHeight()).thenReturn(3);
        when(wildfireInitializationService.getInitialBurningTrees()).thenReturn(Set.of(Pair.of(1,1)));
        when(wildfireInitializationService.fireSpread()).thenReturn(true);

        wildfireSimulationService.preRunSim();

        assertThat(wildfireSimulationService.getSimDuration()).isEqualTo(5);
        ForestPlot[][][] expectedForestOverTime = {
                {{TREE, TREE, TREE}, {TREE, FIRE, TREE}, {TREE, TREE, TREE}, {TREE, TREE, TREE}},
                {{TREE, FIRE, TREE}, {FIRE, ASHES, FIRE}, {TREE, FIRE, TREE}, {TREE, TREE, TREE}},
                {{FIRE, ASHES, FIRE}, {ASHES, ASHES, ASHES}, {FIRE, ASHES, FIRE}, {TREE, FIRE, TREE}},
                {{ASHES, ASHES, ASHES}, {ASHES, ASHES, ASHES}, {ASHES, ASHES, ASHES}, {FIRE, ASHES, FIRE}},
                {{ASHES, ASHES, ASHES}, {ASHES, ASHES, ASHES}, {ASHES, ASHES, ASHES}, {ASHES, ASHES, ASHES}}
        };
        for (int t=0; t< 5; t++) {
            assertThat(wildfireSimulationService.getForestStateAt(t)).isEqualTo(expectedForestOverTime[t]);
        }
    }

    @Test
    void preRunSim_simple2x2SimWithNoPropagationRate_stopAtRound2() {
        when(wildfireInitializationService.getForestWidth()).thenReturn(2);
        when(wildfireInitializationService.getForestHeight()).thenReturn(2);
        when(wildfireInitializationService.getInitialBurningTrees()).thenReturn(Set.of(Pair.of(1,1)));
        when(wildfireInitializationService.fireSpread()).thenReturn(false);

        wildfireSimulationService.preRunSim();

        assertThat(wildfireSimulationService.getSimDuration()).isEqualTo(2);
        ForestPlot[][][] expectedForestOverTime = {
                {{TREE, TREE}, {TREE, FIRE}},
                {{TREE, TREE}, {TREE, ASHES}}
        };
        for (int t=0; t< 2; t++) {
            assertThat(wildfireSimulationService.getForestStateAt(t)).isEqualTo(expectedForestOverTime[t]);
        }
    }
}