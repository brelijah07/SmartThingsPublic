definition(
    name: "Turn on with motion unless turned off",
    namespace: "baldeagle072",
    author: "Eric Roberts",
    description: "Any motion detected will turn the lights on unless the switch has been manually switched off recently.",
    category: "Convenience",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")


preferences {
	section("Devices") {
		input "motion", "capability.motionSensor", title: "Motion Sensor", multiple: false
        input "lights", "capability.switch", title: "Lights to turn on", multiple: true
	}
    section("Preferences") {
    	paragraph "If you turn off this light, the motion sensor will not turn on the lights for the specified minutes"
        input "switchLight", "capability.switch", title: "Switch", multiple: false
        input "minutes", "number", title: "Minutes"
    }
}

def installed() {
	log.debug "Installed with settings: ${settings}"

	initialize()
}

def updated() {
	log.debug "Updated with settings: ${settings}"

	unsubscribe()
	initialize()
}

def initialize() {
	subscribe(motion, "motion", motionHandler)
    subscribe(switchLight, "switch", switchHandler)
    state.enabled = true
    state.runOff = true
}

def motionHandler(evt) {
	log.debug "Motion Handler - Evt value: ${evt.value}"
    if (evt.value == "active") {
    	if (state.enabled) {
        	log.debug("Turning on lights")
            lights?.on()
        } else {
        	log.debug("Motion is disabled - not turning on lights")
        }
    }
}

def switchHandler(evt) {
	log.debug "Switch Handler - Evt value: ${evt.value}, isPhysical: ${evt.isPhysical()}"
    if (evt.value == "off" && evt.isPhysical()) {
    	log.debug "Motion disabled"
    	state.enabled = false
        def runInMins = minutes * 60
        runIn(runInMins, enable)
    }
}

def enable() {
	log.debug "Motion enabled"
	state.enabled = true
}