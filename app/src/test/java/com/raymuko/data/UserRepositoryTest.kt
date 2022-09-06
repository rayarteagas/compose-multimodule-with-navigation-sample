package com.raymuko.data

import com.raymuko.models.User
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Test

class UserRepositoryTest {

    private val dispatcher = UnconfinedTestDispatcher()

    @Before
    fun prepare(){
        Dispatchers.setMain(dispatcher)
    }
    @Test
    fun testUserRepositoryGetAndCrudActionsWorksAsExpected() = runTest {
        val subject = UserRepository()

        val actualFlow = mutableListOf<List<User>>()

        //Test CRUD actions
        actualFlow.add(subject.users.first())
        subject.addOrUpdateUser(User(1, "John", "Carpenter", null))
        actualFlow.add(subject.users.first())
        subject.addOrUpdateUser(User(1, "John", "Builder", null))
        actualFlow.add(subject.users.first())
        subject.addOrUpdateUser(User(2, "Jane", "Carpenter", null))
        actualFlow.add(subject.users.first())
        subject.removeUser(1)
        actualFlow.add(subject.users.first())
        subject.addOrUpdateUser(User(ID_CREATE_USER, "NewJohn", "SuperCarpenter", null))
        actualFlow.add(subject.users.first())

        val expectedFlow = listOf(
            listOf(),
            listOf(User(1, "John", "Carpenter", null)),
            listOf(User(1, "John", "Builder", null)),
            listOf(User(1, "John", "Builder", null), User(2, "Jane", "Carpenter", null)),
            listOf(User(2, "Jane", "Carpenter", null)),
            listOf(User(2, "Jane", "Carpenter", null),User(3, "NewJohn", "SuperCarpenter", null)),
        )

        expectedFlow.forEachIndexed{ index, expected ->
            assertEquals("Inconsistency at index of expectedFlow list [$index]", expected, actualFlow[index])
        }

        //Test GET action
        val gottenUser = subject.getUser(3)
        assertEquals("Gotten user is not the expected one", User(3, "NewJohn", "SuperCarpenter", null), gottenUser)


    }
}