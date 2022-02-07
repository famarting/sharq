
MEM_COMPONENT_IMAGE=quay.io/famargon/sharq-platform-component-mem:latest
KAFKA_COMPONENT_IMAGE=quay.io/famargon/sharq-platform-component-kafka-proxy:latest

build-mem:
	docker build -t quay.io/famargon/sharq-mem-k8s:latest -f build/mem/Dockerfile .

push-mem:	
	docker push quay.io/famargon/sharq-mem-k8s:latest

native-build-mem:
	mvn install -Dquarkus.package.type=native -pl components/mem/