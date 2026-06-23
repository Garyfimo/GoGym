package com.garyfimo.gogym

import platform.UIKit.UIDevice

class IOSPlatform: Platform {
    override val name: String = UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion
    override val apiBaseUrl: String = "http://localhost:3000"
}

actual fun getPlatform(): Platform = IOSPlatform()