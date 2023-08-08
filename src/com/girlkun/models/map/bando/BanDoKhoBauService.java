package com.girlkun.models.map.bando;
//import com.girlkun.models.boss.bdkb.TrungUyXanhLo;

import com.girlkun.models.item.Item;
import com.girlkun.models.map.Zone;
import static com.girlkun.models.map.bando.BanDoKhoBau.TIME_BAN_DO_KHO_BAU;
import com.girlkun.models.player.Player;
import com.girlkun.services.InventoryServiceNew;
import com.girlkun.services.Service;
import com.girlkun.services.func.ChangeMapService;
import com.girlkun.utils.Logger;
import com.girlkun.utils.Util;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author BTH
 *
 */
public class BanDoKhoBauService extends Thread {

    private static BanDoKhoBauService i;

    public BanDoKhoBauService() {

    }

    public static BanDoKhoBauService gI() {
        if (i == null) {
            i = new BanDoKhoBauService();
        }
        return i;
    }

    public static List<BanDoKhoBau> banDoKhoBaus = new ArrayList<BanDoKhoBau>();
    
    public void run() {
        while (true) {
            try {
                if (banDoKhoBaus != null && banDoKhoBaus.size() > 0) {
                    for (BanDoKhoBau banDoKhoBau : banDoKhoBaus) {
                        if (banDoKhoBau != null && banDoKhoBau.isOpened) {
                            banDoKhoBau.update();
                            Thread.sleep(50);
                        }
                    }
                }
                Thread.sleep(1000);
            } catch (Exception e) {

            }

        }
    }

    public void openBanDoKhoBau(Player player, byte level) {
        if (level >= 1 && level <= 110) {
            if (player.clan != null && player.clan.banDoKhoBau == null) {
                Item item = InventoryServiceNew.gI().findItemBag(player, 611);
                if (item != null && item.quantity > 0) {
                    BanDoKhoBau banDoKhoBau = null;
                    for (BanDoKhoBau bdkb : BanDoKhoBau.BAN_DO_KHO_BAUS) {
                        if (!bdkb.isOpened) {
                            banDoKhoBau = bdkb;
                            banDoKhoBaus.remove(bdkb);
                            break;
                        }
                    }
                    if (banDoKhoBau != null) {
                        InventoryServiceNew.gI().subQuantityItemsBag(player, item, 1);
                        InventoryServiceNew.gI().sendItemBags(player);
                        banDoKhoBau.openBanDoKhoBau(player, player.clan, level);
                        banDoKhoBaus.add(banDoKhoBau);
                        try {
                            long totalDame = 0;
                            long totalHp = 0;
                            for (Player play : player.clan.membersInGame) {
                                totalDame += play.nPoint.dame;
                                totalHp += play.nPoint.hpMax;
                            }
                            long dame = (totalHp / 20) * (level);
                            long hp = (totalDame * 4) * (level);
                            if (dame >= 2000000000L) {
                                dame = 2000000000L;
                            }
                            if (hp >= 2000000000L) {
                                hp = 2000000000L;
                            }
                            //new TrungUyXanhLo(player.clan.banDoKhoBau.getMapById(137), player.clan.banDoKhoBau.level, (int) dame, (int) hp);
                        } catch (Exception e) {
                            Logger.logException(BanDoKhoBauService.class, e, "Lỗi init boss");
                        }
                    } else {
                        Service.getInstance().sendThongBao(player, "Bản đồ kho báu đã đầy, vui lòng quay lại sau");
                    }
                } else {
                    Service.getInstance().sendThongBao(player, "Yêu cầu có bản đồ kho báu");
                }
            } else {
                Service.getInstance().sendThongBao(player, "Không thể thực hiện");
            }
        } else {
            Service.getInstance().sendThongBao(player, "Không thể thực hiện");
        }
    }

}
