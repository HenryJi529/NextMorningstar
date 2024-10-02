package com.morningstar.initializer;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile("prod")
@Component
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class ProdInitializer extends CommonInitializer {
    @Override
    protected void initializeKill() {
    }
}
