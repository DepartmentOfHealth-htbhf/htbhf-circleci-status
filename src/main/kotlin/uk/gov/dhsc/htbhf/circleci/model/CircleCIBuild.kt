package uk.gov.dhsc.htbhf.circleci.model

val FAILURE_OUTCOMES = listOf<String>("failed", "infrastructure_fail", "timedout")

class CircleCIBuild(
        private val lifecycle: String?, // queued, scheduled, not_run, not_running, running or finished
        private val status: String, // retried, canceled, infrastructure_fail, timedout, not_run, running, failed, queued, scheduled, not_running, no_tests, fixed, success
        private val outcome: String? // canceled, infrastructure_fail, timedout, failed, no_tests or success
) {
    fun stillRunning() = lifecycle == "queued" || lifecycle == "running"

    fun complete() = lifecycle == "finished" && outcome != "canceled"

    fun simpleStatus(): SimpleStatus {
        if (outcome == "success") {
            return SimpleStatus.SUCCESS
        }
        if (outcome in FAILURE_OUTCOMES) {
            return SimpleStatus.FAILURE
        }
        return SimpleStatus.UNKNOWN
    }
}