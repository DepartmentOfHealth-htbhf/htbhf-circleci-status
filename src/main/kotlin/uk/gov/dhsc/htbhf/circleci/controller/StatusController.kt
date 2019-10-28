package uk.gov.dhsc.htbhf.circleci.controller

import org.slf4j.Logger
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.*
import org.springframework.web.client.RestTemplate
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.*
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

    private val successResponse = getResponse("success")
    private val successRunningResponse = getResponse("success-running")
    private val failedResponse = getResponse("failed")
    private val failedRunningResponse = getResponse("failed-running")
    private val unknownResponse = getResponse("unknown")

    @GetMapping(value = ["/{project}"], produces = [MediaType.IMAGE_PNG_VALUE])
    fun getStatus(@PathVariable("project") project: String): ResponseEntity<ByteArray> {
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

    private fun getStatusImage(running: Boolean, status: SimpleStatus): ResponseEntity<ByteArray> {
        if (status == SimpleStatus.SUCCESS) {
            return if (running) successRunningResponse else successResponse
        }
        if (status == SimpleStatus.FAILURE) {
            return if (running) failedRunningResponse else failedResponse
        }
        return unknownResponse
    }

    private fun getResponse(name: String): ResponseEntity<ByteArray> {
        val imageFile = readImageFile(name)
        val headers = HttpHeaders()
        headers.cacheControl = "no-cache"
        return ResponseEntity(imageFile, headers, HttpStatus.OK)
    }

    private fun readImageFile(name: String) = StatusController::class.java.getResourceAsStream("/images/${name}.png").use { it.readBytes() }

}