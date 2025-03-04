package org.egualpam.contexts.observable.walletinteractionservice.wallet.adapters.`in`.controllers

data class PutWalletDepositRequest(val deposit: Deposit) {
  data class Deposit(val id: String, val amount: Double, val currency: String)
}
