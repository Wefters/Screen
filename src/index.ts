import { definePlugin } from "@wefterjs/core";

export type OrientationType =
  | "portrait"
  | "portrait-primary"
  | "portrait-secondary"
  | "landscape"
  | "landscape-primary"
  | "landscape-secondary"
  | "unknown";

export interface GetBrightnessResult {
  brightness: number;
}

export interface SetBrightnessOptions {
  brightness: number;
}

export interface GetOrientationResult {
  type: OrientationType;
}

export interface LockOrientationOptions {
  orientation: OrientationType;
}

export interface ScreenOperationResult {
  success: true;
}

export const Screen = definePlugin<{
  getBrightness: () => Promise<GetBrightnessResult>;
  setBrightness: (options: SetBrightnessOptions) => Promise<ScreenOperationResult>;
  keepAwake: () => Promise<ScreenOperationResult>;
  allowSleep: () => Promise<ScreenOperationResult>;
  getOrientation: () => Promise<GetOrientationResult>;
  lockOrientation: (options: LockOrientationOptions) => Promise<ScreenOperationResult>;
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
