package org.egualpam.contexts.observable.wallet.adapters.`in`.controllers

import org.egualpam.contexts.observable.shared.adapters.metrics.ErrorMetrics
import org.egualpam.contexts.observable.wallet.application.usecases.command.DepositMoney
import org.egualpam.contexts.observable.wallet.application.usecases.command.DepositMoneyCommand
import org.slf4j.Logger
import org.slf4j.LoggerFactory.getLogger
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.accepted
import org.springframework.transaction.support.TransactionTemplate
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.concurrent.CompletableFuture.runAsync

@RequestMapping("/v2/wallets")
@RestController
class PutWalletDepositControllerV2(
  private val transactionTemplate: TransactionTemplate,
  private val depositMoney: DepositMoney,
  private val errorMetrics: ErrorMetrics,
) {
  private val logger: Logger = getLogger(this::class.java)

  @PutMapping("/{wallet-id}/deposit")
  fun putWalletDeposit(
    @PathVariable("wallet-id") walletId: String,
    @RequestBody putWalletDepositRequest: PutWalletDepositRequest
  ): ResponseEntity<Void> {
    val depositMoneyCommand = DepositMoneyCommand(
        walletId,
        depositId = putWalletDepositRequest.deposit.id,
        depositAmount = putWalletDepositRequest.deposit.amount,
        depositCurrency = putWalletDepositRequest.deposit.currency,
    )
    runAsync {
      try {
        transactionTemplate.executeWithoutResult {
          depositMoney.execute(depositMoneyCommand)
        }
      } catch (e: RuntimeException) {
        errorMetrics.error(e)
        logger.error("Unexpected error processing request [$putWalletDepositRequest]:", e)
      }
    }
    return accepted().build()
  }
}
