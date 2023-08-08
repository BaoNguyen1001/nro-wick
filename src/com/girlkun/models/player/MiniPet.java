package com.girlkun.models.player;

import com.girlkun.consts.ConstPlayer;
import com.girlkun.models.mob.Mob;
import com.girlkun.services.MapService;
import com.girlkun.services.Service;
import com.girlkun.services.func.ChangeMapService;
import com.girlkun.utils.Util;
import com.girlkun.services.PlayerService;

/**
 * @author outcast c-cute hột me 😳
 */
public class MiniPet extends Player {

    public static final byte FOLLOW = 0;
    public static final byte PROTECT = 1;
    public static final byte ATTACK = 2;
    public static final byte GOHOME = 3;
    public static final byte FUSION = 4;
    public int PETID = -1;
    public Player master;
    public byte status = 0;
    private boolean goingHome;


    public static void callMiniPet(Player pl, int iditem) {
        if (pl.minipet == null) {
            init(pl);
            pl.minipet.setByPetId(getMiniPetById(iditem));
            pl.minipet.changeStatus(FOLLOW);
        } else {
            pl.minipet.setByPetId(getMiniPetById(iditem));
            pl.minipet.changeStatus(FOLLOW);
        }
        Service.getInstance().point(pl);
    }

    public byte getStatus() {
        return this.status;
    }
    public void setByPetId(int id) {
        this.PETID = id;
    }

    public MiniPet(Player master) {
        this.master = master;
        this.isMiniPet = true;
    }







    public void changeStatus(byte status) {
//        if (master.minipet != null) {
//            reCall();
//        }
//        if (status == GOHOME) {
//            goHome();
//        }
        if (status == GOHOME) {
            goHome();
        }
        this.status = status;
    }

    public void goHome() {
        if (this.status == GOHOME) {
            return;
        }
        goingHome = true;
        new Thread(() -> {
            try {
                MiniPet.this.status = Pet.ATTACK;
                //Thread.sleep(2000);
            } catch (Exception e) {
            }
            ChangeMapService.gI().goToMap(this, MapService.gI().getMapCanJoin(this, master.gender + 21,-1));
            this.zone.load_Me_To_Another(this);
            MiniPet.this.status = Pet.GOHOME;
            goingHome = false;
        }).start();
    }
    public void reCall() {
        ChangeMapService.gI().goToMap(this, MapService.gI().getMapCanJoin(this, master.gender + 21, master.zone.zoneId));
        this.zone.load_Me_To_Another(this);
    }

//    public void joinMapMaster() {
//        this.location.x = master.location.x + Util.nextInt(-10, 10);
//        this.location.y = master.location.y;
//        ChangeMapService.gI().goToMap(this, master.zone);
//        this.zone.load_Me_To_Another(this);
//    }

    public void joinMapMaster() {
        if (status != GOHOME && !isDie()) {
            this.location.x = master.location.x + Util.nextInt(-20, 20);
            this.location.y = master.location.y;
            ChangeMapService.gI().goToMap(this, master.zone);
            this.zone.load_Me_To_Another(this);
        }
    }

    public long lastTimeMoveIdle;
    private int timeMoveIdle;
    public boolean idle;

    private void moveIdle() {
        if (status == GOHOME) {
            return;
        }
        if (idle && Util.canDoWithTime(lastTimeMoveIdle, timeMoveIdle)) {
            int dir = this.location.x - master.location.x <= 0 ? -1 : 1;
            PlayerService.gI().playerMove(this, master.location.x
                    + Util.nextInt(dir == -1 ? 30 : -50, dir == -1 ? 50 : 30), master.location.y);
            lastTimeMoveIdle = System.currentTimeMillis();
            timeMoveIdle = Util.nextInt(5000, 8000);
        }
    }

    private long lastTimeMoveAtHome;
    private byte directAtHome = -1;

