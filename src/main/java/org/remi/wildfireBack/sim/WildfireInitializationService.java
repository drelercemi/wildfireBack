package org.remi.wildfireBack.sim;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.remi.wildfireBack.sim.tools.InitialBurningTreeParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.Set;

/**
 * load initial conf for the sim. Especially handle the parsing of the initial burning trees chain.
 */
@Service
@Slf4j
@Getter
public class WildfireInitializationService {
    @Value("${sim.default.conf.width:20}")
    private int forestWidth;
    @Value("${sim.default.conf.height:10}")
    private int forestHeight;
    @Value("${sim.default.conf.propagation:50}")
    private int firePropagationProbability;
    @Value("${sim.default.conf.burning:[5,5]}")
    private String initialBurningTreesChain;
    private Set<Pair<Integer, Integer>> initialBurningTrees;
    // TODO: Add a seed? This would allow tests on fireSpread()
    private Random fireSpreader = new Random();

    @PostConstruct
    public void parseDataFromInitialBurningTreesChain() {
        if (this.forestWidth < 1) throw new IllegalArgumentException("sim.default.conf.width should be > 1");
        if (this.forestHeight < 1) throw new IllegalArgumentException("sim.default.conf.height should be > 1");
        if (this.firePropagationProbability < 0 || this.firePropagationProbability > 100) throw new IllegalArgumentException("sim.default.conf.propagation should between 0 and 100");

        this.initialBurningTrees = InitialBurningTreeParser.parseInitialBurningTreesChain(initialBurningTreesChain, forestWidth-1, forestHeight-1);
        log.debug("initial burning tree chain {} -> parsed in {}", initialBurningTreesChain, initialBurningTrees);
    }

    public boolean fireSpread() {
        return fireSpreader.nextInt(100) < this.getFirePropagationProbability();
    }

}
