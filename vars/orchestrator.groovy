class orchestrator implements Serializable {

    def context
    boolean resultParallel
    String resultParallelMessage
    ParallelResultStrategy parallelResultStrategy = ParallelResultStrategy.OR
    OrchestrationExitCondition exitCondition = OrchestrationExitCondition.EXIT_ON_PARALLEL_FAILURE

    def runJob(String jobId) {
        this.@context.stage(jobId) { return getVerdict(buildJob(jobId)) }
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

            boolean verdict = this.resultParallel
            if (!verdict && this.exitCondition == OrchestrationExitCondition.EXIT_ON_PARALLEL_FAILURE) {
                this.@context.error(result)
            }

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
        this.resultParallelMessage = ''
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
        String result = buildJob(jobId);
        updateResultParallel(getVerdict(result))
        if (this.resultParallelMessage != '') {
            this.resultParallelMessage += ', '
        }
        this.resultParallelMessage += jobId "=" + result
    }

    def buildJob(String jobId) {
        def job = this.@context.build job: jobId, propagate: false
        return job.getResult()
    }

    def getVerdict(String result) {
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
