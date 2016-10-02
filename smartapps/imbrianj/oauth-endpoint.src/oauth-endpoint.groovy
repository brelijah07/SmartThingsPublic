definition(
  name: "OAuth Endpoint",
  namespace: "imbrianj",
  author: "brian@bevey.org",
  description: "OAuth endpoint for Universal Controller",
  iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
  iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience%402x.png",
  oauth: true
)

preferences {
  section("Allow Endpoint to Control These Things...") {
    input "switches", "capability.switch",         title: "Which Switches?", multiple: true, required: false
    input "locks",    "capability.lock",           title: "Which Locks?",    multiple: true, required: false
    input "contact",  "capability.contactSensor",  title: "Which Contact?",  multiple: true, required: false
    input "moisture", "capability.waterSensor",    title: "Which Moisture?", multiple: true, required: false
    input "motion",   "capability.motionSensor",   title: "Which Motion?",   multiple: true, required: false
    input "presence", "capability.presenceSensor", title: "Which Presence?", multiple: true, required: false
  }

  section("IP:PORT of local endpoint") {
    input "endpoint", "text", title: "IP:PORT of local endpoint", required: false
  }
}

  /*********************/
 /* EVENT REGISTERING */
/*********************/
def installed() {
  init()
}

def updated() {
  unsubscribe()
  init()
}

def init() {
  if(endpoint) {
    subscribe(location, "mode",        modeEventFired)
    subscribe(switches, "switch",      switchFired)
    subscribe(locks,    "lock",        lockFired)
    subscribe(temp,     "temperature", tempFired)
    subscribe(contact,  "contact",     contactFired)
    subscribe(moisture, "water",       moistureFired)
    subscribe(motion,   "motion",      motionFired)
    subscribe(presence, "presence",    presenceFired)
  }
}

def modeEventFired(evt) {
  sendUpdate("Mode", evt.value, "mode")
}

def switchFired(evt) {
  sendUpdate(evt.displayName, evt.value, "switch")
}

def lockFired(evt) {
  sendUpdate(evt.displayName, evt.value, "lock")
}

def tempFired(evt) {
  sendUpdate(evt.displayName, evt.value, "temp")
}

def contactFired(evt) {
  sendUpdate(evt.displayName, evt.value, "contact")
}

def moistureFired(evt) {
  sendUpdate(evt.displayName, evt.value, "moisture")
}

def motionFired(evt) {
  sendUpdate(evt.displayName, evt.value, "motion")
}

def presenceFired(evt) {
  sendUpdate(evt.displayName, evt.value, "presence")
}

def sendUpdate(name, value, type) {
  def summary = ""

  log.warn(name + " is now " + value)

  if(value == "on"     ||
     value == "lock"   ||
     value == "open"   ||
     value == "wet"    ||
     value == "active" ||
     value == "present") {
    summary = "on"
  }

  else if(value == "off"      ||
          value == "unlock"   ||
          value == "closed"   ||
          value == "dry"      ||
          value == "inactive" ||
          value == "not present") {
    summary = "off"
  }

  else {
    summary = value
  }

  if(name == "Mode") {
    name = ""
  }

  else {
    name = name + "-"
  }

  def hubAction = sendHubCommand(new physicalgraph.device.HubAction(
    method: "GET",
    path: "/",
    headers: [HOST:endpoint, REST:true],
    query: ["smartthings":"subdevice-state-" + type + "-" + name + summary]
  ))

  if(options) {
    hubAction.options = options
  }

  hubAction
}

  /****************/
 /* API ENDPOINT */
/****************/
mappings {
  path("/switches/:id/:command") {
    action: [GET: "updateSwitch"]
  }

  path("/locks/:id/:command") {
    action: [GET: "updateLock"]
  }

  path("/mode/:mode") {
    action: [GET: "updateMode"]
  }

  path("/list") {
    action: [GET: "listDevices"]
  }
}

def listDevices() {
  printDevices(null, null)
}

def printDevices(device, newValue) {
  def mode = params.mode ? params.mode : location.mode

  return [mode: mode, devices: (settings.switches + settings.locks + settings.contact + settings.moisture + settings.motion + settings.presence).collect{deviceJson(it, device, newValue)}]
}

def deviceJson(it, device, newValue) {
  if (!it) { return [] }

  def values = [:]

  for (a in it.supportedAttributes) {
    if(it == device && (a.name == "switch" || a.name == "lock")) {
      values[a.name] = [name  : a.name,
                        value : newValue]
    }

    else {
      values[a.name] = it.currentState(a.name)
    }
  }

  return [label  : it.displayName,
          name   : it.name,
          id     : it.id,
          values : values]
}

def updateSwitch() {
  update(switches, "switch")
}

def updateLock() {
  update(locks, "lock")
}

// Modes
def updateMode() {
  log.debug "Mode change request: params: ${params}"

  setLocationMode(params.mode)

  listDevices()
}

def update(devices, type) {
  def command  = params.command
  def device   = devices.find { it.id == params.id }
  def newValue = ""

  if (command) {
    if (!device) {
      httpError(404, "Device not found")
    }

    else {
      if(command == "toggle") {
        if(type == "switch") {
          if(device.currentValue(type) == "on") {
            device.off();
            newValue = "off"
          }

          else {
            device.on();
            newValue = "on"
          }
        }

        if(type == "lock") {
          if(device.currentValue(type) == "locked") {
            device.off();
            newValue = "unlock"
          }

          else {
            device.on();
            newValue = "lock"
          }
        }
      }

      if(!newValue) {
        device."$command"()
        newValue = command
      }
    }
  }

  printDevices(device, newValue)
}