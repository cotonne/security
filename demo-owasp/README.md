To start the API:

```bash
$ cd sqli-zap 
$ mvn spring-boot:run
$ firefox http://localhost:8080/swagger-ui.html
```

To start a scan:

```bash
$ export LOCAL_IP=192.168.1.16
$ docker run --rm owasp/zap2docker-weekly zap-api-scan.py -t http://$LOCAL_IP:8080/v3/api-docs -f openapi
```

To exclude false positives:

```bash
$ cat config-file
10021	IGNORE	AAA
40029	OUTOFSCOPE	http://*/employees*
$ docker run --volume $PWD/config:/zap/wrk --rm owasp/zap2docker-weekly zap-api-scan.py -t http://$LOCAL_IP:8080/v3/api-docs -f openapi -c config-file
```
