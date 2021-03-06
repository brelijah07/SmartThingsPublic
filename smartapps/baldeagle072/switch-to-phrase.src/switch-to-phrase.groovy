definition(
    name: "Switch to Phrase",
    namespace: "baldeagle072",
    author: "Eric Roberts",
    description: "Use a virtual switch to switch to a phrase.",
    category: "Mode Magic",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")


preferences {
	page(name: "configure")
}

def configure() {
	dynamicPage(name: "configure", title: "Configure", install: true, uninstall: true) {
        section("Choose a switch and a phrase") {
            input "theSwitch", "capability.switch", title: "Switch", multiple: false, required: true
            input "phrase", "enum", title: "Phrase to run", options: getPhrases(), required: true 
        }
        
        section ([mobileOnly:true]) {
            label title: "Assign a name", required: false
            mode title: "Set for specific mode(s)", required: false
        }
    }
}

def getPhrases() {
	def phrases = location.helloHome?.getPhrases()*.label
    if (phrases) {
        phrases.sort()
    }
    return phrases
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
	subscribe(theSwitch, "switch.on", switchHandler)
}

def switchHandler(evt) {
	log.debug("Executing ${settings.phrase}")
	location.helloHome.execute(settings.phrase)
    theSwitch.off()
}