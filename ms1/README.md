# Microservice monitoring with prometheus + Grafana  

Steps to run this project
====== 
#### Step 1
```
 mvn spring-boot:run
```

We have enabled prometheus actuator in this microservice. Added micrometer in pom 
```$xslt
        <dependency>
            <groupId>io.micrometer</groupId>
            <artifactId>micrometer-registry-prometheus</artifactId>
        </dependency>
```

Do check [http://localhost:8811/actuator/prometheus] , it will show prometheus metrics 

#### Step 2 
create microservice-prometheus.yml config file 
```
global:
  scrape_interval:     15s 
  evaluation_interval: 15s 
rule_files:
scrape_configs:
  - job_name: 'prometheus'
    static_configs:
    - targets: ['127.0.0.1:9090']

  - job_name: 'microservices-1'
    metrics_path: '/actuator/prometheus'
    scrape_interval: 5s
    static_configs:
    - targets: ['Machine-IP(localhost will not work as we are running docker):8811']
```

#### Step 3
Start prometheus via docker , use below command. Replace <path> with the actual path
``` 
docker run -d --name=prometheus -p 9090:9090 -v  /<path>/microservice-prometheus.yml:/etc/prometheus/prometheus.yml prom/prometheus --config.file=/etc/prometheus/prometheus.yml
```

Open [http://localhost:9090/] check Targets , you will be able to see 2 services like this

 ![Alt text](images/Prometheus-1.png?raw=true "Targets")
 
 Hit [http://localhost:8811/api1/] multiple time and try some graphs  
 ![Alt text](images/Prometheus-2.png?raw=true "Graphs")
 
#### Step 4
Run grafana 

```
docker run -d --name=grafana -p 3000:3000 grafana/grafana 
```
use admin/admin - default credential
 
#### Step 5

Configure Grafana and point to Prometheus datasource. You will be able to configure
Dashboard with multiple panel like this.

 ![Alt text](images/grafana-panel.png?raw=true "Grafana")
 