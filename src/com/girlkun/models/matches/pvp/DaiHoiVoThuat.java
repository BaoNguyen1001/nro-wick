package com.girlkun.models.matches.pvp;

import com.girlkun.models.matches.PVP;
import com.girlkun.models.matches.TYPE_LOSE_PVP;
import com.girlkun.models.matches.TYPE_PVP;
import com.girlkun.models.player.Player;


public class DaiHoiVoThuat extends PVP{

    public DaiHoiVoThuat(Player p1, Player p2) {
        super(TYPE_PVP.THACH_DAU, p1, p2);
    }

    @Override
    public void finish() {
        
    }

    @Override
    public void update() {
        
    }

    @Override
    public void reward(Player plWin) {
        
    }

    @Override
    public void sendResult(Player plLose, TYPE_LOSE_PVP typeLose) {
        
    }

}





















