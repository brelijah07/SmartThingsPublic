definition (
    name: "Reset Color on Close",
    namespace: "smartthings",
    author: "smartthings",
    description: "Return color bulbs to previous setting on closure of contact sensor(s).",
    category: "Convenience",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png"
)

preferences {
	section("When this/these contact(s) close...") {
		input "contacts", "capability.contactSensor", multiple: true
	}
	section("Return this light to the color at contact open...") {
		input "bulb", "capability.colorControl"
	}
}

def installed() {
	subscribe(contacts, "contact.open", contactOpenHandler)
    subscribe(contacts, "contact.closed", contactClosedHandler)
}

def updated() {
	unsubscribe()
	subscribe(contacts, "contact.open", contactOpenHandler)
    subscribe(contacts, "contact.closed", contactclosedHandler)
}

def contactOpenHandler(evt) {
    def values = [:]
	values = [ level: bulb.latestValue("level") as Integer,
               hex: bulb.latestValue("color"),
               saturation: bulb.latestValue("saturation"),
               hue: bulb.latestValue("hue")]
               
    atomicState.previousValues = values
	log.info "Previous values are: ${atomicState.previousValues}"
}