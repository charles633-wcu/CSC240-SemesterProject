#!/bin/bash
echo "Starting all CSC240 project servers..."

# Start DataAPI
gnome-terminal -- bash -c "cd DataAPI && mvn exec:java -Dexec.mainClass=dataapi.Main; exec bash" &

sleep 3
# Start ClassAPI
gnome-terminal -- bash -c "cd ClassAPI && mvn exec:java -Dexec.mainClass=classapi.Main; exec bash" &

sleep 3
# Start UIAPI
gnome-terminal -- bash -c "cd UIAPI && mvn exec:java -Dexec.mainClass=uiapi.Main; exec bash" &

echo "All servers launched!"
echo "l"
