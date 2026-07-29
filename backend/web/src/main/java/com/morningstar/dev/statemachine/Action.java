package com.morningstar.dev.statemachine;

import java.util.UUID;

public interface Action {
    void execute(UUID runId);

    Type getType();

    enum Type {
        START,
        SYNC,
        SCAN,
        FIX,
        VERIFY,
        SUBMIT,
        CLEAN,
        RESTORE,

        MOCK;

        public static final String BASE_NAME = "BASE";
        public static final String START_NAME = "START";
        public static final String SYNC_NAME = "SYNC";
        public static final String SCAN_NAME = "SCAN";
        public static final String FIX_NAME = "FIX";
        public static final String VERIFY_NAME = "VERIFY";
        public static final String SUBMIT_NAME = "SUBMIT";
        public static final String CLEAN_NAME = "CLEAN";
        public static final String RESTORE_NAME = "RESTORE";
    }
}
