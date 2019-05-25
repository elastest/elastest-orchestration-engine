@Library('OrchestrationLib') _

// Config
orchestrator.setContext(this)
orchestrator.setExitCondition(OrchestrationExitCondition.EXIT_AT_END)

// Graph
orchestrator.runJob('full-teaching-functional-smoke')
orchestrator.runJobsInParallel('full-teaching-functional-session',
   'full-teaching-functional-user', 'full-teaching-functional-comment')
