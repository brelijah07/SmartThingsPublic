definition(
    name: "I'm awake!",
    namespace: "Petezah",
    author: "Peter Dunshee",
    description: "Switch to your 'awake' mode when certain switches are used in your home.",
    category: "Convenience",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")

preferences {
	page(name: "mainPage")
}

def mainPage() {
	dynamicPage(name: "mainPage", install: true, uninstall: true) {
    
    	section() {
			input(name: "switches", type: "capability.switch", title: "Switches", description: "Switches to monitor for activity", multiple: true, required: true)
			input(name: "awakePhrase", type: "enum", title: "Execute a phrase when I'm awake", options: listPhrases(), required: true)
			input(name: "applicableModes", type: "mode", title: "Do this only if I am in one of these modes", description: "The modes that this app is applicable to", multiple: true, required: true)
        }
	}
}

// Lifecycle management
def installed() {
	log.debug "<I'm awake> Installed with settings: ${settings}"
	initialize()
}

def updated() {
	log.debug "<I'm awake> Updated with settings: ${settings}"
	unsubscribe()
	initialize()
}

def initialize() {
	subscribe(switches, "switch.on", switchHandler)
}

// Event handlers
def switchHandler(evt) {
	log.debug "<I'm awake> switchHandler: $evt"

	if (allOk) {
		if (awakePhrase) {
			log.debug "<I'm awake> executing: $awakePhrase"
			executePhrase(awakePhrase)
		}
    }
}

// Helpers
private listPhrases() {
	location.helloHome.getPhrases().label
}

private executePhrase(phraseName) {
	if (phraseName) {
		location.helloHome.execute(phraseName)
		log.debug "<I'm awake> executed phrase: $phraseName"
	}
}

private getAllOk() {
	modeOk
}

private getModeOk() {
	def result = !applicableModes || applicableModes.contains(location.mode)
	log.trace "<I'm awake> modeOk = $result"
	result
}