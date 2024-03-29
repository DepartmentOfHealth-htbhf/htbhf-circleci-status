package uk.gov.dhsc.htbhf.circleci

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.InjectionPoint
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Scope
import org.springframework.web.client.RestTemplate

@Configuration
class Config {

    @Bean
    fun getObjectMapper() = ObjectMapper().registerModule(KotlinModule())

    @Bean
    fun getRestTemplate() = RestTemplate()

    @Bean
    @Scope("prototype")
    fun logger(injectionPoint: InjectionPoint) : Logger {
        return LoggerFactory.getLogger(injectionPoint.methodParameter?.containingClass)
    }
}