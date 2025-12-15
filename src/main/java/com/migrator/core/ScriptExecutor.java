package com.migrator.core;

import com.migrator.model.MigrationScript;


public abstract class ScriptExecutor {
    protected abstract void execute(MigrationScript script) throws Exception;
    public void executeWithRetry(MigrationScript script, int maxRetries) {

        int attempt = 1;

        while (true) {
            try {
                execute(script);
                return;
            } catch (Exception e) {
                if (attempt >= maxRetries) {
                    throw new RuntimeException(
                            "Migration failed after " + maxRetries + " attempts", e
                    );
                }

                System.out.println(
                        "Migration failed, retrying... Attempt " + attempt
                );

                attempt++;
            }
        }
    }
}
