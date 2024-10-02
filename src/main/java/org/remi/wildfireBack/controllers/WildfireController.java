package org.remi.wildfireBack.controllers;

import org.remi.wildfireBack.dto.ForestDTO;
import org.remi.wildfireBack.dto.SimConfDTO;
import org.remi.wildfireBack.sim.WildfireInitializationService;
import org.remi.wildfireBack.sim.WildfireSimulationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WildfireController {

    public static final String SIMULATION_CONFIGURATION_ENDPOINT = "/simulation/configuration";
    public static final String FOREST_ENDPOINT = "/forest";

    @Autowired
    private WildfireInitializationService wildfireInitializationService;

    @Autowired
    private WildfireSimulationService wildfireSimulationService;

    @GetMapping(SIMULATION_CONFIGURATION_ENDPOINT)
    public SimConfDTO getSimConfiguration() {
        return new SimConfDTO(
                wildfireInitializationService.getForestWidth(),
                wildfireInitializationService.getForestHeight(),
                wildfireInitializationService.getFirePropagationProbability(),
                wildfireSimulationService.getSimDuration(),
                wildfireInitializationService.getInitialBurningTrees().stream().map(forestPlot -> new Integer[]{forestPlot.getLeft(), forestPlot.getRight()}).toArray(Integer[][]::new)
        );
    }

    @GetMapping(FOREST_ENDPOINT+"/{time}")
    public ForestDTO getForest(@PathVariable String time) {
        return new ForestDTO(wildfireSimulationService.getForestStateAt(Math.max(0, Math.min(Integer.parseInt(time), wildfireSimulationService.getSimDuration()-1))));
    }

}
