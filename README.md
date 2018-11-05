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

To setup the jenkins server
* Create a JenkinsAccess permission for EC2 services (Role Type 'EC2') with the 'AWSCodePipelineCustomActionAccess' policy
* Setup an ec2 instance with ports 80 and 22 accessible from your ip address 
* Attach the JenkinsAccess permission you just created to the new EC2 instance
* Setup the jenkins build server on the EC2 instance with the following commands
```
# install java 8 and maven 3.5
wget http://www-eu.apache.org/dist/maven/maven-3/3.5.3/binaries/apache-maven-3.5.3-bin.tar.gz -C /opt/
sudo tar xzvf apache-maven-3.3.3-bin.tar.gz -C /opt/ 
rm apache-maven-3.3.3-bin.tar.gz

sudo yum install java-1.8.0
sudo yum remove java-1.7.0-openjdk

# setup jenkins
sudo wget -O /etc/yum.repos.d/jenkins.repo http://pkg.jenkins-ci.org/redhat/jenkins.repo
sudo rpm --import https://jenkins-ci.org/redhat/jenkins-ci.org.key
sudo yum install -y jenkins

# confirm jenkins is started
sudo service jenkins status

# start jenkins if its stopped
sudo service jenkins start

```

Jenkins has a default port of TCP/8080, but we’ll use iptables to redirect port 80 to port 8080 and allow local connections.

```
sudo iptables -A PREROUTING -t nat -i eth0 -p tcp --dport 80 -j REDIRECT --to-port 8080
sudo iptables -t nat -I OUTPUT -p tcp -o lo --dport 80 -j REDIRECT --to-ports 8080
```
