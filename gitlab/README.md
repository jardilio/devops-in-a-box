A immutable deployed version of GitLab. The application version and configuration are all contained in source code and can then be deployed or rolled back. Volume is mounted separate for data and configurations are reapplied at initialization of the service.

Update configurations in [gitlab.rb](./gitlab.rb) template which will be run against local environment variables.