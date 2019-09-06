class orchestrator implements Serializable {

    def context
    boolean resultParallel
    String resultParallelMessage
    ParallelResultStrategy parallelResultStrategy = ParallelResultStrategy.AND
    OrchestrationExitCondition exitCondition = OrchestrationExitCondition.EXIT_AT_END

    def runJob(String jobId, Map vars) {
        this.@context.stage(jobId) { return getVerdict(buildJob(jobId, vars)) }
    }

    def runJobDependingOn(boolean verdict, String job1Id, String job2Id, Map vars) {
        if (verdict) {
            return runJob(job1Id, vars)
        }
        else {
            return runJob(job2Id, vars)
        }
    }

    def runJobsInParallel(Map vars, String... jobs) {
        initResultParallel()
        this.@context.stage(jobs.join(", ")) {
            def stepsForParallel = [:]
            for (int i = 0; i < jobs.length; i++) {
                def job = jobs[i]
                stepsForParallel["${job}"] = { -> buildParalleJob("${job}", vars) }
            }
            this.@context.parallel stepsForParallel

            boolean verdict = this.resultParallel
            if (!verdict && this.exitCondition == OrchestrationExitCondition.EXIT_ON_PARALLEL_FAILURE) {
                this.@context.error(this.resultParallelMessage)
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
        this.resultParallelMessage = ""
    }

    def updateResultParallel(String result) {
        boolean verdict = getVerdict(result)
        if (this.parallelResultStrategy == ParallelResultStrategy.AND) {
            this.resultParallel &= verdict
        }
        else if (this.parallelResultStrategy == ParallelResultStrategy.OR) {
            this.resultParallel |= verdict
        }
    }

    def buildParalleJob(String jobId, Map vars) {
        String result = buildJob(jobId, vars);
        updateResultParallel(result)

        if (this.resultParallelMessage != "") {
            this.resultParallelMessage += ", "
        }
        this.resultParallelMessage += (jobId + "=" + result)
    }

    def buildJob(String jobId, Map vars) { //map vars = [ip : "a.b.c.d", ...]
    	def params = []
	vars.each {key, val ->
		params += [$class: 'StringParameterValue', name: key, value: val]
	}
        def job = this.@context.build job: jobId, propagate: false, parameters: params
        return job.getResult()
    }

    def getVerdict(String result) {
        boolean verdict = (result == "SUCCESS")
        if (!verdict && this.exitCondition == OrchestrationExitCondition.EXIT_ON_FAIL) {
            this.@context.error(result)
        }
        return verdict
    }

    def setContext(ctx) {
        this.@context = ctx
    }

    def setParallelResultStrategy(strategy) {
        this.parallelResultStrategy = strategy
    }

    def setExitCondition(condition) {
        this.exitCondition = condition
    }
}
