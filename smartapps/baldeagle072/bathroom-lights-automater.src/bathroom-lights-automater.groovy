definition(
    name: "Bathroom Lights Automater",
    namespace: "baldeagle072",
    author: "Eric Roberts",
    description: "Automate your bathroom lights",
    category: "Convenience",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")


preferences {
	section("Bathroom Setup") {
		input "doorContact", "capability.contactSensor", title: "Door Contact Sensor", required: false, multiple: true
        input "motionSensor", "capability.motionSensor", title: "Motion Detector", required: false, multiple: true
        input "mainLights", "capability.switch", title: "Light", required: true, multiple: true
        input "timeout", number, title: "Timeout in minutes if door left open", required: false
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
	subscribe(doorContact, "contact.open", openHandler)
    subscribe(doorContact, "contact.closed", closedHandler)
    state.out = true
}

def openHandler(evt) {
	log.debug("Open - state.out: ${state.out}")
    unsubscribe(motionSensor)
	subscribe(motionSensor, "motion", openMotionHandler)
    mainLights.on()
    motionSensor.each {
        if (it.currentValue("motion") != "active"){
            runIn(timeout.toInteger() * 60, timedout)
        }
    }
    if (state.out) {
    	state.out = false
    } else {
    	state.out = true
    }
}

def closedHandler(evt) {
	log.debug("Closed - state.out: ${state.out}")
	unsubscribe(motionSensor)
    unschedule()
    subscribe(motionSensor, "motion.active", closedMotionHandler)
	if (state.out) {
    	mainLights.off()
    }
}

def closedMotionHandler(evt) {
	log.debug("closed motion")
	state.out = false
    mainLights.on()
}

def openMotionHandler(evt) {
	log.debug("Motion - evt.value: ${evt.value}")
	if (evt.value == "active") {
    	mainLights.on()
        unschedule()
    } else {
    	runIn(timeout.toInteger() * 60, timedout)
    }
}

def timedout() {
	log.debug("timedout")
	mainLights.off()
    state.out = true
}