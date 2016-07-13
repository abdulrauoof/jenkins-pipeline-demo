def deploy(id, port, ver) {
 sh "docker ps -a --format=\"{{ .Names }}\" --filter \"name=${id}\" > RUNNING"
 isRunning=readFile('RUNNING')
 if(isRunning?.trim()) {
     undeploy id
 }
 echo "Running image tag ${ver} as ${id}"
 sh "docker run --name ${id} -d -p ${port}:8080 lionelve/demo-war:${ver}"
}

def undeploy(id) {
    echo "undeploying ${id}"
    sh "docker stop ${id}"
    sh "docker rm ${id}"
}


this
