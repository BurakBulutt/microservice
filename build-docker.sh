#!/bin/bash

projects=$(find . -maxdepth 1 -mindepth 1 -type d -name "service*")

for project in $projects; do
  echo "🔧 Building: $project"
  cd "$project" || { echo "⛔ Failed to enter directory: $project"; continue; }

  mvn compile jib:dockerBuild
  if [ $? -eq 0 ]; then
    echo "✅ Success: $project"
  else
    echo "❌ Build failed: $project"
  fi

  echo "---------------------------"
  cd ..
done
