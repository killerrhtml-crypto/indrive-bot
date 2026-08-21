#!/bin/bash

echo "============================================"
echo "🚀 PASO 3A: BUILD & TEST - InDrive Bot"
echo "============================================"
echo ""

# Colores para output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo "📋 Paso 1: Limpiar proyecto..."
echo "Ejecutando: ./gradlew clean"
./gradlew clean
if [ $? -ne 0 ]; then
    echo -e "${RED}❌ Error en limpieza${NC}"
    exit 1
fi
echo -e "${GREEN}✅ Limpieza completada${NC}"
echo ""

echo "🔨 Paso 2: Compilar APK Debug..."
echo "Ejecutando: ./gradlew assembleDebug"
./gradlew assembleDebug
if [ $? -ne 0 ]; then
    echo -e "${RED}❌ Error en compilación${NC}"
    exit 1
fi
echo -e "${GREEN}✅ Compilación completada${NC}"
echo ""

echo "📱 Paso 3: Verificar APK generado..."
APK_PATH="app/build/outputs/apk/debug/app-debug.apk"
if [ -f "$APK_PATH" ]; then
    echo -e "${GREEN}✅ APK encontrado: $APK_PATH${NC}"
    APK_SIZE=$(ls -lh "$APK_PATH" | awk '{print $5}')
    echo "   Tamaño: $APK_SIZE"
else
    echo -e "${RED}❌ APK no encontrado en $APK_PATH${NC}"
    exit 1
fi
echo ""

echo "📲 Paso 4: Buscar dispositivos conectados..."
echo "Ejecutando: adb devices"
adb devices
echo ""

echo "⚠️  NOTA IMPORTANTE:"
echo "Para instalar el APK, ejecuta uno de estos comandos:"
echo ""
echo -e "${YELLOW}# Si tienes UN DISPOSITIVO/EMULADOR conectado:${NC}"
echo "adb install -r app/build/outputs/apk/debug/app-debug.apk"
echo ""
echo -e "${YELLOW}# Si tienes MÚLTIPLES dispositivos (reemplaza XXX con serial):${NC}"
echo "adb -s <device_serial> install -r app/build/outputs/apk/debug/app-debug.apk"
echo ""
echo -e "${YELLOW}# Ver logs en tiempo real:${NC}"
echo "adb logcat | grep -E 'ConfigManager|ProUserManager|SplashActivity|AdminDashboard'"
echo ""
echo "============================================"
echo -e "${GREEN}✅ BUILD COMPLETADO EXITOSAMENTE${NC}"
echo "============================================"
