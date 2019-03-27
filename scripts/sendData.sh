#!/usr/bin/env bash
#http://mission-service-missionx.apps.copenhagen-62bc.openshiftworkshop.com/api/missions
#http://localhost:8080/api/missions

#  { "id": "1", "incidentId": "1", "responderId": "1", "responderStartLat": 34.16877, "responderStartLong": 34.16877, "incidentLat": 34.16877, "incidentLong": 34.16877, "destinationLat": 34.16877, "destinationLong": 34.16877, "responderLocationHistory": [ { "lat": 34.16877, "long": 34.16877, "timestamp": 2434546 } ], "status": "Assigned" }

curl --header "Content-Type: application/json" \
  --request PUT \
  --data @temp.json \
  http://mission-service-mission-service.apps.copenhagen-62bc.openshiftworkshop.com/api/missions
