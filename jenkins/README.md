A immutable deployed version of Jenkins. The application version, plugins, and configuration are all contained in source code and can then be deployed or rolled back. Volume is mounted separate for workspace data and configurations are reapplied at initialization of the service.

* Add any new plugins to [plugins.txt](./plugins.txt) - this will install all requested plugins on image build
* Add any initialization scripts to [init.groovy.d](./init.groovy.d) - this will run initialization scripts on each start of the service
* Add any default groovy pipeline libraries to the [workflow-libs](./workflow-libs) directory - these libraries will automatically be available to any pipeline scripts
* Add any default jobs to the [jobs](./jobs) directory