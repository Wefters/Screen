# @wefterjs/screen

Official Wefter plugin for controlling screen brightness, orientation locks, and keep-awake state on Android and iOS.

---

## Features

- ☀️ **Screen Brightness**: Query and adjust display brightness (`getBrightness`, `setBrightness`).
- ☕ **Keep Awake**: Prevent screen display sleep during critical tasks (`keepAwake`, `allowSleep`).
- 🔄 **Orientation Locks**: Query active screen orientation and lock/unlock orientation modes (`getOrientation`, `lockOrientation`, `unlockOrientation`).

---

## Installation & Setup

1. Add the plugin to your Wefter project:

```bash
wefter add @wefterjs/screen
```

2. Synchronize native projects:

```bash
wefter sync
```

---

## JavaScript / TypeScript API Reference

```ts
import { Screen } from "@wefterjs/screen";
```

### 1. `getBrightness()` & `setBrightness(options)`

Query or adjust screen display brightness (normalized `0.0` to `1.0`).

```ts
const { brightness } = await Screen.getBrightness();
console.log("Current brightness:", brightness);

await Screen.setBrightness({ brightness: 0.8 });
```

### 2. `keepAwake()` & `allowSleep()`

Keep the screen awake during video playback or active navigation, and restore default sleep timers when finished.

```ts
// Prevent display from turning off
await Screen.keepAwake();

// Restore system sleep behavior
await Screen.allowSleep();
```

### 3. Orientation Management (`getOrientation`, `lockOrientation`, `unlockOrientation`)

```ts
// Check current orientation
const { type } = await Screen.getOrientation();
console.log("Current orientation:", type);

// Lock orientation to landscape
await Screen.lockOrientation({ orientation: "landscape" });

// Unlock orientation
await Screen.unlockOrientation();
```

---

## Complete Usage Example

```ts
import { Screen } from "@wefterjs/screen";

export async function enterVideoPlayerMode() {
  // Maximize brightness and lock to landscape
  await Screen.setBrightness({ brightness: 1.0 });
  await Screen.keepAwake();
  await Screen.lockOrientation({ orientation: "landscape" });
}

export async function exitVideoPlayerMode() {
  await Screen.allowSleep();
  await Screen.unlockOrientation();
}
```
