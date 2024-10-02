package org.remi.wildfireBack.sim.tools;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class InitialBurningTreeParserTest {

    @Test
    void parseInitialBurningTreesChain_validChainWithSpaces_returnAllCoordsAndIgnoreSpaces() {
        String spacedChainInProperties = "[1,1], [   2   ,2]  ,    [3,   3   ]  , [   4   ,   4   ]";
        assertThat(InitialBurningTreeParser.parseInitialBurningTreesChain( spacedChainInProperties, 5, 5))
                .containsExactlyInAnyOrderElementsOf(
                        Set.of(
                                Pair.of(1,1),
                                Pair.of(2,2),
                                Pair.of(3,3),
                                Pair.of(4,4)
                                )
                );
    }

    @Test
    void parseInitialBurningTreesChain_invalidChain_ignoresIllFormedNegativesAndDecimals() {
        String invalidChain = "[1),1],[-2,2],[3.1,3],[4, ]";
        assertThat(InitialBurningTreeParser.parseInitialBurningTreesChain( invalidChain, 5, 5))
                .isEmpty();
    }

    @Test
    void parseInitialBurningTreesChain_validChainWithOutOfBoundAndDuplicates_returnCoordsInBoundWithoutDuplicates() {
        String chainWithOutOfBoundAndDuplicates = "[1,1],[1,5],[1,11], [5, 4], [6, 4]";
        assertThat(InitialBurningTreeParser.parseInitialBurningTreesChain( chainWithOutOfBoundAndDuplicates, 5, 5))
                .containsExactlyInAnyOrderElementsOf(
                        Set.of(
                                Pair.of(1,1),
                                Pair.of(1,5),
                                Pair.of(5,4)
                        )
                );
    }

    @Test
    void ensureValidCoordinates_coordsInBounds_returnPairContainingCoords() {
        assertThat(InitialBurningTreeParser.ensureValidCoordinates("1", "11", 4, 15))
                .isEqualTo(Pair.of(1,11));
    }

    @Test
    void ensureValidCoordinates_coordsOutOfBounds_returnPairContainingCeiledCoords() {
        assertThat(InitialBurningTreeParser.ensureValidCoordinates("100", "211", 4, 15))
                .isEqualTo(Pair.of(4,15));
    }
}