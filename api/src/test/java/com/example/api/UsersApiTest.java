package com.example.api;

import com.intuit.karate.junit5.Karate;

/**
 * Users API Karateテスト
 */
class UsersApiTest {

    @Karate.Test
    Karate testUsersAPI() {
        return Karate.run("classpath:features/users/UsersAPI.feature");
    }
}