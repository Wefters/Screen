/** Controls screen brightness, keeping the screen awake, and orientation. */

import { definePlugin } from "@wefterjs/core";

/**
 * A screen orientation. `portrait` and `landscape` allow either direction; `-primary` and `-secondary` pick
 * one.
 */
export type OrientationType =
  | "portrait"
  | "portrait-primary"
  | "portrait-secondary"
  | "landscape"
  | "landscape-primary"
  | "landscape-secondary"
  | "unknown";

/** Screen brightness from 0 to 1. */
export interface GetBrightnessResult {
  brightness: number;
}

/** Brightness from 0 to 1. Values outside the range are clamped. */
export interface SetBrightnessOptions {
  brightness: number;
}

/** The current orientation. */
export interface GetOrientationResult {
  type: OrientationType;
}

/**
 * An orientation to lock to: `portrait` or `landscape` (either way up), one exact way up (`-primary`,
 * `-secondary`), or `any`.
 */
export type LockableOrientation = Exclude<OrientationType, "unknown"> | "any";

/** The orientation to lock to. */
export interface LockOrientationOptions {
  orientation: LockableOrientation;
}

/** Returned by successful changes. */
export interface ScreenOperationResult {
  success: true;
}

/** The Screen plugin. */
export const Screen = definePlugin<{
  /** The current screen brightness. */
  getBrightness: () => Promise<GetBrightnessResult>;
  /** Sets the screen brightness while the app is in use. */
  setBrightness: (options: SetBrightnessOptions) => Promise<ScreenOperationResult>;
  /** Stops the screen from turning off while the app is in use. */
  keepAwake: () => Promise<ScreenOperationResult>;
  /** Lets the screen turn off again. */
  allowSleep: () => Promise<ScreenOperationResult>;
  /** The current screen orientation. */
  getOrientation: () => Promise<GetOrientationResult>;
  /** Locks the screen to an orientation. */
  lockOrientation: (options: LockOrientationOptions) => Promise<ScreenOperationResult>;
  /** Returns the screen to the app's own orientation (`orientation` in `wefter.config.json`). */
  unlockOrientation: () => Promise<ScreenOperationResult>;
}>("screen", {
  getBrightness: true,
  setBrightness: true,
  keepAwake: true,
  allowSleep: true,
  getOrientation: true,
  lockOrientation: true,
  unlockOrientation: true,
});
