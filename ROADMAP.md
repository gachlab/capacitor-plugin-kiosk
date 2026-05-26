# Roadmap — @gachlab/capacitor-kiosk

**Contexto:** Modo kiosk en Android vía Lock Task API (fija la app a la pantalla). Para auditoría de dispositivos, además de fijar la app interesa **saber cuándo el usuario salió de ella**.

**Estado actual:** v2.0.1 — Android funcional (`startLockTask`/`stopLockTask`, `getLockTaskModeState`); Web stub defensivo; iOS no existe. Bump AGP 9 mergeado, sin release npm.

---

## Decisión: Android-only definitivo

El kiosk de iOS (Single App Mode) requiere supervisión MDM y no es activable por una app no supervisada. **El plugin es Android-only por decisión** — documentarlo así en el README y cerrar el tema iOS.

## Fase 1 — Eventos de estado

Hoy hay que hacer polling de `isInKioskMode()`. Para auditoría y reactividad:

```typescript
'kioskExited'  → { reason: 'user' | 'system' | 'api', timestamp: number }
'kioskEntered' → { timestamp: number }
```

- Detectar la salida del Lock Task (botón, sistema, fallo de permisos) y emitir el evento.
- El plugin emite; el consumidor reporta. Para reporte confiable en background ver `event-sink`.

## Fase 2 — Robustez de entrada

```typescript
canEnterKioskMode(): Promise<{ value: boolean; reason?: string }>
```

Check preventivo (¿es Device Owner / está whitelisted?) en vez de tirar `SecurityException` al intentar `enterKioskMode()`.

## Fase 3 — Tests Android reales

Hoy solo hay un smoke test de instanciación. Agregar tests con mock de `ActivityManager` que cubran enter/exit/estado.

---

## Backlog

- **Setup asistido de Device Owner** (documentación/flujo de enrollment ADB/QR) para despliegues en flotas gestionadas.
- Sin enrollment de Device Owner, el screen-pinning estándar permite que el usuario salga (queda registrado vía `kioskExited`); con Device Owner el lock es firme.

## Notas de plataforma

- **Android-only.** Web es stub; iOS no aplica.
