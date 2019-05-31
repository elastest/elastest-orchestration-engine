@Library('OrchestrationLib') _

// Config
orchestrator.setContext(this)
orchestrator.setParallelResultStrategy(ParallelResultStrategy.OR)
orchestrator.setExitCondition(OrchestrationExitCondition.EXIT_ON_FAIL)

// Graph
def result1 = orchestrator.runJob('myjob1')
orchestrator.runJobDependingOn(result1, 'myjob2', 'myjob3')
def result3 = orchestrator.runJobsInParallel('myjob4', 'myjob5')

if (result3) {
   orchestrator.runJob('myjob6')
   orchestrator.runJob('myjob7')
}
else {
   orchestrator.runJob('myjob8')
}
