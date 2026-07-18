package com.example.bff

import com.example.bff.presentation.config.CustomAuthenticationSuccessHandler
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.web.reactive.function.client.WebClient // 追加

@SpringBootTest(properties = ["KEYCLOAK_CLIENT_SECRET=dummy-secret"])
class BffApplicationTests {

  @Autowired
  private lateinit var context: ApplicationContext

  @TestConfiguration
  class TestConfig {
    @Bean
    fun customAuthenticationSuccessHandler(): CustomAuthenticationSuccessHandler {
      return Mockito.mock(CustomAuthenticationSuccessHandler::class.java)
    }

    @Bean
    fun clientRegistrationRepository(): ReactiveClientRegistrationRepository {
      return Mockito.mock(ReactiveClientRegistrationRepository::class.java)
    }

    // 今回不足していた WebClient.Builder をテストコンテキストに追加
    @Bean
    fun webClientBuilder(): WebClient.Builder {
      // モックではなく本物のBuilderを返すことで、設定クラス内での .build() 呼び出しエラーを防ぐ
      return WebClient.builder()
    }
  }
  
  @Test
  @DisplayName("アプリケーションコンテキストが正常にロードされ、セキュリティチェーンが取得できること")
  fun contextLoads() {
    val filterChain = context.getBean(SecurityWebFilterChain::class.java)
    assertThat(filterChain).isNotNull
  }
}