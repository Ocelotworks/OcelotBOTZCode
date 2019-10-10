package pw.dvd604.ocelot.consumer

import com.zaxsoft.zax.zmachine.ZCPU

open class Game (val id : String, var ui : RPCInterface, var cpu : ZCPU, var thread : Thread, var lastPlayTime : Long = 0, var canDestroy : Boolean = false) {
    fun saveAndDestroy() {
        ui.text = "save"
        thread.interrupt()
        canDestroy = true
    }
}