definition(
    name: "Virtual Presence using switch",
    namespace: "baldeagle072",
    author: "Eric Roberts",
    description: "Use a switch to change state of a virtual presence sensor",
    category: "Family",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")


preferences {
	section("Switch on = present, switch off = not present") {
		input "controlSwitch", "capability.switch", title: "Switch", multiple: false, required: true
        input "slavePresence", "device.simulatedPresenceSensor", title: "Virtual Presence", multiple: false, required: true
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
	subscribe(controlSwitch, "switch", "switchHandler")
}

def switchHandler(evt) {
	if (evt.value == "on") {
    	slavePresence.arrived()
    } else {
    	slavePresence.departed()
    }
}