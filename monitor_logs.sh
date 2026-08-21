#!/bin/bash

echo "============================================"
echo "📊 Monitoreo de Logs - InDrive Bot"
echo "============================================"
echo ""
echo "🔍 Filtrando logs (Ctrl+C para salir):"
echo "   - ConfigManager"
echo "   - ProUserManager  "
echo "   - SplashActivity"
echo "   - AdminDashboard"
echo ""

adb logcat | grep -E "ConfigManager|ProUserManager|SplashActivity|AdminDashboard|MainActivity"
