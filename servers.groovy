def deploy(id, port, ver) {
 sh "sudo docker inspect -f {{.State.Running}} ${id} > RUNNING"
 isRunning=readFile('RUNNING')
 if(isRunning == 'true') {
     undeploy id
 }
 echo "Running image tag ${ver} as ${id}"
 sh "sudo docker run --name ${id} -d -p ${port}:8080 lionelve/demo-war:${ver}
}

def undeploy(id) {
    echo "undeploying ${id}"
    sh "sudo docker stop ${id}"
    sh "sudo docker rm ${id}"
}


this
