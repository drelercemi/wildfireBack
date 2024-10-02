package org.remi.wildfireBack.sim.tools;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class InitialBurningTreeParser {

    public static Set<Pair<Integer, Integer>> parseInitialBurningTreesChain(String initialBurningTreesChain, Integer maxWidth, Integer maxHeight) {
        Set<Pair<Integer, Integer>> burningTreesAtTO = new HashSet<>();
        Pattern pairsOfIntegerInsideBrackets = Pattern.compile("\\[\\s*(\\d+)\\s*,\\s*(\\d+)\\s*\\]");
        Matcher pairsMatcher = pairsOfIntegerInsideBrackets.matcher(initialBurningTreesChain);
        while (pairsMatcher.find()) {
            burningTreesAtTO.add(ensureValidCoordinates(pairsMatcher.group(1), pairsMatcher.group(2), maxWidth, maxHeight));
        }
        if (burningTreesAtTO.isEmpty()) {
            log.warn("No initial burning trees found (chain in parameters was '{}'). The simulation will stop at t=0. The right format is a list of [int, int].", initialBurningTreesChain);
        }
        return burningTreesAtTO;
    }

    public static Pair<Integer, Integer> ensureValidCoordinates(String x, String y, Integer maxWidth, Integer maxHeight) {
        return Pair.of(
                Math.min(Integer.valueOf(x), maxWidth),
                Math.min(Integer.valueOf(y), maxHeight));

    }
}
