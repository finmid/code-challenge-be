package com.finmid.bank.Account

import com.finmid.bank.AccountDTO
import com.finmid.bank.ApiApplication
import com.finmid.bank.model.Account
import com.finmid.bank.repository.AccountRepository
import com.finmid.bank.repository.TransactionRepository
import com.finmid.interview.bank.AccountController
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
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
@WebFluxTest(AccountController::class)
class AccountControllerTest() {

    @Autowired
    lateinit var webTestClient: WebTestClient

    @MockkBean(relaxed = true)
    private lateinit var accountRepository: AccountRepository

    @MockkBean(relaxed = true)
    private lateinit var transactionRepository: TransactionRepository

    private var account = Account(1,300.00)

    @Nested
    inner class CreateAccount{
        @Nested
        inner class Success {
            @Test
            fun `should return HTTP CREATED Status and return the account data`() {
                every { accountRepository.save(any()) } returns account

                webTestClient.post().uri("/account")
                    .body(Mono.just(AccountDTO(300.00)))
                    .exchange()
                    .expectStatus().isCreated
            }
        }
        @Nested
        inner class Exception {
            @Test
            fun `should return bad request when balance is set as negative`() {
                webTestClient.post().uri("/account")
                    .body(Mono.just(AccountDTO(-300.00)))
                    .exchange()
                    .expectStatus().isBadRequest
                    .expectBody()
                    .jsonPath("$.message").isEqualTo("Invalid balance input")
            }
        }
    }

    @Nested
    inner class GetAccount{
        @Nested
        inner class Success {
            @Test
            fun `should return HTTP OK Status and return the account data`() {
                every { accountRepository.findById(any()) } returns Optional.of(account)

                webTestClient.get().uri("/account/1")
                    .exchange()
                    .expectStatus().isOk
                    .expectBody()
                    .jsonPath("$.balance").isEqualTo(300.00)
            }
        }
        @Nested
        inner class Exception {
            @Test
            fun `should return not found when account not exist`() {
                @Test
                fun `should return HTTP OK Status and return the account data`() {
                    every { accountRepository.findById(any()) } returns Optional.empty()

                    webTestClient.get().uri("/account/1")
                        .exchange()
                        .expectStatus().isNotFound
                }
            }
        }
    }

    @Nested
    inner class UpdateAccount{
        @Nested
        inner class Success {
            @Test
            fun `should return HTTP NO_CONTENT for success update`() {
                every { accountRepository.findById(any()) } returns Optional.of(account)
                every { accountRepository.save(any()) } returns account

                webTestClient.put().uri("/account/1")
                    .body(Mono.just(AccountDTO(200.00)))
                    .exchange()
                    .expectStatus().isNoContent
            }
        }
        @Nested
        inner class Exception {
            @Test
            fun `should return not found when account not exist`() {
                every { accountRepository.findById(any()) } returns empty()

                webTestClient.put().uri("/account/1")
                    .body(Mono.just(AccountDTO(200.00)))
                    .exchange()
                    .expectStatus().isNotFound
            }

            @Test
            fun `should return bad request when balance is set as negative`() {
                webTestClient.put().uri("/account/1")
                    .body(Mono.just(AccountDTO(-300.00)))
                    .exchange()
                    .expectStatus().isBadRequest
                    .expectBody()
                    .jsonPath("$.message").isEqualTo("Invalid balance input")
            }
        }
    }
}
