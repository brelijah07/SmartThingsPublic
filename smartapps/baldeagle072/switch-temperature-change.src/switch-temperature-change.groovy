definition(
    name: "Switch Temperature Change",
    namespace: "baldeagle072",
    author: "Eric Roberts",
    description: "Changes the set temperature after turning on a switch and goes back when switch is turned off",
    category: "Green Living",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")


preferences {
	section("Preferences") {
		input "thermostat", "capability.thermostat", title: "Thermostat", multiple: true, required: true
        input "newHeatingSetpoint", "number", title: "Heating Setpoint", required: false
        input "newCoolingSetpoint", "number", title: "Cooling Setpoint", required: false
        input "switch1", "capability.switch", title: "Switch", required: true
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
	subscribe(switch1, "switch.on", "onSwitchHandler")
    subscribe(switch1, "switch.off", "offSwitchHandler")
}

def onSwitchHandler(evt) {
	state.originalHeatingSetpoint = thermostat.latestValue("heatingSetpoint")
    state.originalCoolingSetpoint = thermostat.latestValue("coolingSetpoint")
    if (newHeatingSetpoint) {
    	thermostat.setHeatingSetpoint(newHeatingSetpoint)
    }
    if (newCoolingSetpoint) {
    	thermostat.setCoolingSetpoint(newCoolingSetpoint)
    }
}

def offSwitchHandler(evt) {
    log.debug("state.originalHeatingSetpoint[0]: ${state.originalHeatingSetpoint[0]}")
	thermostat.setHeatingSetpoint(state.originalHeatingSetpoint[0])
    thermostat.setCoolingSetpoint(state.originalCoolingSetpoint[0])
}