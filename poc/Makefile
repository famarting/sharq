AUTHORINO_REPO?=https://github.com/Kuadrant/authorino.git
AUTHORINO_BRANCH?=v0.3.0-pre

kind_start:
	kind create cluster --config kind.yaml
	$(MAKE) install_nginx_ingress
# $(MAKE) install_certmanager

# using nginx ingress version v0.46.0
install_nginx_ingress:
	kubectl apply -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/controller-v0.46.0/deploy/static/provider/kind/deploy.yaml
	kubectl patch deployment ingress-nginx-controller -n ingress-nginx --type=json -p '[{"op":"add","path":"/spec/template/spec/containers/0/args/-","value":"--enable-ssl-passthrough"}]'

install_certmanager:
	kubectl apply -f https://github.com/jetstack/cert-manager/releases/download/v1.4.0/cert-manager.yaml
	kubectl delete mutatingwebhookconfiguration.admissionregistration.k8s.io/cert-manager-webhook
	kubectl delete validatingwebhookconfigurations.admissionregistration.k8s.io/cert-manager-webhook
	kubectl -n cert-manager wait --timeout=300s --for=condition=Available deployments --all

kind_stop:
	kind delete cluster

get_install_manifests: get_authorino_manifests

get_authorino_manifests:
	git clone -b $(AUTHORINO_BRANCH) $(AUTHORINO_REPO) tmp_authorino
	cp -r tmp_authorino/install authorino/install
	cp -r tmp_authorino/deploy authorino/deploy
	rm -rf tmp_authorino

install_infra:
	$(MAKE) install_authorino
	$(MAKE) install_eventing

#TODO install everything in a custom namespace other than authorino
install_authorino:
	kustomize build authorino/install | kubectl apply -f -
	kubectl create namespace authorino | true
	kustomize build authorino/deploy/base/certmanager | kubectl apply -f -
	kustomize build authorino/deploy/overlays/namespaced | kubectl apply -f -
	kubectl -n authorino wait --timeout=300s --for=condition=Available deployments --all
	kubectl ns authorino

# https://knative.dev/docs/admin/install/eventing/install-eventing-with-yaml/
install_eventing:
	kubectl apply -f https://github.com/knative/eventing/releases/download/v0.24.0/eventing-crds.yaml
	kubectl apply -f https://github.com/knative/eventing/releases/download/v0.24.0/eventing-core.yaml
	kubectl apply -f https://github.com/knative/eventing/releases/download/v0.24.0/in-memory-channel.yaml
	kubectl apply -f https://github.com/knative/eventing/releases/download/v0.24.0/mt-channel-broker.yaml

install_atomix:
	kubectl apply -f https://raw.githubusercontent.com/atomix/atomix-controller/v0.5.10/deploy/atomix-controller.yaml -n kube-system
# kubectl apply -f https://raw.githubusercontent.com/atomix/atomix-raft-storage/v0.8.3/deploy/atomix-raft-storage-plugin.yaml
# kubectl apply -f https://raw.githubusercontent.com/atomix/atomix-raft-storage/v0.8.3/deploy/atomix-raft-storage.yaml
	kubectl apply -f https://raw.githubusercontent.com/atomix/atomix-raft-storage/v0.8.3/deploy/raft-storage-controller.yaml -n kube-system

install_kubevela:
	helm repo add kubevela https://kubevelacharts.oss-accelerate.aliyuncs.com/core
	helm repo update
	kubectl create namespace vela-system
	helm install -n vela-system kubevela kubevela/vela-core

deploy_app:
	kubectl apply -f authorino/envoy.yaml
	kubectl apply -f authorino/envoy-config.yaml
	kubectl apply -f authorino/simple-api-key-security.yaml
	kubectl apply -f authorino/talker-api-example-app.yaml
	kubectl apply -f eventing/broker.yaml -n authorino
	kubectl apply -f eventing/broker-ingress.yaml -n knative-eventing
	kubectl apply -f eventing/talker-api-trigger.yaml -n authorino
	kubectl apply -f eventing/ping-source.yaml -n authorino
	kubectl apply -f eventing/event-display.yaml -n authorino
	kubectl apply -f eventing/event-display-trigger.yaml -n authorino
	# FINISH DATABASE
	kubectl apply -f atomix/database.yaml -n kube-system

test_api_key:
	http http://talker-api-authorino.127.0.0.1.nip.io/ "Authorization:APIKEY ndyBzreUzF4zqDQsqSPMHkRhriEOtcRx"