// @vitest-environment jsdom
import { afterEach, describe, expect, it } from "vitest";
import { installMockBridge, uninstallMockBridge } from "@wefterjs/core/testing";
import { Screen } from "../src/index.js";

afterEach(() => {
  uninstallMockBridge();
});

describe("Screen.getBrightness", () => {
  it("resolves screen brightness", async () => {
    installMockBridge({
      screen: (method) => {
        expect(method).toBe("getBrightness");
        return { brightness: 0.75 };
      },
    });

    const result = await Screen.getBrightness();
    expect(result.brightness).toBe(0.75);
  });
});

describe("Screen.setBrightness", () => {
  it("sets screen brightness", async () => {
    installMockBridge({
      screen: (method, payload) => {
        expect(method).toBe("setBrightness");
        expect(payload).toEqual({ brightness: 0.5 });
        return { success: true };
      },
    });

    const result = await Screen.setBrightness({ brightness: 0.5 });
    expect(result).toEqual({ success: true });
  });
});

describe("Screen.keepAwake", () => {
  it("enables keep awake mode", async () => {
    installMockBridge({
      screen: (method) => {
        expect(method).toBe("keepAwake");
        return { success: true };
      },
    });

    const result = await Screen.keepAwake();
    expect(result).toEqual({ success: true });
  });
});

describe("Screen.allowSleep", () => {
  it("disables keep awake mode", async () => {
    installMockBridge({
      screen: (method) => {
        expect(method).toBe("allowSleep");
        return { success: true };
      },
    });

    const result = await Screen.allowSleep();
    expect(result).toEqual({ success: true });
  });
});

describe("Screen.getOrientation", () => {
  it("resolves screen orientation type", async () => {
    installMockBridge({
      screen: (method) => {
        expect(method).toBe("getOrientation");
        return { type: "portrait" };
      },
    });

    const result = await Screen.getOrientation();
    expect(result.type).toBe("portrait");
  });
});

describe("Screen.lockOrientation", () => {
  it("locks screen orientation to specified target", async () => {
    installMockBridge({
      screen: (method, payload) => {
        expect(method).toBe("lockOrientation");
        expect(payload).toEqual({ orientation: "landscape" });
        return { success: true };
      },
    });

    const result = await Screen.lockOrientation({ orientation: "landscape" });
    expect(result).toEqual({ success: true });
  });
});

describe("Screen.unlockOrientation", () => {
  it("unlocks screen orientation lock", async () => {
    installMockBridge({
      screen: (method) => {
        expect(method).toBe("unlockOrientation");
        return { success: true };
      },
    });

    const result = await Screen.unlockOrientation();
    expect(result).toEqual({ success: true });
  });
});
