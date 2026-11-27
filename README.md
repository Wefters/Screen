# @wefterjs/screen

Official Wefter plugin for controlling screen brightness, orientation locks, and keep-awake states on Android and iOS.

## Features

- Query and set screen display brightness levels (normalized between 0.0 and 1.0).
- Prevent the device display from dimming or sleeping during long-running tasks.
- Restore standard system display timeout behavior.
- Query active screen orientation and apply portrait or landscape locks.
- Unlock orientation to return to the app's own orientation (`orientation` in `wefter.config.json`: portrait on phones by default).

## Installation and setup

Install the plugin package in your Wefter application:

```bash
wefter add @wefterjs/screen
wefter sync
```

### Native permissions

When synchronized:

- Android automatically requests `<uses-permission android:name="android.permission.WAKE_LOCK" />`.
- iOS requires no additional permission keys.

## JavaScript API reference

Import `Screen` from `@wefterjs/screen`:

```ts
import { Screen } from "@wefterjs/screen";
```

### Screen brightness

Query or adjust the application window brightness:

```ts
// Get current display brightness (0.0 to 1.0)
const { brightness } = await Screen.getBrightness();
console.log("Current brightness:", brightness);

// Set window brightness to 80%
await Screen.setBrightness({ brightness: 0.8 });
```

Setting brightness changes the brightness of the current application window. It does not permanently overwrite the user's global system settings.

### Keep awake and allow sleep

Prevent the device screen from turning off during video playback, barcode scanning, or active navigation:

```ts
// Prevent the display from sleeping
await Screen.keepAwake();

// Restore default operating system sleep timer
await Screen.allowSleep();
```

### Screen orientation

Query current orientation or lock display rotation:

```ts
// Check active orientation
const { type } = await Screen.getOrientation();
console.log("Current orientation:", type);
// "portrait", "portrait-primary", "landscape", "landscape-primary", etc.

// Lock to landscape
await Screen.lockOrientation({ orientation: "landscape" }); // portrait, landscape, their -primary/-secondary forms, or any

// Lock to portrait
await Screen.lockOrientation({ orientation: "portrait" });

// Unlock: back to the app's own orientation (portrait by default; set "orientation": "any" to rotate freely)
await Screen.unlockOrientation();
```

## Complete usage example

```ts
import { Screen } from "@wefterjs/screen";

export async function enterMediaPlayback() {
  // Maximize brightness, lock to landscape, and prevent screen sleep
  await Screen.setBrightness({ brightness: 1.0 });
  await Screen.lockOrientation({ orientation: "landscape" });
  await Screen.keepAwake();
}

export async function exitMediaPlayback() {
  // Restore default brightness, orientation, and sleep timer
  await Screen.setBrightness({ brightness: -1 }); // -1 resets to system default on Android
  await Screen.unlockOrientation();
  await Screen.allowSleep();
}
```

## Platform implementation notes

### Android

- **Brightness**: Modifies `WindowManager.LayoutParams.screenBrightness` on the current window.
- **Keep awake**: Toggles the `WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON` flag on the window decor view.
- **Orientation**: Uses the shell's `OrientationLock`, which sets `requestedOrientation`; unlocking returns to the orientation in the app's manifest.

### iOS

- **Brightness**: Sets `UIScreen.main.brightness`.
- **Keep awake**: Controls `UIApplication.shared.isIdleTimerDisabled`.
- **Orientation**: Uses the shell's `OrientationLock`, which overrides the app's supported orientations while locked (so a portrait app can lock to landscape) and requests a geometry update.

## License

[MIT](LICENSE) © 2026 Sandip Ghimire
