package rs.engine.script

class ScriptInfo {
    var scriptName = ""
    var sourceFilePath = ""
    var lookupKey = -1
    var parameterTypes = ArrayList<Int>()
    var pcs = ArrayList<Int>()
    var lines = ArrayList<Int>()
}