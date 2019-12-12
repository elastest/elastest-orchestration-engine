class orchestrator implements Serializable {

    def context
    boolean resultParallel
    String resultParallelMessage
    ParallelResultStrategy parallelResultStrategy = ParallelResultStrategy.AND
    OrchestrationExitCondition exitCondition = OrchestrationExitCondition.EXIT_AT_END

    def packetLossArray = []
    def cpuBurstArray = []

    long compareTimeInMillis = 0
    Compare compare

    def runJob(String jobId, Map vars) {
        // Normal build
        if(packetLossArray.size() == 0 && cpuBurstArray.size() == 0) {
            this.@context.stage(jobId) { return getVerdict(buildJob(jobId, vars)) }
        }else { // Multi configuration Job
            // packetloss + cpuburst (currently does not work due to EIM limitations)
            if(packetLossArray.size() > 0 && cpuBurstArray.size() > 0) {
                packetLossArray.each { packetLossValue ->
                    cpuBurstArray.each { cpuBurstValue ->
                        def varsAux = vars.clone()
                        varsAux['ET_EIM_CONTROLLABILLITY_PACKETLOSS'] = packetLossValue
                        varsAux['ET_EIM_CONTROLLABILLITY_CPUBURST'] = cpuBurstValue

                        this.@context.stage(jobId) { return getVerdict(buildJob(jobId, varsAux)) }
                    }
                }
            }else {
                // packetloss only
                if(packetLossArray.size() > 0) {
                    packetLossArray.each { packetLossValue ->
                        def varsAux = vars.clone()
                        varsAux['ET_EIM_CONTROLLABILLITY_PACKETLOSS'] = packetLossValue
                        this.@context.stage(jobId) { return getVerdict(buildJob(jobId, varsAux)) }
                    }
                }else { // cpuburst only
                    cpuBurstArray.each { cpuBurstValue ->
                        def varsAux = vars.clone()
                        varsAux['ET_EIM_CONTROLLABILLITY_CPUBURST'] = cpuBurstValue
                        this.@context.stage(jobId) { return getVerdict(buildJob(jobId, varsAux)) }
                    }
                }
            }
        }
    }

    def runJob(String jobId) {
        runJob(jobId, [:])
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
                stepsForParallel["${job}"] = { -> buildParallelJob("${job}", vars) }
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

    def buildParallelJob(String jobId, Map vars) {
        String result = buildJob(jobId, vars).getResult();
        updateResultParallel(result)

        if (this.resultParallelMessage != "") {
            this.resultParallelMessage += ", "
        }
        this.resultParallelMessage += (jobId + "=" + result)
    }

    def buildJob(String jobId, Map vars) {
        //map vars = [ip : "a.b.c.d", ...]
        def params = []
        vars.each {key, val ->
            params += [$class: 'StringParameterValue', name: key, value: val]
        }
        def job = this.@context.build job: jobId, propagate: false, parameters: params
        return job
    }

    def getVerdict(jobBuild) {
        String result = jobBuild.getResult()
        boolean verdict = (result == "SUCCESS")

        // If check time comparison activated
        if(compare != null && compareTimeInMillis > 0) {
            long buildDuration = jobBuild.getDuration()
            println "buildTime: "+ buildDuration
            println "compareTimeInMillis: "+ compareTimeInMillis
            verdict = verdict && compare.eval(buildDuration, compareTimeInMillis)
        }

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

    def setPacketLoss(pLArr) {
        this.packetLossArray = pLArr
    }

    def setCpuBurst(cBArr) {
        this.cpuBurstArray = cBArr
    }

    def checkTime(Compare compare, time, TimeUnit timeUnit) {
        println "Check time activated with limit of " + time + " " + timeUnit
        long timeInMillis = timeUnit.convertToMillis(time)
        this.compare = compare
        this.compareTimeInMillis = timeInMillis
    }
}
