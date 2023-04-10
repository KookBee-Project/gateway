package com.KookBee.gateway.filter;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class FilterConfig {
    private String baseMessage;
    private boolean preLogger;
    private boolean postLogger;

}
