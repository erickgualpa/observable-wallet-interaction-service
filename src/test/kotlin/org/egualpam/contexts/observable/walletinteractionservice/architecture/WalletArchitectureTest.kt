package org.egualpam.contexts.observable.walletinteractionservice.architecture

import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes
import org.egualpam.contexts.observable.walletinteractionservice.shared.application.domain.AggregateRoot
import org.junit.jupiter.api.Test

class WalletArchitectureTest {

  private val importedClasses =
      ClassFileImporter().importPackages("org.egualpam.contexts.observable.walletinteractionservice")

  @Test
  fun `wallet entity should be the wallet aggregate root`() {
    classes().that()
        .resideInAPackage("..domain..")
        .and()
        .haveNameMatching(".*Wallet")
        .should()
        .beAssignableTo(AggregateRoot::class.java)
        .check(importedClasses)
  }
}
