#!/usr/bin/env bash
oc new-app datagrid-service -p APPLICATION_USER=demo -p APPLICATION_PASSWORD=demo -p NUMBER_OF_INSTANCES=3 -e AB_PROMETHEUS_ENABLE=true
oc expose svc/datagrid-service-ping
oc expose svc/datagrid-service

# Create configmap for application
oc create -f configmap.yml
