/************
 * Metadata *
 ************/

definition(
    name: "Hue OffSwitch",
    namespace: "evennink",
    author: "Erik Vennink",
    description: "Switch off Hue lights based on an (virtual) switch.",
    category: "My Apps",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")

/**********
 * Setup  *
 **********/

preferences {
    page(name: "lightSelectPage", title: "Turn off these lights:", install: true, params: [sceneId:sceneId], uninstall: true) 
}

def lightSelectPage() {
	dynamicPage(name: "lightSelectPage") {
        section("Use this (virtual) switch"){
            input "inputSwitch", "capability.switch", title: "Switches", required: true, multiple: true
        }

		section("To switch off these lights (when the switch is OFF)") {
			input "lights", "capability.colorControl", multiple: true, required: false, title: "Lights, switches & dimmers"
		}
		section([mobileOnly:true]) {
			label title: "Assign a name", required: false
		}
    }
}

/*************************
 * Installation & update *
 *************************/

def installed() {
	log.debug "Installed with settings: ${settings}"
	subscribeToEvents()
}

def updated() {
	log.debug "Updated with settings: ${settings}"
	unsubscribe()
	unschedule()
	subscribeToEvents()
}

def subscribeToEvents() {
	subscribe(app, appTouchHandler)
	subscribe(inputSwitch, "switch", switchHandler)
}

/******************
 * Event handlers *
 ******************/

def appTouchHandler(evt) {
	log.info "app started manually"
    deactivateHue()
}

def switchHandler(evt) {
	log.trace "switchHandler()"
	def current = inputSwitch.currentValue('switch')
	def switchValue = inputSwitch.find{it.currentSwitch == "on"}
    def waitMode = 2500
	if (switchValue) {
    	log.info "Wrong mode to activate anything"
    }
	else {
        pause(waitMode)
        deactivateHue()
        pause(waitMode)
        deactivateHue()
        pause(waitMode)
        deactivateHue()
    }
}

/******************
 * Helper methods *
 ******************/

private deactivateHue() {
	log.trace "Deactivating!"
	state.lastStatus = "off"
    def wait = 250
    log.debug wait

	lights.each {light ->
        light.off()
        pause(wait)
        light.off()
        pause(wait)
        light.off()
    }
}