    @Override
    public void update() {
        try {
            super.update();
            if (this.zone == null || this.zone != master.zone) {
                joinMapMaster();
            }
            moveIdle();
            //System.out.println("Gọi pet");
            switch (status) {
                case FOLLOW:
                    //followMaster(60);
                    break;
                case GOHOME:
                    if (this.zone != null && (this.zone.map.mapId == 21 || this.zone.map.mapId == 22 || this.zone.map.mapId == 23)) {
                        if (System.currentTimeMillis() - lastTimeMoveAtHome <= 5000) {
                            return;
                        } else {
                            if (this.zone.map.mapId == 21) {
                                if (directAtHome == -1) {
                                    PlayerService.gI().playerMove(this, 250, 336);
                                    directAtHome = 1;
                                } else {
                                    PlayerService.gI().playerMove(this, 200, 336);
                                    directAtHome = -1;
                                }
                            } else if (this.zone.map.mapId == 22) {
                                if (directAtHome == -1) {
                                    PlayerService.gI().playerMove(this, 500, 336);
                                    directAtHome = 1;
                                } else {
                                    PlayerService.gI().playerMove(this, 452, 336);
                                    directAtHome = -1;
                                }
                            } else if (this.zone.map.mapId == 22) {
                                if (directAtHome == -1) {
                                    PlayerService.gI().playerMove(this, 250, 336);
                                    directAtHome = 1;
                                } else {
                                    PlayerService.gI().playerMove(this, 200, 336);
                                    directAtHome = -1;
                                }
                            }
                            Service.getInstance().chatJustForMe(master, this, "H2O + C12H22O11 -> Uống ngọt lắm sư phụ ạ!");
                            lastTimeMoveAtHome = System.currentTimeMillis();
                        }
                    }
                    break;

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void followMaster() {
        switch (this.status) {
            case FOLLOW:
                followMaster(80);
                break;
        }
    }

    private void followMaster(int dis) {
        int mX = master.location.x;
        int mY = master.location.y;
        int disX = this.location.x - mX;
        if (Math.sqrt(Math.pow(mX - this.location.x, 2) + Math.pow(mY - this.location.y, 2)) >= dis) {
            if (disX < 0) {
                this.location.x = mX - Util.nextInt(dis - 1, dis);
            } else {
                this.location.x = mX + Util.nextInt(dis - 1, dis);
            }
            this.location.y = mY;
            PlayerService.gI().playerMove(this, this.location.x, this.location.y);
        }
    }

    public static void init(Player player) {
        MiniPet minipet = new MiniPet(player);
        minipet.name = "# ";
        minipet.gender = 0;
        minipet.id = player.id - 10000;
        minipet.nPoint.hpMax = 5000000;
        minipet.nPoint.hpg = 5000000;
        minipet.nPoint.hp = 5000000;
        minipet.nPoint.setFullHpMp();
        player.minipet = minipet;
    }

    public static int getMiniPetById(int iditem) {
        switch (iditem) {
            case -1:
                return 0;
            case 936: //tloc
                return 1;
            case 892://tho xam
                return 2;
            case 893://tho trang
                return 3;
            case 942:
                return 4;
            case 943:
                return 5;
            case 944:
                return 6;
            case 967:
                return 7;
            case 1039:
                return 8;
            case 1040:
                return 9;
            case 916:
                return 10;
            case 917:
                return 11;
            case 918:
                return 12;
            case 919:
                return 13;
            case 1107:
                return 14;
            case 2045:
                return 15;
            case 2046:
                return 16;
            case 2047:
                return 17;
            case 2048:
                return 18;
            case 2017:
                return 19;
            case 2018:
                return 20;
            case 2019:
                return 21;
            case 2020:
                return 22;
            case 2021:
                return 23;
            case 2022:
                return 24;
            case 2023:
                return 25;
            case 2024:
                return 26;
            case 2025:
                return 27;
            case 2053:
                return 28;
            case 1128:
                return 29;
            case 1145:
                return 30;
            case 1148:
                return 31;
            case 1153:
                return 32;  
            case 1154:
                return 33;     
        }
        return -1;
    }

    private Mob mobAttack;
    @Override
    public void dispose() {
        this.mobAttack = null;
        this.master = null;
        super.dispose();
    }//763 764 765

    @Override
    public short getHead() {
        switch (PETID) {
            case 0:
                return -1;
            case 1:
                return 718;//tuần lộc
            case 2:
                return 882;
            case 3:
                return 885;
            case 4:
                return 966;
            case 5:
                return 969;
            case 6:
                return 972;
            case 7:
                return 1050;
            case 8:
                return 1089;
            case 9:
                return 1092;
            case 10:
                return 931;//3 giác
            case 11:
                return 928;//vuông
            case 12:
                return 925;//tròn
            case 13:
                return 934;//búp bê
            case 14:
                return 1155;
            case 15:
                return 2060;
            case 16:
                return 2063;
            case 17:
                return 2066;
            case 18:
                return 2069;
            case 19:
                return 778;
            case 20:
                return 813;
            case 21:
                return 891;
            case 22:
                return 894;
            case 23:
                return 897;
            case 24:
                return 925;
            case 25:
                return 928;
            case 26:
                return 931;
            case 27:
                return 934;
            case 28:
                return 1227;
            case 29:
                return 1285;
            case 30:
                return 763;
            case 31:
                return 813;
            case 32:
                return 1313;
            case 33:
                return 1316;    
        }
        return 0;
    }

    @Override
    public short getBody() {
        switch (PETID) {
            case 0:
                return -1;
            case 1:
                return 719;//tuần lộc
            case 2:
                return 883;
            case 3:
                return 886;
            case 4:
                return 967;
            case 5:
                return 970;
            case 6:
                return 973;
            case 7:
                return 1051;
            case 8:
                return 1090;
            case 9:
                return 1093;
            case 10:
                return 932;//tam giác
            case 11:
                return 929;//vuông
            case 12:
                return 926;
            case 13:
                return 935;
            case 14:
                return 1156;
            case 15:
                return 2061;
            case 16:
                return 2064;
            case 17:
                return 2067;
            case 18:
                return 2070;
            case 19:
                return 779;
            case 20:
                return 814;
            case 21:
                return 892;
            case 22:
                return 895;
            case 23:
                return 898;
            case 24:
                return 926;
            case 25:
                return 929;
            case 26:
                return 932;
            case 27:
                return 935;
            case 28:
                return 1228;
            case 29:
                return 1286;
            case 30:
                return 764;
            case 31:
                return 814;
            case 32:
                return 1314;
            case 33:
                return 1317;    
        }
        return 0;
    }

    @Override
    public short getLeg() {
        switch (PETID) {
            case 0:
                return -1;
            case 1:
                return 720;//tuần lộc
            case 2:
                return 884;
            case 3:
                return 887;
            case 4:
                return 968;
            case 5:
                return 971;
            case 6:
                return 974;
            case 7:
                return 1052;
            case 8:
                return 1091;
            case 9:
                return 1094;
            case 10:
                return 933;
            case 11:
                return 930;
            case 12:
                return 927;
            case 13:
                return 936;
            case 14:
                return 1157;
            case 15:
                return 2062;
            case 16:
                return 2065;
            case 17:
                return 2068;
            case 18:
                return 2071;
            case 19:
                return 780;
            case 20:
                return 815;
            case 21:
                return 893;
            case 22:
                return 896;
            case 23:
                return 899;
            case 24:
                return 927;
            case 25:
                return 930;
            case 26:
                return 933;
            case 27:
                return 936;
            case 28:
                return 1229;
            case 29:
                return 1287;
            case 30:
                return 765;
            case 31:
                return 815;
            case 32:
                return 1315;
            case 33:
                return 1318;    
        }
        return -1;
    }
}