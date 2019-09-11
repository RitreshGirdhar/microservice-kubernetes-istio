# Microservice kubernetes deployment + persistent logs gathering  

### Agenda

1.To show you how to deploy microservices on kubernetes  
2.Configure gateway to expose microservices via ingress


Steps to run this project
====== 
#### Step 1
```
cd ms1
mvn clean install
docker build -t k8ms1:latest .
cd ../ms2
mvn clean install
docker build -t k8ms2:latest .
cd ../
```

Above code will help you in building docker images k8ms1 & k8ms2 

#### Step 2 
Push this docker images to docker registry 

```
docker tag <id> <docker-registry-userid>/<repository>:<tag>
docker push <docker-registry-userid>/<repository>:<tag>
```
replace <id> with container id that you could get by running 
``` 
docker image ls --all
```
 
#### Step 3

``` 
cd deployment 
kubectl apply -f deployment-with-pvc.yaml
```

above command will create MS1 & MS2 microservices deployment with replica-set value 2 that means 
it will create two pods. 

In `deployment-with-pvc.yaml` file i have mentioned volumeMounts that means MS2 /tmp/app.log will be map with the 
persistentVolumeClaim. 
######[please read concept of persistentVolume & persistentVolumeClaim first]

```
volumeMounts:
            - mountPath: "/tmp/logs"
              name: k8ms2-mt-volume
      volumes:
        - name: k8ms2-mt-volume
          persistentVolumeClaim:
            claimName: task-pv-claim
``` 

#### Step 4 
Create Gateway & virtual Service. You have to define the list of api's you want to expose

``` 
kubectl apply -f k8-gateway.yaml
```

In `k8-gateway.yaml` we are creating Gateway and in this i am using istio - ingressgateway 

You have to set up istio service-mesh first , refer [http://istio.io/]

```
selector:
    istio: ingressgateway
``` 

For testing i am using minikube. You have to create persistant volume first 

```
minikube ssh
sudo mkdir /mnt/data
exit
```

Then run below command 
```
kubectl apply -f https://k8s.io/examples/pods/storage/pv-volume.yaml
```
pv-volume.yaml will command kubernetes to create PersistentVolume of capacity 10GB 
with ReadWriteOnce permission. Ideally in production environemnt, Administrator will provide you 
PersistentVolume which your pod will share by PersistentVolumeClaim .

```
apiVersion: v1
   kind: PersistentVolume
   metadata:
     name: task-pv-volume
     labels:
       type: local
   spec:
     storageClassName: manual
     capacity:
       storage: 10Gi
     accessModes:
       - ReadWriteOnce
     hostPath:
       path: "/mnt/data"
```

Let's create PersistentVolumeClaim
```
kubectl apply -f https://k8s.io/examples/pods/storage/pv-claim.yaml
```
`pv-claim.yaml` is a request to get 3GB disk resource with ReadWriteOnce accessModes.  
```

apiVersion: v1
kind: PersistentVolumeClaim
metadata:
  name: task-pv-claim
spec:
  storageClassName: manual
  accessModes:
    - ReadWriteOnce
  resources:
    requests:
      storage: 3Gi
```

Let's test our application.

```
echo $(minikube ip):$(kubectl -n istio-system get service istio-ingressgateway -o jsonpath='{.spec.ports[?(@.name=="http2")].nodePort}')

Output 
192.168.99.103:31380

```

Open browser and hit [ http://192.168.99.103:31380/api2] you will get web page with hello
message. 

Invoke [http://192.168.99.103:31380/invoke3rdparty/api2] it will invoke 3rd party test api in this 
api code i have added some random logic to prove that in case you deploy this service with replication factor >1 then you will 
get merged logs of each container in persistant volume 

```$xslt
minikube ssh 
cd /mnt/data 
ls -lrt 
```

```$xslt
Output 
app.log
```

Above app.log contains merged logs of each pod. 

This would not be the best scenario to capture Logs in Kubernetes environment. Open for discussion :) 
Refer [https://kubernetes.io/docs/concepts/cluster-administration/logging/] for Logging architecture in k8 