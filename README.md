[![License badge](https://img.shields.io/badge/license-Apache2-green.svg)](http://www.apache.org/licenses/LICENSE-2.0)
[![Documentation badge](https://img.shields.io/badge/docs-latest-brightgreen.svg)](http://elastest.io/docs/)

[![][ElasTest Logo]][ElasTest]

Copyright © 2017-2019 [Universidad Rey Juan Carlos]. Licensed under [Apache 2.0 License].

Elastest Orchestration Engine
=============================

The ElasTest Orchestration Engine (EOE) is the component responsible of implementing the concept of orchestration within Elastest, understood as a novel way to select, order, and execute a group of TJobs. To that aim, we have leveraged the pipelines Jenkins notation for the topology generation by means of a [Jenkins shared library]. This library exposes a simple API to orchestrate testing jobs. The orchestrator library API is summarized in the following table:

Method | Description
--- | ---
`runJob(String jobId)` |  Method to run a job given its identifier (*jobId*).This method returns a boolean value: `true` if the execution of the job finishes correctly and `false` if fails
`runJobDependingOn(boolean verdict, String job1Id, String job2Id)` |  Method allows to run one job given a boolean value (typically a verdict from another job). This boolean value is passes in the first argument (called verdict in the method signature). If this value job with identifier `job1Id` is executed. Otherwise it is executed `job2Id`
`runJobsInParallel(String... jobs)` |  This method allows to run a set of jobs in parallel. The jobs identifier are passes using a variable number of arguments (*varargs*)

Moreover, the exit condition for the orchestrated job can be also configured:

Method | Description
--- | ---
`EXIT_AT_END` |  The orchestration finishes at the end (option by default)
`EXIT_ON_FAIL` |  The orchestration finishes when any of the TJobs fail
`EXIT_ON_PARALLEL_FAILURE` |  The orchestration finishes when any a set of parallel TJobs fail

Finally, the verdict of a group of parallel jobs can be also configured:

Method | Description
--- | ---
`AND` |  The verdict of a set of jobs executed in parallel is `true` only if all the jobs finish correctly
`OR` |  The verdict of parallel jobs is `true` when at least one of the jobs finishes correctly

Other configurations:

Method | Description | Use Example
--- | --- | ---
`setPacketLoss` | If a SuT instrumented By ElasTest through EIM is used, one or more packet loss values can be set. If more than one is specified, the job will be run as many times as there are values. | setPacketLoss(['0.01', '0.02'])
`setCpuBurst` | If a SuT instrumented By ElasTest through EIM is used, one or more cpu burst (stress) values can be set. If more than one is specified, the job will be run as many times as there are values. | setCpuBurst(['0.2'])
`checkTime` | Allows to check if an execution lasts less (LessThan) or more (GreaterThan) than the indicated time (In milliseconds, seconds, minutes, etc.). | checkTime(Compare.LessThan, 60, TimeUnit.SECONDS)

At the moment, only packet loss and cpu burst can be set separately, so the two cannot be set at the same time.

How to install
-----------------
- Navigate to *Manage Jenkins* in your Jenkins.
- Click to *System Configuration*
- Add new library in *Global Pipeline Libraries* with name **OrchestrationLib** and default version **master**
- Select **Modern SCM**
- Select **GitHub**
- Owner: **elastest**
- Repository: **elastest-orchestration-engine**
- Save changes

What is ElasTest
-----------------

This repository is part of [ElasTest], which is a flexible open source testing
platform aimed to simplify the end-to-end testing processes for different types
of applications, including web and mobile, among others.

The objective of ElasTest is to provide advance testing capabilities aimed to
increase the scalability, robustness, security and quality of experience of
large distributed systems. All in all, ElasTest will make any software
development team capable of delivering software faster and with fewer defects.

Documentation
-------------

The ElasTest project provides detailed [documentation][ElasTest Doc] including
tutorials, installation and development guide.

Source
------

Source code for other ElasTest projects can be found in the [GitHub ElasTest
Group].

News
----

Check the [ElasTest Blog] and follow us on Twitter [@elastestio][ElasTest Twitter].

Issue tracker
-------------

Issues and bug reports should be posted to the [GitHub ElasTest Bugtracker].

Licensing and distribution
--------------------------

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

Contribution policy
-------------------

You can contribute to the ElasTest community through bug-reports, bug-fixes,
new code or new documentation. For contributing to the ElasTest community,
you can use GitHub, providing full information about your contribution and its
value. In your contributions, you must comply with the following guidelines

* You must specify the specific contents of your contribution either through a
  detailed bug description, through a pull-request or through a patch.
* You must specify the licensing restrictions of the code you contribute.
* For newly created code to be incorporated in the ElasTest code-base, you
  must accept ElasTest to own the code copyright, so that its open source
  nature is guaranteed.
* You must justify appropriately the need and value of your contribution. The
  ElasTest project has no obligations in relation to accepting contributions
  from third parties.
* The ElasTest project leaders have the right of asking for further
  explanations, tests or validations of any code contributed to the community
  before it being incorporated into the ElasTest code-base. You must be ready
  to addressing all these kind of concerns before having your code approved.

Support
-------

The ElasTest project provides community support through the [ElasTest Public
Mailing List] and through [StackOverflow] using the tag *elastest*.


<p align="center">
  <img src="http://elastest.io/images/logos_elastest/ue_logo-small.png"><br>
  Funded by the European Union
</p>

[Apache 2.0 License]: http://www.apache.org/licenses/LICENSE-2.0
[ElasTest]: http://elastest.io/
[ElasTest Blog]: http://elastest.io/blog/
[ElasTest Doc]: http://elastest.io/docs/
[ElasTest Logo]: http://elastest.io/images/logos_elastest/elastest-logo-gray-small.png
[ElasTest Public Mailing List]: https://groups.google.com/forum/#!forum/elastest-users
[ElasTest Twitter]: https://twitter.com/elastestio
[GitHub ElasTest Group]: https://github.com/elastest
[GitHub ElasTest Bugtracker]: https://github.com/elastest/bugtracker
[StackOverflow]: http://stackoverflow.com/questions/tagged/elastest
[Universidad Rey Juan Carlos]: https://www.urjc.es/
[Jenkins shared library]: https://jenkins.io/doc/book/pipeline/shared-libraries/
