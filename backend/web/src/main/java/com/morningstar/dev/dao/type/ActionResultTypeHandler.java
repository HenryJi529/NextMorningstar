package com.morningstar.dev.dao.type;

import com.morningstar.dev.statemachine.result.ActionResult;
import com.morningstar.infra.dao.type.JsonTypeHandler;
import org.springframework.stereotype.Component;

@Component
public class ActionResultTypeHandler extends JsonTypeHandler<ActionResult> {
    public ActionResultTypeHandler() {
        super(ActionResult.class);
    }
}
