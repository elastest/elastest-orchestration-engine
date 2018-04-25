class orchestrator implements Serializable {

    def context
    boolean resultParallel
    ParallelResultStrategy parallelResultStrategy = ParallelResultStrategy.AND
    OrchestrationExitCondition exitCondition = OrchestrationExitCondition.EXIT_ON_FAIL

    def runJob(String jobId) {
        this.@context.stage(jobId) { return buildJob(jobId) }
    }

    def runJobDependingOn(boolean result, String job1Id, String job2Id) {
        if (result) {
            return runJob(job1Id)
        }
        else {
            return runJob(job2Id)
        }
    }

    def runJobsInParallel(String... jobs) {
        initResultParallel()
        this.@context.stage(jobs.join(", ")) {
            def stepsForParallel = [:]
            for (int i = 0; i < jobs.length; i++) {
                def job = jobs[i]
                stepsForParallel["${job}"] = { -> buildParalleJob("${job}") }
            }
            this.@context.parallel stepsForParallel
            return this.resultParallel
        }
    }

    def initResultParallel() {
        if (this.parallelResultStrategy == ParallelResultStrategy.AND) {
            this.resultParallel = true
        }
        else if (this.parallelResultStrategy == ParallelResultStrategy.OR) {
            this.resultParallel = false
        }
    }

    def updateResultParallel(boolean result) {
        if (this.parallelResultStrategy == ParallelResultStrategy.AND) {
            this.resultParallel &= result
        }
        else if (this.parallelResultStrategy == ParallelResultStrategy.OR) {
            this.resultParallel |= result
        }
    }

    def buildParalleJob(String jobId) {
        updateResultParallel(buildJob(jobId))
    }

    def buildJob(String jobId) {
        def job = this.@context.build job: jobId, propagate: false
        String result = job.getResult()
        boolean verdict = (result == 'SUCCESS')
        if (!verdict && this.exitCondition == OrchestrationExitCondition.EXIT_ON_FAIL) {
            this.@context.error(result)
        }
        return verdict
    }

    def setContext(ctx){
        this.@context = ctx
    }

    def setParallelResultStrategy(strategy){
        this.parallelResultStrategy = strategy
    }
}
