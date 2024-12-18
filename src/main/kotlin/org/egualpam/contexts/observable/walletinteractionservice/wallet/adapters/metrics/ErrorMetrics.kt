package org.egualpam.contexts.observable.walletinteractionservice.wallet.adapters.metrics

import io.micrometer.core.instrument.Counter
import io.micrometer.core.instrument.MeterRegistry

class ErrorMetrics(
  private val meterRegistry: MeterRegistry
) {
  fun error(cause: Exception) {
    Counter.builder("wallet_error_occurred")
        .tag("type", cause.javaClass.simpleName)
        .register(meterRegistry)
        .increment()
  }
}
