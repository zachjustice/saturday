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
* Topic invites have a accepted/rejected column
* Adding a topic member first checks if an invite for that member and topic exists
* * Adding a topic member checks the sender is authenticated as the invitee
* * Deleting a topic member checks the sender is authenticated as the inviter
* Endpoint checks permissions
* s3 bucket and key instead of url

As a topic owner I can invite someone to a topic I own
As a topic owner I can revoke a previously issues invite
As a user, I can accept an invitation to a topic
As a user, I can delete an invitiation to a topic
