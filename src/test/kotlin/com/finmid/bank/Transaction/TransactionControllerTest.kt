package com.finmid.bank.Transaction

import com.finmid.bank.ApiApplication
import com.finmid.bank.TransactionDTO
import com.finmid.bank.model.Account
import com.finmid.bank.model.Transaction
import com.finmid.bank.repository.AccountRepository
import com.finmid.bank.repository.TransactionRepository
import com.finmid.interview.bank.TransactionController
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest

import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.body
import reactor.core.publisher.Mono
import java.util.*
import java.util.Optional.empty

@ContextConfiguration(classes = [ApiApplication::class])
@RunWith(SpringRunner::class)
@Tag("controller")
@AutoConfigureWebTestClient
@WebFluxTest(TransactionController::class)
class TransactionControllerTest() {

    @Autowired
    lateinit var webTestClient: WebTestClient

    @MockkBean(relaxed = true)
    private lateinit var accountRepository: AccountRepository

    @MockkBean(relaxed = true)
    private lateinit var transactionRepository: TransactionRepository

    private var account1 = Account(1,300.00)
    private var account2 = Account(2,50.00)

    @Nested
    inner class TransferSucceeds {
        @Test
        fun `should return HTTP Status ok for succeed transfers`() {
            every { accountRepository.findById(1) } returns Optional.of(account1)
            every { accountRepository.findById(2) } returns Optional.of(account2)
            every { accountRepository.save(any()) } returns account2
            every { transactionRepository.save(any()) } returns mockk<Transaction>()

            webTestClient.post().uri("/transaction")
                .body(Mono.just(TransactionDTO(2,1,300.00)))
                .exchange()
                .expectStatus().isNoContent
        }
    }

    @Nested
    inner class TransferFails {
        @Test
        fun `should return bad request in case of not money avaliable in the account`() {
            every { accountRepository.findById(1) } returns Optional.of(account1)
            every { accountRepository.findById(2) } returns Optional.of(account2)
            every { accountRepository.save(any()) } returns account2
            every { transactionRepository.save(any()) } returns mockk()

            webTestClient.post().uri("/transaction")
                .body(Mono.just(TransactionDTO(2,1,400.00)))
                .exchange()
                .expectStatus().isBadRequest
                .expectBody()
                .jsonPath("$.message").isEqualTo("Transfer is not possible, balance is not enough")
        }

        @Test
        fun `should return not found in case of invalid destinatary account`() {
            every { accountRepository.findById(1) } returns Optional.of(account1)
            every { accountRepository.findById(2) } returns empty()

            webTestClient.post().uri("/transaction")
                .body(Mono.just(TransactionDTO(2,1,400.00)))
                .exchange()
                .expectStatus().isNotFound
                .expectBody()
                .jsonPath("$.message").isEqualTo("Invalid account")
        }

        @Test
        fun `should return not found in case of invalid origin account`() {
            every { accountRepository.findById(1) } returns empty()
            every { accountRepository.findById(2) } returns Optional.of(account2)

            webTestClient.post().uri("/transaction")
                .body(Mono.just(TransactionDTO(2,1,400.00)))
                .exchange()
                .expectStatus().isNotFound
                .expectBody()
                .jsonPath("$.message").isEqualTo("Invalid account")
        }

        @Test
        fun `should return bad request in case of to and from are the same account`() {
            webTestClient.post().uri("/transaction")
                .body(Mono.just(TransactionDTO(1,1,400.00)))
                .exchange()
                .expectStatus().isBadRequest
                .expectBody()
                .jsonPath("$.message").isEqualTo("One or more inputs are invalid")
        }

        @Test
        fun `should return bad request in case of negative value`() {
            webTestClient.post().uri("/transaction")
                .body(Mono.just(TransactionDTO(1,2,-400.00)))
                .exchange()
                .expectStatus().isBadRequest
                .expectBody()
                .jsonPath("$.message").isEqualTo("One or more inputs are invalid")
        }
    }
}