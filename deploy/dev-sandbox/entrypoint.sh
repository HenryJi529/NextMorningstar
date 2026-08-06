#!/bin/sh

set -e

if [ -n "$MODEL_API_KEY" ]; then
    sed -i "s|<MODEL_API_KEY>|$MODEL_API_KEY|g" /home/bot/.claude/settings.json
fi

if [ -n "$SONARQUBE_TOKEN" ]; then
    sed -i "s|<SONARQUBE_TOKEN>|$SONARQUBE_TOKEN|g" /workspace/.mcp.json
fi

exec "$@"
