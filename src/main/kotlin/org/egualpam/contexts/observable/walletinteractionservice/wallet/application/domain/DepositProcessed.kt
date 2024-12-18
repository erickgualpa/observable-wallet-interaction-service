package org.egualpam.contexts.observable.walletinteractionservice.wallet.application.domain

import org.egualpam.contexts.observable.walletinteractionservice.shared.application.domain.DomainEvent
import org.egualpam.contexts.observable.walletinteractionservice.shared.application.domain.DomainEventId
import java.time.Instant

class DepositProcessed(wallet: Wallet) : DomainEvent(wallet) {
  private val id = DomainEventId.generate()
  private val occurredOn = Instant.now()

  override fun id() = this.id
  override fun occurredOn(): Instant = this.occurredOn
}
