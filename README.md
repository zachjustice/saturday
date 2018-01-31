Install the elastic beanstalk cli:
```
$ pip install awsebcli
$ echo "export PATH=~/Library/Python/2.7/bin:$PATH" >> ~/.bash_profile
```

*To deploy with Elastic Beanstalk*

Ensure these lines are included in .elasticbeanstalk.config.yml
```
deploy:
  artifact: build/libs/saturday-0.0.1-SNAPSHOT.jar
```

And these lines are added to application.yml. Nginx runs on port 80 and it expects our app to run on port 5000.
```
server:
  port: 5000
```

Create an environment if one is not already created
```
gradle build
eb create -s # single instance environment
eb console
```

To redeploy
```
eb deploy
```

TODO
* Gradle build scripts for ebs deploys

* Repeatable postman requests
  * requires delete resource routes

* S3 CORS
  * Current policy is too permissive

* Separate table for access tokens
  * Use cookie scheme for auth instead of JWT
  * Cron job for clearing out old tokens
  * move entity.token field to access_tokens
  * logout invalidates token

* Cohesive exception scheme
  * find<resource> methods shouldn't throw ResourceNotFoundExceptions()
  * ProcessingResourceException vs BussinessLogicException
  * Specific Exceptions rather than broad exceptions
    * Most processing resource exceptions are probably illegal arguments
    * Let callers handle specific scenarios.
    
##### BUGS
* emailConfirmed + isEmailConfirmed both on user object

Questions:
* Use obscured ids rather than autoincrementing id?
* use /me/<resource> instead of /entities/{id}/<resource>?

