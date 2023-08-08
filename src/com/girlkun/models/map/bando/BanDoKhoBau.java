package com.girlkun.models.map.bando;
//import com.girlkun.models.boss.bdkb.TrungUyXanhLo;

import com.girlkun.models.clan.Clan;
import com.girlkun.models.map.TrapMap;
import com.girlkun.models.map.Zone;
import com.girlkun.models.mob.Mob;
import com.girlkun.models.player.Player;
import com.girlkun.services.ItemTimeService;
import com.girlkun.services.MapService;
import com.girlkun.services.Service;
import com.girlkun.services.func.ChangeMapService;
import com.girlkun.utils.Logger;
import com.girlkun.utils.Util;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author BTH public static final int MAX_AVAILABLE = 50;
 *
 *
 */
public class BanDoKhoBau {

    public static final long POWER_CAN_GO_TO_DBKB = 2000000000;
    public static final List<BanDoKhoBau> BAN_DO_KHO_BAUS;
    public static final int MAX_AVAILABLE = 50;
    public static final int TIME_BAN_DO_KHO_BAU = 1800000; //1800000;

    private Player player;

    static {
        BAN_DO_KHO_BAUS = new ArrayList<>();
        for (int i = 0; i < MAX_AVAILABLE; i++) {
            BAN_DO_KHO_BAUS.add(new BanDoKhoBau(i));
        }
    }

    public int id;
    public byte level;
    public final List<Zone> zones;

    public Clan clan;
    public boolean isOpened;
    public long lastTimeOpen;

    public BanDoKhoBau(int id) {
        this.id = id;
        this.zones = new ArrayList<>();
    }

    public void update() {
        if (this.isOpened) {
            System.out.println("Go TO BDKB \n" + lastTimeOpen + "\n Count " + TIME_BAN_DO_KHO_BAU);
            System.out.println(System.currentTimeMillis() - lastTimeOpen);

            if (Util.canDoWithTime(lastTimeOpen, TIME_BAN_DO_KHO_BAU)) {
                // if (System.currentTimeMillis() - lastTimeOpen ==  0) {

                System.out.println("Outtt BDKB");
                finish();
                isOpened = false;
            }
        }
    }

    public void openBanDoKhoBau(Player plOpen, Clan clan, byte level) {
        this.level = level;
        this.lastTimeOpen = System.currentTimeMillis();
        this.isOpened = true;
        this.clan = clan;
        this.clan.timeOpenBanDoKhoBau = this.lastTimeOpen;
        this.clan.playerOpenBanDoKhoBau = plOpen;
        this.clan.banDoKhoBau = this;
        this.update();
        resetBanDo();
        ChangeMapService.gI().goToDBKB(plOpen);
        sendTextBanDoKhoBau();
    }

    private void resetBanDo() {
        for (Zone zone : zones) {
            for (TrapMap trap : zone.trapMaps) {
                trap.dame = this.level * 10000;
            }
        }
        for (Zone zone : zones) {
            for (Mob m : zone.mobs) {
                Mob.initMobBanDoKhoBau(m, this.level);
                Mob.hoiSinhMob(m);
            }
        }
    }

    //kết thúc bản đồ kho báu
    public void finish() {

        for (Player pl : this.clan.membersInGame) {
            if (pl != null && pl.isPl()) {
      
                    ChangeMapService.gI().changeMapBySpaceShip(pl, 5, -1, 64);
                
            }

        }

        System.out.println("Tafu bat");
        this.clan.banDoKhoBau = null;
        this.clan = null;
        this.isOpened = false;
    }

    public Zone getMapById(int mapId) {
        for (Zone zone : zones) {
            if (zone.map.mapId == mapId) {
                return zone;
            }
        }
        return null;
    }

    public static void addZone(int idBanDo, Zone zone) {
        BAN_DO_KHO_BAUS.get(idBanDo).zones.add(zone);
    }

    private void sendTextBanDoKhoBau() {
        for (Player pl : this.clan.membersInGame) {
            ItemTimeService.gI().sendTextBanDoKhoBau(pl);
        }
    }
}
