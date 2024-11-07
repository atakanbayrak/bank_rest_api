package org.app.sekom_java_api.configuration.prometheus;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PrometheusService {

    private final MeterRegistry meterRegistry;

    public void incrementCustomCounter() {
        meterRegistry.counter("account_metric_counter", "endpoint", "/account/v1/getAllAccounts").increment();
    }

    public void recordCustomTimer(Runnable runnable) {
        Timer timer = meterRegistry.timer("custom_metric_timer", "method", "processData");
        timer.record(runnable);
    }



}
