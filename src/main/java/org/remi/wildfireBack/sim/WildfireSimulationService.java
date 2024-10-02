package org.remi.wildfireBack.sim;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.remi.wildfireBack.dto.ForestPlot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import static org.remi.wildfireBack.dto.ForestPlot.*;

/**
 * run the full sim in a postConstruct method. Then exposes the forest for every time, and sim max time.
 */
@Service
@Slf4j
public class WildfireSimulationService {

    // this represents the forest over time [time][x][y]
    private ForestPlot[][][] forestOverTime;

    @Autowired
    private WildfireInitializationService wildfireInitializationService;

    @PostConstruct
    public void preRunSim() {
        LocalDateTime startSim = LocalDateTime.now();
        //fill the forest at T0
        ForestPlot[][] forestAtT0 = new ForestPlot[wildfireInitializationService.getForestWidth()][wildfireInitializationService.getForestHeight()];
        Stream.of(forestAtT0).forEach(forestLine -> Arrays.fill(forestLine, TREE));

        //initial fires
        Set<Pair<Integer, Integer>> burningTrees = wildfireInitializationService.getInitialBurningTrees();
        burningTrees.forEach(burningTree -> forestAtT0[burningTree.getLeft()][burningTree.getRight()] = FIRE);

        ArrayList<ForestPlot[][]> forestThroughTime = new ArrayList<>();
        forestThroughTime.add(forestAtT0);
        Set<Pair<Integer, Integer>> burntTrees;

        while (!burningTrees.isEmpty()) {
            forestThroughTime.add(deepClone(forestThroughTime.getLast()));
            burntTrees = burningTrees;
            burningTrees = new HashSet<>();
            spreadFireAndTurnToAshesForEachBurntTrees(burntTrees, forestThroughTime.getLast(), burningTrees, wildfireInitializationService.getForestHeight(), wildfireInitializationService.getForestWidth());
        }

        this.forestOverTime = forestThroughTime.toArray(new ForestPlot[forestThroughTime.size()][wildfireInitializationService.getForestHeight()][wildfireInitializationService.getForestWidth()]);
        log.debug("sim rendered in {}s ({} loops)", Duration.between(startSim, LocalDateTime.now()).toSeconds(), this.forestOverTime.length);
    }

    static ForestPlot[][] deepClone(ForestPlot[][] lastforest) {
        ForestPlot[][] currentForest = new ForestPlot[lastforest.length][lastforest[0].length];
        for (int i= 0; i < lastforest.length; i++) {
            currentForest[i] = Arrays.copyOf(lastforest[i], lastforest[i].length);
        }
        return currentForest;
    }

    public Integer getSimDuration() {
        return this.forestOverTime.length;
    }

    public ForestPlot[][] getForestStateAt(Integer time) {
        return this.forestOverTime[time];
    }

    void spreadFireAndTurnToAshesForEachBurntTrees(final Set<Pair<Integer, Integer>> burntTrees, ForestPlot[][] forestThroughTime, Set<Pair<Integer, Integer>> burningTrees, Integer maxHeight, Integer maxWidth) {
        for (Pair<Integer, Integer> burntPlot: burntTrees) {
            //handle fire propagation to north
            if (burntPlot.getRight() > 0 && forestThroughTime[burntPlot.getLeft()][burntPlot.getRight()-1].equals(TREE) && wildfireInitializationService.fireSpread()) {
                spreadFireTo(burntPlot.getLeft(), burntPlot.getRight()-1, forestThroughTime, burningTrees);
            }
            //south
            if (burntPlot.getRight() < maxHeight-1 && forestThroughTime[burntPlot.getLeft()][burntPlot.getRight()+1].equals(TREE) && wildfireInitializationService.fireSpread()) {
                spreadFireTo(burntPlot.getLeft(), burntPlot.getRight()+1, forestThroughTime, burningTrees);
            }
            //west
            if (burntPlot.getLeft() > 0 && forestThroughTime[burntPlot.getLeft()-1][burntPlot.getRight()].equals(TREE) && wildfireInitializationService.fireSpread()) {
                spreadFireTo(burntPlot.getLeft()-1, burntPlot.getRight(), forestThroughTime, burningTrees);
            }
            //east
            if (burntPlot.getLeft() < maxWidth-1 && forestThroughTime[burntPlot.getLeft()+1][burntPlot.getRight()].equals(TREE) && wildfireInitializationService.fireSpread()) {
                spreadFireTo(burntPlot.getLeft()+1, burntPlot.getRight(), forestThroughTime, burningTrees);
            }
            forestThroughTime[burntPlot.getLeft()][burntPlot.getRight()] = ASHES;
        }
    }

    // spread fire in current forest to tree x,y and add it to the list of currently burning trees
    private static void spreadFireTo(Integer x, Integer y, ForestPlot[][] forestThroughTime, Set<Pair<Integer, Integer>> burningTrees) {
        forestThroughTime[x][y] = FIRE;
        burningTrees.add(Pair.of(x, y));
    }
}
