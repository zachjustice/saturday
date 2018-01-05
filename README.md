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
* Separate table for access tokens
  * Cron job for clearing out old tokens
  * move entity.token field to access_tokens
  * logout invalidates token
  
* Use cookie scheme for auth instead of JWT
  
* Enable personal user photos. 
    * user_content table
    * topic_content has a foreign key to user_content.id
    * let's us backup all user photos without requiring each photo to have a topic
    
* Production quality emails

* Cohesive exception scheme
  * find<resource> methods shouldn't throw ResourceNotFoundExceptions()
  * ProcessingResourceException vs BussinessLogicException
  
Questions:
* Use obscured ids rather than autoincrementing id?
* use /me/<resource> instead of /entities/{id}/<resource>?
