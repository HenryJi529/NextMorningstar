package com.morningstar.boot.initializer;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile("prod")
@Component
@RequiredArgsConstructor
public class ProdInitializer extends CommonInitializer {
}
