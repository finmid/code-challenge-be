package com.finmid.bank.Transaction

import com.finmid.bank.model.Account
import com.finmid.bank.model.Transaction
import com.finmid.bank.repository.AccountRepository
import com.finmid.bank.repository.TransactionRepository
import com.finmid.bank.service.TransactionService
import com.finmid.interview.bank.InvalidAccountException
import com.finmid.interview.bank.InvalidBalanceException
import io.mockk.every

import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*
import java.util.Optional.empty

class TransacationServiceTest() {

    private lateinit var accountRepository: AccountRepository
    private lateinit var transactionRepository : TransactionRepository
    private lateinit var transactionService: TransactionService

    private var account = Account(1,300.00)

    @BeforeEach
    fun beforeEach() {
        accountRepository = mockk()
        transactionRepository = mockk()

        every { accountRepository.save(any()) } returns account
        every { accountRepository.findById(any()) } returns Optional.of(account)

        transactionService = TransactionService(accountRepository = accountRepository,
                                                transactionRepository = transactionRepository)
    }

    @Test
    fun `should execute a transfer from an account to another`() {
        every { transactionRepository.save(any()) } returns Transaction(1,1,2,300.00)
        transactionService.executeTransaction(1,2,100.00)

        verify(exactly = 1) { transactionRepository.save(any()) }
    }

    @Test
    fun `should throw Invalid account exception in case of account does not exist`() {
        every { accountRepository.findById(any()) } returns empty()

        assertThrows<InvalidAccountException> {
            transactionService.executeTransaction(1,2,100.00)
        }
    }

    @Test
    fun `should throw Invalid balance exception in case of amount is bigger than the balance`() {
        every { transactionRepository.save(any()) } returns Transaction(1,1,2,300.00)
        assertThrows<InvalidBalanceException> {
            transactionService.executeTransaction(1, 2, 800.00)
        }
    }

}