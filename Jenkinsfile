jettyUrl = 'http://pipeline-demo-918a6c46-1.4de5d103.cont.dockerapp.io:32805/'

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
}

stage 'Tests'
parallel(longerTests: {
    runTests(servers, 15)
}, quickerTests: {
    runTests(servers, 10)
})

stage 'Build Docker Image'
node ("dockerhost") {
    def newApp = docker.build "lionelve/demo-war:${env.BUILD_TAG}"
    newApp.push()
}

stage name: 'Staging', concurrency: 1
node {
    servers.deploy 'staging'
}

input message: "Does ${jettyUrl}staging/ look good?"
try {
    checkpoint('Before production')
} catch (NoSuchMethodError _) {
    echo 'Checkpoint feature available in CloudBees Jenkins Enterprise.'
}

stage name: 'Production', concurrency: 1
node {
    sh "wget -O - -S ${jettyUrl}staging/"
    echo 'Production server looks to be alive'
    servers.deploy 'production'
    echo "Deployed to ${jettyUrl}production/"
}

def mvn(args) {
    sh "${tool 'M3'}/bin/mvn ${args}"
}

def runTests(servers, duration) {
    node {
        checkout scm
        servers.runWithServer {id ->
            mvn "-o -f sometests test -Durl=${jettyUrl}${id}/ -Dduration=${duration}"
        }
    }
}
