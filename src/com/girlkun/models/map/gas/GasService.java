package com.girlkun.models.map.gas;

//import com.girlkun.models.boss.bdkb.TrungUyXanhLo;
import com.girlkun.models.item.Item;
import com.girlkun.models.player.Player;
import com.girlkun.services.InventoryServiceNew;
import com.girlkun.services.Service;
import com.girlkun.utils.Logger;

/**
 *
 * @author BTH
 *
 */
public class GasService {

    private static GasService i;

    private GasService() {

    }

    public static GasService gI() {
        if (i == null) {
            i = new GasService();
        }
        return i;
    }


    public void openBanDoKhoBau(Player player, byte level) {
        if (level >= 1 && level <= 110) {
            if (player.clan != null && player.clan.khiGas == null) {
             
                    Gas gas = null;
                    for (Gas bdkb : Gas.KHI_GAS) {
                        if (!bdkb.isOpened) {
                            gas = bdkb;
                            break;
                        }
                    }
                    if (gas != null) {
                      
                        InventoryServiceNew.gI().sendItemBags(player);
                        gas.openKhiGas(player, player.clan, level);
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
 //                           new TrungUyXanhLo(player.clan.banDoKhoBau.getMapById(137), player.clan.banDoKhoBau.level, (int) dame, (int) hp);
                        } catch (Exception e) {
                            Logger.logException(GasService.class, e, "Lỗi init boss");
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
       
        }
    }
