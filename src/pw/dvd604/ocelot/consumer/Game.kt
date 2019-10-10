package pw.dvd604.ocelot.consumer

import com.zaxsoft.zax.zmachine.ZCPU
import org.json.simple.JSONArray
import java.util.stream.Collectors



class Game (val id : String, var ui : RPCInterface, var cpu : ZCPU, var thread : Thread, var lastPlayTime : Long = 0, var canDestroy : Boolean = false, var players : ArrayList<String> = ArrayList(0)) {

    init {
        ui.container = this
    }
    fun saveAndDestroy() {
        ui.text = "save"
        thread.interrupt()
        canDestroy = true
    }

    fun addPlayer(s: String) {
        if(!players.contains(s)){
            players.add(s)
        }
    }

    fun getPlayers(): String {
        val array = JSONArray()
        array.addAll(players)

        return array.toJSONString()
    }
}