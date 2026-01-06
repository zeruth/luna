package rs.net.msg.out.game

class ServerGameProt(val id: Int, val length: Int) {
    companion object {
        val IF_OPENCHAT = ServerGameProt(109, 2);
        val IF_OPENMAIN_SIDE = ServerGameProt(128, 4);
        val IF_CLOSE = ServerGameProt(29, 0);
        val IF_SETTAB = ServerGameProt(10, 3);
        val IF_SETTAB_ACTIVE = ServerGameProt(252, 1);
        val IF_OPENMAIN = ServerGameProt(159, 2);
        val IF_OPENSIDE = ServerGameProt(246, 2);
        val IF_OPENOVERLAY = ServerGameProt(50, 2);
        val IF_OPENFULL = ServerGameProt(253, 4);

        // updating interfaces
        val IF_SETANGLE = ServerGameProt(186, 8); // todo: Real name? 
        val IF_SETCOLOUR = ServerGameProt(218, 4); // NXT naming
        val IF_SETHIDE = ServerGameProt(82, 3); // NXT naming
        val IF_SETOBJECT = ServerGameProt(21, 6); // NXT naming
        val IF_SETMODEL = ServerGameProt(216, 4); // NXT naming
        val IF_SETROTATION = ServerGameProt(18, 6); // todo: Real name?    
        val IF_SETRECOL = ServerGameProt(103, 6); // NXT naming
        val IF_SETANIM = ServerGameProt(2, 4); // NXT naming
        val IF_SETPLAYERHEAD = ServerGameProt(255, 2); // NXT naming
        val IF_SETTEXT = ServerGameProt(232, -2); // NXT naming
        val IF_SETNPCHEAD = ServerGameProt(162, 4); // NXT naming
        val IF_SETPOSITION = ServerGameProt(166, 6); // NXT naming
        val IF_SETSCROLLPOS = ServerGameProt(200, 4); // NXT naming

        // tutorial area
        val TUT_FLASH = ServerGameProt(238, 1);
        val TUT_OPEN = ServerGameProt(158, 2);

        // inventory
        val UPDATE_INV_STOP_TRANSMIT = ServerGameProt(219, 2); // NXT naming
        val UPDATE_INV_FULL = ServerGameProt(206, -2); // NXT naming
        val UPDATE_INV_PARTIAL = ServerGameProt(134, -2); // NXT naming

        // camera control
        val CAM_LOOKAT = ServerGameProt(167, 6); // NXT naming
        val CAM_SHAKE = ServerGameProt(67, 4); // NXT naming
        val CAM_MOVETO = ServerGameProt(3, 6); // NXT naming
        val CAM_RESET = ServerGameProt(148, 0); // NXT naming

        // entity updates
        val NPC_INFO = ServerGameProt(71, -2); // NXT naming
        val PLAYER_INFO = ServerGameProt(90, -2); // NXT naming

        // social
        val FRIENDLIST_LOADED = ServerGameProt(251, 1); // NXT naming
        val MESSAGE_GAME = ServerGameProt(63, -1); // NXT naming
        val UPDATE_IGNORELIST = ServerGameProt(226, -2); // NXT naming
        val CHAT_FILTER_SETTINGS = ServerGameProt(201, 3); // NXT naming
        val MESSAGE_PRIVATE = ServerGameProt(135, -1); // NXT naming
        val UPDATE_FRIENDLIST = ServerGameProt(78, 9); // NXT naming

        // misc
        val UNSET_MAP_FLAG = ServerGameProt(61, 0); // NXT has "SET_MAP_FLAG" but we cannot control the position
        val UPDATE_RUNWEIGHT = ServerGameProt(174, 2); // NXT naming
        val HINT_ARROW = ServerGameProt(199, 6); // NXT naming
        val UPDATE_REBOOT_TIMER = ServerGameProt(190, 2); // NXT naming
        val UPDATE_STAT = ServerGameProt(49, 6); // NXT naming
        val UPDATE_RUNENERGY = ServerGameProt(125, 1); // NXT naming
        val RESET_ANIMS = ServerGameProt(13, 0); // NXT naming
        val UPDATE_PID = ServerGameProt(126, 3);
        val LAST_LOGIN_INFO = ServerGameProt(76, 23); // NXT naming
        val LOGOUT = ServerGameProt(5, 0); // NXT naming
        val P_COUNTDIALOG = ServerGameProt(58, 0); // named after runescript command + client resume_p_countdialog packet
        val SET_MULTIWAY = ServerGameProt(233, 1);
        val SET_PLAYER_OP = ServerGameProt(157, -1);
        val P_NAMEDIALOG = ServerGameProt(6, 0);
        val MINIMAP_TOGGLE = ServerGameProt(156, 1);

        // maps
        val REBUILD_NORMAL = ServerGameProt(222, 4); // NXT naming
        val REBUILD_REGION = ServerGameProt(53, -1); // NXT naming

        // vars
        val VARP_SMALL = ServerGameProt(182, 3); // NXT naming
        val VARP_LARGE = ServerGameProt(115, 6); // NXT naming
        val RESET_CLIENT_VARCACHE = ServerGameProt(113, 0); // NXT naming

        // audio
        val SYNTH_SOUND = ServerGameProt(26, 5); // NXT naming
        val MIDI_SONG = ServerGameProt(220, 2); // NXT naming
        val MIDI_JINGLE = ServerGameProt(249, 5); // NXT naming

        // zones
        val UPDATE_ZONE_PARTIAL_FOLLOWS = ServerGameProt(75, 2); // NXT naming
        val UPDATE_ZONE_FULL_FOLLOWS = ServerGameProt(40, 2); // NXT naming
        val UPDATE_ZONE_PARTIAL_ENCLOSED = ServerGameProt(183, -2); // NXT naming
    }
}