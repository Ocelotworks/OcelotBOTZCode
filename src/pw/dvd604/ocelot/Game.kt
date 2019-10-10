package pw.dvd604.ocelot

import com.zaxsoft.zax.zmachine.ZCPU

open class Game (val id : String, var ui : RPCInterface, var cpu : ZCPU, var thread : Thread)