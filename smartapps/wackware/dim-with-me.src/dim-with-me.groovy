definition(
    name: "Dim With Me",
    namespace: "wackware",
    author: "todd@wackford.net",
    description: "Follows the dimmer level of another dimmer",
    category: "My Apps",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience%402x.png"
)

preferences {
	section("When this...") { 
		input "masters", "capability.switchLevel", 
			multiple: false, 
			title: "Master Dimmer Switch...", 
			required: true
	}

	section("Then these will follow with on/off...") {
		input "slaves2", "capability.switch", 
			multiple: true, 
			title: "Slave On/Off Switch(es)...", 
			required: false
	}
    
	section("And these will follow with dimming level...") {
		input "slaves", "capability.switchLevel", 
			multiple: true, 
			title: "Slave Dimmer Switch(es)...", 
			required: true
	}
}

def installed()
{
	subscribe(masters, "switch.on", switchOnHandler)
	subscribe(masters, "switch.off", switchOffHandler)
	subscribe(masters, "switch.setLevel", switchSetLevelHandler)
	subscribe(masters, "switch", switchSetLevelHandler)
}

def updated()
{
	unsubscribe()
	subscribe(masters, "switch.on", switchOnHandler)
	subscribe(masters, "switch.off", switchOffHandler)
	subscribe(masters, "switch.setLevel", switchSetLevelHandler)
	subscribe(masters, "switch", switchSetLevelHandler)
	log.info "subscribed to all of switches events"
}

def switchSetLevelHandler(evt)
{	
	
	if ((evt.value == "on") || (evt.value == "off" ))
		return
	def level = evt.value.toFloat()
	level = level.toInteger()
	log.info "switchSetLevelHandler Event: ${level}"
	slaves?.setLevel(level)
}

def switchOffHandler(evt) {
	log.info "switchoffHandler Event: ${evt.value}"
	slaves?.off()
	slaves2?.off()
}

def switchOnHandler(evt) {
	log.info "switchOnHandler Event: ${evt.value}"
	def dimmerValue = masters.latestValue("level") //can be turned on by setting the level
	slaves?.on()
	slaves2?.on()
}