jettyUrl = 'http://104.236.65.18'

def servers

def choice = new ChoiceParameterDefinition('AGENT', ['mock', 'docker', 'ec2'] as String[], 'Where do you want to build this?')
properties ([[$class: 'ParametersDefinitionProperty', parameterDefinitions: [choice]]])

stage 'Build App'
//def nodeLabel = input message: 'Where do you want to run this?', parameters: [choice]

echo "The agent is $AGENT"
node("$AGENT") {
   checkout scm
   servers = load 'servers.groovy'
   def mvnHome = tool 'M3'
   sh "${mvnHome}/bin/mvn clean package"
   dir('target') {stash name: 'war', includes: 'demo-war.war'}
   stash name: 'dockerfile', includes: 'Dockerfile' 
}

stage 'Tests'
parallel(longerTests: {
    runTests(servers, 15)
}, quickerTests: {
    runTests(servers, 10)
})

stage 'Build Docker Image'
node ("dockerhost") {
    unstash 'war'
    unstash 'dockerfile'
    def newApp = docker.build "lionelve/demo-war:${env.BUILD_NUMBER}"
    newApp.push()
}

stage name: 'Staging', concurrency: 1
node {
    servers.deploy 'staging', 8180, ${env.BUILD_NUMBER}
}

input message: "Does ${jettyUrl}:8180/staging/ look good?"
try {
    checkpoint('Before production')
} catch (NoSuchMethodError _) {
    echo 'Checkpoint feature available in CloudBees Jenkins Enterprise.'
}

stage name: 'Production', concurrency: 1
node {
    servers.deploy 'production', 8280, ${env.BUILD_NUMBER}
    echo "Deployed to ${jettyUrl}:8280/production/"
}

def mvn(args) {
    sh "${tool 'M3'}/bin/mvn ${args}"
}

def runTests(servers, duration) {
    node {
        echo 'Tests go here!'
    }
}
