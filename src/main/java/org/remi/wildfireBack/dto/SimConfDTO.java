package org.remi.wildfireBack.dto;

import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class SimConfDTO {
    private Integer forestWidth;
    private Integer forestHeight;
    private Integer firePropagationRate;
    private Integer simDuration;
    private Integer[][] firstBurningTrees;
}
