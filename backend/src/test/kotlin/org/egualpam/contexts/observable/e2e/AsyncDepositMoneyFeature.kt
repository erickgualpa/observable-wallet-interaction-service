package org.egualpam.contexts.observable.e2e

import org.apache.commons.lang3.RandomStringUtils.randomAlphabetic
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.data.TemporalUnitWithinOffset
import org.awaitility.Awaitility.await
import org.egualpam.contexts.observable.shared.adapters.AbstractIntegrationTest
import org.junit.jupiter.api.Test
import org.testcontainers.shaded.com.google.common.net.HttpHeaders.CONTENT_TYPE
import java.time.Instant
import java.time.temporal.ChronoUnit.SECONDS
import java.util.UUID.randomUUID
import java.util.concurrent.TimeUnit
import kotlin.random.Random.Default.nextDouble
import kotlin.random.Random.Default.nextInt
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class AsyncDepositMoneyFeature : AbstractIntegrationTest() {
  @Test
  fun `async deposit money`() {
    val walletId = randomUUID().toString()
    createWallet(walletId)

    val depositId = randomUUID().toString()
    val amount = nextDouble(10.0, 10000.0)
    val currency = "EUR"

    val request = """
      {
        "deposit": {
          "id": "$depositId",
          "amount": "$amount",
          "currency": "$currency"
        }
      }
    """

    webTestClient.put()
        .uri("/v2/wallets/{wallet-id}/deposit", walletId)
        .header(CONTENT_TYPE, "application/json")
        .bodyValue(request)
        .exchange()
        .expectStatus()
        .isAccepted

    await().atMost(10, TimeUnit.SECONDS).untilAsserted {
      val depositResult = depositTestRepository.findDeposit(depositId)
      assertThat(depositResult).satisfies(
          {
            assertNotNull(it)
            assertEquals(depositId, it.id)
            assertThat(it.createdAt).isCloseTo(
                Instant.now(),
                TemporalUnitWithinOffset(1, SECONDS),
            )
          },
      )
    }
  }

  @Test
  fun `request multiple deposits`() {
    val walletId = randomUUID().toString()
    createWallet(walletId)

    val amount = nextDouble(10.0, 10000.0)
    val currency = "EUR"

    // First deposit
    val firstDepositId = randomUUID().toString()
    val firstDepositRequest = """
      {
        "deposit": {
          "id": "$firstDepositId",
          "amount": "$amount",
          "currency": "$currency"
        }
      }
    """

    webTestClient.put()
        .uri("/v2/wallets/{wallet-id}/deposit", walletId)
        .header(CONTENT_TYPE, "application/json")
        .bodyValue(firstDepositRequest)
        .exchange()
        .expectStatus()
        .isAccepted

    // Next deposit
    val nextDepositId = randomUUID().toString()
    val nextDepositRequest = """
      {
        "deposit": {
          "id": "$nextDepositId",
          "amount": "$amount",
          "currency": "$currency"
        }
      }
    """

    webTestClient.put()
        .uri("/v2/wallets/{wallet-id}/deposit", walletId)
        .header(CONTENT_TYPE, "application/json")
        .bodyValue(nextDepositRequest)
        .exchange()
        .expectStatus()
        .isAccepted

    await().atMost(10, TimeUnit.SECONDS).untilAsserted {
      val firstDepositResult = depositTestRepository.findDeposit(firstDepositId)
      assertNotNull(firstDepositResult)
      val nextDepositResult = depositTestRepository.findDeposit(nextDepositId)
      assertNotNull(nextDepositResult)
    }
  }

  private fun createWallet(walletId: String) {
    val walletPersistenceId = nextInt(100, 999)
    walletTestRepository.createWallet(walletPersistenceId, walletId)

    val ownerId = randomUUID().toString()
    val ownerUsername = randomAlphabetic(10)
    ownerTestRepository.createOwner(ownerId, ownerUsername, walletPersistenceId, walletId)

    val accountId = randomUUID().toString()
    accountTestRepository.createAccount(
        accountEntityId = accountId,
        currency = "EUR",
        walletPersistenceId = walletPersistenceId,
        walletEntityId = walletId,
    )
  }
}
