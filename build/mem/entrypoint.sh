#!/bin/bash

export CURL_CA_BUNDLE=/var/run/secrets/kubernetes.io/serviceaccount/ca.crt

TOKEN=$(cat /var/run/secrets/kubernetes.io/serviceaccount/token)
NS=$(cat /var/run/secrets/kubernetes.io/serviceaccount/namespace)
curl -H "Authorization: Bearer $TOKEN" https://kubernetes.default.svc/api/v1/namespaces/$NS/pods

# TODO read sharq custom resources and set up env vars for app to work

echo "launching quarkus app"
./application -Dquarkus.http.host=0.0.0.0