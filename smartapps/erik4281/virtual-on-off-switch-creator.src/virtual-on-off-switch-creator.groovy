/************
 * Metadata *
 ************/

definition(
	name: "Virtual On/Off Switch Creator",
	namespace: "erik4281",
	author: "Erik Vennink",
	description: "Creates virtual switches!",
	category: "My Apps",
	iconUrl: "http://baldeagle072.github.io/icons/standard-tile@1x.png",
	iconX2Url: "http://baldeagle072.github.io/icons/standard-tile@2x.png",
	iconX3Url: "http://baldeagle072.github.io/icons/standard-tile@3x.png")

/************
 * Metadata *
 ************/

preferences {
	section("Create Virtual Switch") {
		input "switchLabel", "text", title: "Switch Label", required: true
	}
}

/*************************
 * Installation & update *
 *************************/

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
	def deviceId = app.id + "SimulatedSwitch"
	log.debug(deviceId)
	def existing = getChildDevice(deviceId)
	if (!existing) {
		def childDevice = addChildDevice("smartthings", "On/Off Button Tile", deviceId, null, [label: switchLabel])
	}
}

def uninstalled() {
    removeChildDevices(getChildDevices())
}

/******************
 * Event handlers *
 ******************/

/******************
 * Helper methods *
 ******************/

private removeChildDevices(delete) {
	delete.each {
		deleteChildDevice(it.deviceNetworkId)
	}
}