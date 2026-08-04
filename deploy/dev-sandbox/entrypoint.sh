#!/bin/sh

set -e

if [ -n "$DEEPSEEK_API_KEY" ]; then
    sed -i "s|<DEEPSEEK_API_KEY>|$DEEPSEEK_API_KEY|g" /root/.claude/settings.json
fi

if [ -n "$SONARQUBE_TOKEN" ]; then
    sed -i "s|<SONARQUBE_TOKEN>|$SONARQUBE_TOKEN|g" /workspace/.mcp.json
fi

exec "$@"
