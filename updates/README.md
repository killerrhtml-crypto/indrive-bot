# Actualizaciones

Este directorio puede publicarse como Static Site en Render. Antes de desplegar:

1. Publica el APK generado en `updates/app-debug.apk`.
2. Sustituye `apkUrl` en `update_info.json` por la URL pública real de Render.
3. Incrementa `latestVersionCode` cada vez que publiques una versión nueva.

La app consulta `update_info.json` durante el splash y descarga el APK si su
`latestVersionCode` es superior al instalado.