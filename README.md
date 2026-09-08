# @wefterjs/screen

Official Wefter plugin for controlling screen brightness, orientation locks, and keep-awake states on Android and iOS.

## Features

- Query and set screen display brightness levels (normalized between 0.0 and 1.0).
- Prevent the device display from dimming or sleeping during long-running tasks.
- Restore standard system display timeout behavior.
- Query active screen orientation and apply portrait or landscape locks.
- Unlock orientation to restore automatic sensor-driven rotation.

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
await Screen.lockOrientation({ orientation: "landscape" });

// Lock to portrait
await Screen.lockOrientation({ orientation: "portrait" });

// Unlock orientation to re-enable sensor auto-rotation
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
- **Orientation**: Sets `requestedOrientation` on `MainActivity` using `ActivityInfo.SCREEN_ORIENTATION_*` constants.

### iOS

- **Brightness**: Sets `UIScreen.main.brightness`.
- **Keep awake**: Controls `UIApplication.shared.isIdleTimerDisabled`.
- **Orientation**: Interacts with `UIViewController.supportedInterfaceOrientations` and requests geometry updates on iOS 16 and later.

## License

[MIT](LICENSE) © 2026 Sandip Ghimire
