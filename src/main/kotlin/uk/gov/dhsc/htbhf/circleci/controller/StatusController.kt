package uk.gov.dhsc.htbhf.circleci.controller

import org.slf4j.Logger
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import org.springframework.web.client.RestTemplate
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpMethod
import uk.gov.dhsc.htbhf.circleci.model.CircleCIBuild
import uk.gov.dhsc.htbhf.circleci.model.SimpleStatus


@RestController
@RequestMapping(value = ["/"])
class StatusController(private val restTemplate: RestTemplate, private val logger: Logger) {

    @Value("\${circleci.base-url}")
    private val baseUrl: String? = null

    @Value("\${circleci.organisation}")
    private val organisation: String? = null

    @Value("\${circleci.branch}")
    private val branch: String? = null

    @Value("\${circleci.token}")
    private val apiToken: String? = null

    private val successPng = readImageFile("success")
    private val successRunningPng = readImageFile("success-running")
    private val failedPng = readImageFile("failed")
    private val failedRunningPng = readImageFile("failed-running")
    private val unknownPng = readImageFile("unknown")

    @GetMapping(value = ["/{project}"], produces = [MediaType.IMAGE_PNG_VALUE])
    @ResponseBody
    fun getStatus(@PathVariable("project") project: String): ByteArray {
        val builds = getCircleCIBuildHistory(project)

        val running = builds.any(CircleCIBuild::stillRunning)
        val status = builds.filter(CircleCIBuild::complete).firstOrNull()?.simpleStatus() ?: SimpleStatus.UNKNOWN

        return getStatusImage(running, status)
    }

    private fun getCircleCIBuildHistory(project: String): List<CircleCIBuild> {
        val response = restTemplate.exchange<List<CircleCIBuild>>(
                "$baseUrl/$organisation/$project/tree/$branch?circle-token=$apiToken&limit=10&shallow=true",
                HttpMethod.GET,
                null,
                object : ParameterizedTypeReference<List<CircleCIBuild>>() {})
        return response.body ?: emptyList()
    }

    private fun getStatusImage(running: Boolean, status: SimpleStatus): ByteArray {
        if (status == SimpleStatus.SUCCESS) {
            return if (running) successRunningPng else successPng
        }
        if (status == SimpleStatus.FAILURE) {
            return if (running) failedRunningPng else failedPng
        }
        return unknownPng
    }

    private fun readImageFile(name: String) = StatusController::class.java.getResourceAsStream("/images/${name}.png").use { it.readBytes() }

}