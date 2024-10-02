package org.remi.wildfireBack.controllers;

import org.junit.jupiter.api.Test;
import org.remi.wildfireBack.dto.ForestDTO;
import org.remi.wildfireBack.dto.ForestPlot;
import org.remi.wildfireBack.dto.SimConfDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.remi.wildfireBack.controllers.WildfireController.FOREST_ENDPOINT;
import static org.remi.wildfireBack.controllers.WildfireController.SIMULATION_CONFIGURATION_ENDPOINT;
import static org.remi.wildfireBack.dto.ForestPlot.ASHES;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class WildfireControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void getSimConfiguration_givenTestConf_shouldPrintConfAndDuration() {
        assertThat(this.restTemplate.getForObject("http://localhost:" + port + "/" + SIMULATION_CONFIGURATION_ENDPOINT,
                SimConfDTO.class)).isEqualTo(new SimConfDTO(
                        40, 22, 100, 37, new Integer[][]{{14,9}, {16,9}})
        );
    }

    @Test
    void getForest_atTheEndOfTheSim_everythingShouldBeBurnt() {
        ForestPlot[][] burnt = new ForestPlot[40][22];
        Arrays.stream(burnt).forEach(col -> Arrays.fill(col, ASHES));
        assertThat(this.restTemplate.getForObject("http://localhost:" + port + "/" + FOREST_ENDPOINT + "/9999",
                ForestDTO.class)).isEqualTo(new ForestDTO(burnt));
    }
}