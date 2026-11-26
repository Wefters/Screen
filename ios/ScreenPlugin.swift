import UIKit

/// Screen brightness, idle timer and orientation control.
final class ScreenPlugin: WefterPlugin {

    /// The current screen brightness from 0 to 1.
    // @WefterMethod
    func getBrightness(payload: [String: Any], callback: @escaping (Result<Any, Error>) -> Void) throws {
        DispatchQueue.main.async {
            let brightness = Double(UIScreen.main.brightness)
            self.resolve(callback, data: ["brightness": brightness])
        }
    }

    /// Sets the screen brightness, clamped to 0–1.
    // @WefterMethod
    func setBrightness(payload: [String: Any], callback: @escaping (Result<Any, Error>) -> Void) throws {
        guard let brightnessNum = payload["brightness"] as? NSNumber else {
            reject(callback, code: "INVALID_ARGUMENT", message: "A brightness value between 0.0 and 1.0 is required.")
            return
        }
        let brightness = CGFloat(min(max(brightnessNum.doubleValue, 0.0), 1.0))
        DispatchQueue.main.async {
            UIScreen.main.brightness = brightness
            self.resolve(callback, data: ["success": true])
        }
    }

    /// Disables the idle timer so the screen stays on.
    // @WefterMethod
    func keepAwake(payload: [String: Any], callback: @escaping (Result<Any, Error>) -> Void) throws {
        DispatchQueue.main.async {
            UIApplication.shared.isIdleTimerDisabled = true
            self.resolve(callback, data: ["success": true])
        }
    }

    /// Re-enables the idle timer.
    // @WefterMethod
    func allowSleep(payload: [String: Any], callback: @escaping (Result<Any, Error>) -> Void) throws {
        DispatchQueue.main.async {
            UIApplication.shared.isIdleTimerDisabled = false
            self.resolve(callback, data: ["success": true])
        }
    }

    /// The current interface orientation.
    // @WefterMethod
    func getOrientation(payload: [String: Any], callback: @escaping (Result<Any, Error>) -> Void) throws {
        DispatchQueue.main.async {
            let deviceOrientation = UIDevice.current.orientation
            let type: String
            switch deviceOrientation {
            case .portrait:
                type = "portrait"
            case .portraitUpsideDown:
                type = "portrait-secondary"
            case .landscapeLeft:
                type = "landscape-primary"
            case .landscapeRight:
                type = "landscape-secondary"
            case .faceUp, .faceDown, .unknown:
                type = "unknown"
            default:
                type = "unknown"
            }
            self.resolve(callback, data: ["type": type])
        }
    }

    /// Rotates the interface to an orientation by setting the device orientation.
    // @WefterMethod
    func lockOrientation(payload: [String: Any], callback: @escaping (Result<Any, Error>) -> Void) throws {
        let orientationStr = (payload["orientation"] as? String ?? "portrait").lowercased()
        let value: UIInterfaceOrientation
        switch orientationStr {
        case "landscape", "landscape-primary":
            value = .landscapeLeft
        case "landscape-secondary":
            value = .landscapeRight
        case "portrait-secondary":
            value = .portraitUpsideDown
        default:
            value = .portrait
        }

        DispatchQueue.main.async {
            UIDevice.current.setValue(value.rawValue, forKey: "orientation")
            UIViewController.attemptRotationToDeviceOrientation()
            self.resolve(callback, data: ["success": true])
        }
    }

    /// Lets the interface follow the device orientation again.
    // @WefterMethod
    func unlockOrientation(payload: [String: Any], callback: @escaping (Result<Any, Error>) -> Void) throws {
        DispatchQueue.main.async {
            UIViewController.attemptRotationToDeviceOrientation()
            self.resolve(callback, data: ["success": true])
        }
    }
}
