package com.girlkun.services.func;

import com.girlkun.database.GirlkunDB;
import com.girlkun.consts.ConstNpc;
import com.girlkun.jdbc.daos.GodGK;
import com.girlkun.jdbc.daos.PlayerDAO;
import com.girlkun.models.item.Item;
import com.girlkun.models.map.Zone;
import com.girlkun.models.npc.Npc;
import static com.girlkun.models.npc.NpcFactory.PLAYERID_OBJECT;
import com.girlkun.models.npc.NpcManager;
import com.girlkun.models.player.Inventory;
import com.girlkun.models.player.Player;
import com.girlkun.network.io.Message;
import com.girlkun.network.session.ISession;
import com.girlkun.result.GirlkunResultSet;
import com.girlkun.server.Client;
import com.girlkun.server.Maintenance;
import com.girlkun.services.Service;
import com.girlkun.services.GiftService;
import com.girlkun.services.InventoryServiceNew;
import com.girlkun.services.ItemService;
//import com.girlkun.services.NapThe;
import com.girlkun.services.NpcService;
import com.girlkun.utils.Util;

import java.util.HashMap;
import java.util.Map;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class Input {
    
    private static final Map<Integer, Object> PLAYER_ID_OBJECT = new HashMap<Integer, Object>();
    
    public static final int CHANGE_PASSWORD = 500;
    public static final int GIFT_CODE = 501;
    public static final int FIND_PLAYER = 552;
    public static final int CHANGE_NAME = 503;
    public static final int CHOOSE_LEVEL_BDKB = 504;
    public static final int CHOOSE_LEVEL_GAS = 512;
    public static final int NAP_THE = 505;
    public static final int CHANGE_NAME_BY_ITEM = 506;
    public static final int GIVE_IT = 507;
    public static final int DOIDANS = 2510;
    public static final int DOITHOIVANG = 2511;
    public static final int DOIHONGNGOC = 2512;
    public static final int TAI = 510;
    public static final int XIU = 511;
    public static final byte NUMERIC = 0;
    public static final byte ANY = 1;
    public static final byte PASSWORD = 2;
    public static final int FIND_PLAYER_PHU = 553;
    public static final int SEND_ITEM = 554;
    public static final int SEND_ITEM_OP = 555;
    public static final int SEND_ITEM_SKH = 556;
    public static final int SEND_THOI_VANG = 557;
    public static final int SEND_ITEM_GOLDBAR = 558;
    
    private static Input intance;
    
    private Input() {
        
    }
    
    public static Input gI() {
        if (intance == null) {
            intance = new Input();
        }
        return intance;
    }
    
    public void doInput(Player player, Message msg) {
        try {
            String[] text = new String[msg.reader().readByte()];
            for (int i = 0; i < text.length; i++) {
                text[i] = msg.reader().readUTF();
            }
            switch (player.iDMark.getTypeInput()) {
                case GIVE_IT:
                    String namee = text[0];
                    int id = Integer.valueOf(text[1]);
                    int q = Integer.valueOf(text[2]);
                    if (Client.gI().getPlayer(namee) != null) {
                        Item item = ItemService.gI().createNewItem(((short) id));
                        item.quantity = q;
                        InventoryServiceNew.gI().addItemBag(Client.gI().getPlayer(namee), item);
                        InventoryServiceNew.gI().sendItemBags(Client.gI().getPlayer(namee));
                        Service.getInstance().sendThongBao(Client.gI().getPlayer(namee), "Nhận " + item.template.name + " từ " + player.name);
                        
                    } else {
                        Service.getInstance().sendThongBao(player, "Không online");
                    }
                    break;
//                case NAP_THE:
//                    NapThe.gI().napThe(player, text[0], text[1]);
//                    break;
                case CHANGE_PASSWORD:
                    Service.getInstance().changePassword(player, text[0], text[1], text[2]);
                    break;
                case GIFT_CODE: {
                    String textLevel = text[0];
                    Input.gI().addItemGiftCodeToPlayer(player, textLevel);
                    break;
                }
                case SEND_ITEM_GOLDBAR: {
                    if (Maintenance.isRuning) {
                        break;
                    }
                    Player p = (Player) PLAYERID_OBJECT.get(player.id);
                    if (p != null) {
                        String name = p.getSession().uu;
                        PlayerDAO.addGoldBar(p, Integer.parseInt(text[0]), p.getSession().uu);

//                        Thread.sleep(1000);
                        String txtBuff = "|2|Buff goldbar đến tài khoản: " + name + "/Id: " + p.getSession().userId + "/Tên nhân vật: " + p.getSession().player.name + "\b";
                        String txtBuffpl = "|2|Bạn nhận được: " + Integer.parseInt(text[0]) + " thỏi từ ADMIN\b"
                                + "|1|Số lượng thỏi trong tài khoản của bạn hiện tại: " + p.getSession().goldBar;
                        txtBuff += "|2|Số lượng: " + Integer.parseInt(text[0]) + " thỏi\b"
                                + "|1|Số lượng thỏi trong tài khoản " + p.getSession().uu + " hiện tại: " + p.getSession().goldBar;
                        NpcService.gI().createTutorial(player, 24, txtBuff);
                        NpcService.gI().createTutorial(p, 24, txtBuffpl);
                    } else {
                        NpcService.gI().createTutorial(player, 24, "Không tìm thấy player");
                    }
                }
                break;
                
                case TAI:
                    int sotvxiu = Integer.valueOf(text[0]);
                    Item tv2 = null;
                    for (Item item : player.inventory.itemsBag) {
                        if (item.isNotNullItem() && item.template.id == 457) {
                            tv2 = item;
                            break;
                        }
                    }
                    try {
                        if (tv2 != null && tv2.quantity >= sotvxiu) {
                            InventoryServiceNew.gI().subQuantityItemsBag(player, tv2, sotvxiu);
                            InventoryServiceNew.gI().sendItemBags(player);
                            int TimeSeconds = 10;
                            Service.gI().sendThongBao(player, "Chờ 10 giây để biết kết quả.");
                            while (TimeSeconds > 0) {
                                TimeSeconds--;
                                Thread.sleep(1000);
                            }
                            int x = Util.nextInt(1, 6);
                            int y = Util.nextInt(1, 6);
                            int z = Util.nextInt(1, 6);
                            int tong = (x + y + z);
                            if (4 <= (x + y + z) && (x + y + z) <= 10) {
                                if (player != null) {
                                    Item tvthang = ItemService.gI().createNewItem((short) 457);
                                    tvthang.quantity = (int) Math.round(sotvxiu * 1.5);
                                    InventoryServiceNew.gI().addItemBag(player, tvthang);
                                    InventoryServiceNew.gI().sendItemBags(player);
                                    Service.gI().sendThongBaoOK(player, "Kết quả" + "\nSố hệ thống quay ra : " + x + " "
                                            + y + " " + z + "\nTổng là : " + tong + "\nBạn đã cược : " + sotvxiu
                                            + " thỏi vàng vào Xỉu 1" + "\nKết quả : Xỉu" + "\n\nVề bờ");
                                    return;
                                }
                            } else if (x == y && x == z) {
                                if (player != null) {
                                    Service.gI().sendThongBaoOK(player, "Kết quả" + "Số hệ thống quay ra : " + x + " " + y + " " + z + "\nTổng là : " + tong + "\nBạn đã cược : " + sotvxiu + " thỏi vàng vào Xỉu" + "\nKết quả : Tam hoa" + "\nCòn cái nịt.");
                                    return;
                                }
                            } else if ((x + y + z) > 10) {
                                if (player != null) {
                                    Service.gI().sendThongBaoOK(player, "Kết quả" + "\nSố hệ thống quay ra là :"
                                            + " " + x + " " + y + " " + z + "\nTổng là : " + tong + "\nBạn đã cược : "
                                            + sotvxiu + " thỏi vàng vào Xỉu 2" + "\nKết quả : Tài" + "\nCòn cái nịt.");
                                    return;
                                }
                            }
                        } else {
                            Service.gI().sendThongBao(player, "Bạn không đủ thỏi vàng để chơi.");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Service.gI().sendThongBao(player, "Lỗi.");
                    }
                case XIU:
                    int sotvtai = Integer.valueOf(text[0]);
                    Item tv1 = null;
                    for (Item item : player.inventory.itemsBag) {
                        if (item.isNotNullItem() && item.template.id == 457) {
                            tv1 = item;
                            break;
                        }
                    }
                    try {
                        if (tv1 != null && tv1.quantity >= sotvtai) {
                            InventoryServiceNew.gI().subQuantityItemsBag(player, tv1, sotvtai);
                            InventoryServiceNew.gI().sendItemBags(player);
                            int TimeSeconds = 10;
                            Service.gI().sendThongBao(player, "Chờ 10 giây để biết kết quả.");
                            while (TimeSeconds > 0) {
                                TimeSeconds--;
                                Thread.sleep(1000);
                            }
                            int x = Util.nextInt(1, 6);
                            int y = Util.nextInt(1, 6);
                            int z = Util.nextInt(1, 6);
                            int tong = (x + y + z);
                            if (4 <= (x + y + z) && (x + y + z) <= 10) {
                                if (player != null) {// tự sửa lại tên lấy
                                    Service.gI().sendThongBaoOK(player, "Kết quả" + "\nSố hệ thống quay ra là :"
                                            + " " + x + " " + y + " " + z + "\nTổng là : " + tong + "\nBạn đã cược : "
                                            + sotvtai + " thỏi vàng vào Tài 1" + "\nKết quả : Xỉu " + "\nCòn cái nịt.");
                                    return;
                                }
                            } else if (x == y && x == z) {
                                if (player != null) {
                                    Service.gI().sendThongBaoOK(player, "Kết quả" + "Số hệ thống quay ra : " + x + " " + y + " " + z + "\nTổng là : " + tong + "\nBạn đã cược : " + sotvtai + " thỏi vàng vào Xỉu" + "\nKết quả : Tam hoa" + "\nCòn cái nịt.");
                                    return;
                                }
                            } else if ((x + y + z) > 10) {
                                
                                if (player != null) {
                                    Item tvthang = ItemService.gI().createNewItem((short) 457);
                                    tvthang.quantity = (int) Math.round(sotvtai * 1.5);
                                    InventoryServiceNew.gI().addItemBag(player, tvthang);
                                    InventoryServiceNew.gI().sendItemBags(player);
                                    Service.gI().sendThongBaoOK(player, "Kết quả" + "\nSố hệ thống quay ra : " + x + " "
                                            + y + " " + z + "\nTổng là : " + tong + "\nBạn đã cược : " + sotvtai
                                            + " thỏi vàng vào Tài 2 " + "\nKết quả : Tài" + "\n\nVề bờ");
                                    return;
                                }
                            }
                        } else {
                            Service.gI().sendThongBao(player, "Bạn không đủ thỏi vàng để chơi.");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Service.gI().sendThongBao(player, "Lỗi.");
                    }
                    break;
                case DOIDANS:
                    
                    if (player != null) {
                        
                        try {
                            int sl = Math.abs(Integer.parseInt(text[0]));
                            int money = GodGK.GetMoney((player.getSession().userId));
                            if (money > 0 && money > sl * 1000) {
                                GodGK.setMoney(player, GodGK.GetMoney(player.getSession().userId) - sl * 1000);
                                
                                Item dns = ItemService.gI().createNewItem((short) 674);
                                dns.quantity = sl;
                                InventoryServiceNew.gI().addItemBag(player, dns);
                                InventoryServiceNew.gI().sendItemBags(player);
                                Service.gI().sendThongBao(player, "Ban da doi thanh cong x" + sl + " da ngu sac");
                                
                            } else {
                                Service.gI().sendThongBao(player, "Ban khoong du tien");
                            }
                        } catch (Exception e) {
                            Service.gI().sendThongBao(player, "Ban nhap sai so luong");
                        }
                        
                    }
                    break;
                
                case DOITHOIVANG:
                    
                    if (player != null) {
                        
                        try {
                            int sl = Math.abs(Integer.parseInt(text[0]));
                            int money = GodGK.GetMoney((player.getSession().userId));
                            int rate = 200;
                            if (money > 0 && money > sl * rate) {
                                GodGK.setMoney(player, GodGK.GetMoney(player.getSession().userId) - sl * rate);
                                
                                Item dns = ItemService.gI().createNewItem((short) 457);
                                dns.quantity = sl;
                                InventoryServiceNew.gI().addItemBag(player, dns);
                                InventoryServiceNew.gI().sendItemBags(player);
                                Service.gI().sendThongBao(player, "Ban da doi thanh cong x" + sl + "" + dns.template.name);
                                
                            } else {
                                Service.gI().sendThongBao(player, "Ban khoong du tien");
                            }
                        } catch (Exception e) {
                            Service.gI().sendThongBao(player, "Ban nhap sai so luong");
                        }
                        
                    }
                    break;
                case DOIHONGNGOC:
                    
                    if (player != null) {
                        
                        try {
                            int sl = Math.abs(Integer.parseInt(text[0]));
                            int money = GodGK.GetMoney((player.getSession().userId));
                            int rate = 200;
                            if (money > 0 && money > sl * rate) {
                                GodGK.setMoney(player, GodGK.GetMoney(player.getSession().userId) - sl * rate);
                                
                                Item dns = ItemService.gI().createNewItem((short) 861);
                                dns.quantity = sl;
                                InventoryServiceNew.gI().addItemBag(player, dns);
                                InventoryServiceNew.gI().sendItemBags(player);
                                Service.gI().sendThongBao(player, "Ban da doi thanh cong x" + sl + "" + dns.template.name);
                                
                            } else {
                                Service.gI().sendThongBao(player, "Ban khoong du tien");
                            }
                        } catch (Exception e) {
                            Service.gI().sendThongBao(player, "Ban nhap sai so luong");
                        }
                        
                    }
                    break;    
                case SEND_THOI_VANG: {
                    if (Maintenance.isRuning) {
                        break;
                    }
                    if (player.getSession().goldBar > 0) {
                        int checkthoi = (player.getSession().goldBar - Integer.parseInt(text[0]));
                        if (player.getSession().actived == false) {
                            if (checkthoi < 20) {
                                NpcService.gI().createTutorial(player, 24, "|1|Hiện tại bạn chưa kích hoạt thành viên\b"
                                        + "|7|Hãy chừa lại 20 thỏi vàng\b"
                                        + "|1|để kích hoạt thành viên tại Quy lão Kame!");
                                return;
                            }
                        }
                        if (InventoryServiceNew.gI().getCountEmptyBag(player) > 0) {
                            
                            int goldBefore = player.getSession().goldBar;
                            
                            int quatidyitembeforebag = 0;
                            int quatidyitemafterbag = 0;
                            
                            int quatidyitembeforebox = 0;
                            int quatidyitemafterbox = 0;
                            
                            for (Item itembeforebag : player.inventory.itemsBag) {
                                if (itembeforebag.isNotNullItem() && itembeforebag.template.id == 457) {
                                    quatidyitembeforebag = itembeforebag.quantity;
                                }
                            }
                            
                            for (Item itembeforebox : player.inventory.itemsBox) {
                                if (itembeforebox.isNotNullItem() && itembeforebox.template.id == 457) {
                                    quatidyitembeforebox = itembeforebox.quantity;
                                }
                            }
                            
                            int soluong = Integer.parseInt(text[0]);
                            if (text[0].contains("-")) {
                                NpcService.gI().createTutorial(player, 24, "|7|Số lượng không hợp lệ");
                                return;
                            }

//                            if (Util.isNumeric(text[0])) {
//                                NpcService.gI().createTutorial(player,24, "|7|Chỉ được nhập số");
//                                return;
//                            }
                            if (soluong <= 0) {
                                NpcService.gI().createTutorial(player, 24, "|7|Giá trị nhập vào không đúng");
                                break;
                            }
                            if (soluong > player.getSession().goldBar) {
                                NpcService.gI().createTutorial(player, 24, "|1|Hiện con đang không đủ thỏi vàng\b"
                                        + "|7|hãy liên hệ ADMIN 'B O'\b"
                                        + "|1|để nạp thỏi vàng nhé!");
                                return;
                            }
                            if (PlayerDAO.subGoldBar(player, soluong)) {
                                int goldAfter = player.getSession().goldBar;
                                
                                Item goldBar = ItemService.gI().createNewItem((short) 457, Integer.parseInt(text[0]));
                                InventoryServiceNew.gI().addItemBag(player, goldBar);
                                InventoryServiceNew.gI().sendItemBags(player);
                                
                                for (Item itemafterbag : player.inventory.itemsBag) {
                                    if (itemafterbag.isNotNullItem() && itemafterbag.template.id == 457) {
                                        quatidyitemafterbag = itemafterbag.quantity;
                                    }
                                }
                                
                                for (Item itemafterbox : player.inventory.itemsBox) {
                                    if (itemafterbox.isNotNullItem() && itemafterbox.template.id == 457) {
                                        quatidyitemafterbox = itemafterbox.quantity;
                                    }
                                }
                                NpcService.gI().createTutorial(player, 24, "|1|Ta đã gửi " + Integer.parseInt(text[0]) + " thỏi vàng vào hành trang của con\b con hãy kiểm tra ");
                                PlayerDAO.addHistoryReceiveGoldBar(player, goldBefore, goldAfter, quatidyitembeforebag, quatidyitemafterbag, quatidyitembeforebox, quatidyitemafterbox);
                            } else {
                                NpcService.gI().createTutorial(player, 24, "|7|Lỗi vui lòng báo admin...");
                            }
                        } else {
                            NpcService.gI().createTutorial(player, 24, "|2|Hãy chừa cho ta 1 ô trống");
                        }
                    } else {
                        NpcService.gI().createTutorial(player, 24, "|1|Con đang không có thỏi vàng\b"
                                + "|7|hãy liên hệ ADMIN 'B O'\b"
                                + "|1|để nạp thỏi vàng nhé!");
                    }
                }
                break;
                case FIND_PLAYER_PHU: {
                    Player pl = Client.gI().getPlayer(text[0]);
                    if (pl != null) {
                        NpcService.gI().createMenuConMeo(player, ConstNpc.MENU_FIND_PLAYER_PHU, -1, "Lựa chọn muốn..?",
                                new String[]{"Ban", "Mở thành viên"},
                                pl);
                    } else {
                        Service.getInstance().sendThongBao(player, "Người chơi không tồn tại hoặc đang offline");
                    }
                }
                break;
                case FIND_PLAYER:
                    Player pl = Client.gI().getPlayer(text[0]);
                    if (pl != null) {
                        NpcService.gI().createMenuConMeo(player, ConstNpc.MENU_FIND_PLAYER, -1, "Ngài muốn..?",
                                new String[]{"Đi tới\n" + pl.name, "Gọi " + pl.name + "\ntới đây", "Đổi tên", "Ban", "Kick", "Kích hoạt tài khoản", "Thỏi Vàng"},
                                pl);
                    } else {
                        Service.getInstance().sendThongBao(player, "Người chơi không tồn tại hoặc đang offline");
                    }
                    break;
                case CHANGE_NAME: {
                    Player plChanged = (Player) PLAYER_ID_OBJECT.get((int) player.id);
                    if (plChanged != null) {
                        if (GirlkunDB.executeQuery("select * from player where name = ?", text[0]).next()) {
                            Service.getInstance().sendThongBao(player, "Tên nhân vật đã tồn tại");
                        } else {
                            plChanged.name = text[0];
                            GirlkunDB.executeUpdate("update player set name = ? where id = ?", plChanged.name, plChanged.id);
                            Service.getInstance().player(plChanged);
                            Service.getInstance().Send_Caitrang(plChanged);
                            Service.getInstance().sendFlagBag(plChanged);
                            Zone zone = plChanged.zone;
                            ChangeMapService.gI().changeMap(plChanged, zone, plChanged.location.x, plChanged.location.y);
                            Service.getInstance().sendThongBao(plChanged, "Chúc mừng bạn đã có cái tên mới đẹp đẽ hơn tên ban đầu");
                            Service.getInstance().sendThongBao(player, "Đổi tên người chơi thành công");
                        }
                    }
                }
                break;
                case CHANGE_NAME_BY_ITEM: {
                    if (player != null) {
                        if (GirlkunDB.executeQuery("select * from player where name = ?", text[0]).next()) {
                            Service.getInstance().sendThongBao(player, "Tên nhân vật đã tồn tại");
                            createFormChangeNameByItem(player);
                        } else {
                            Item theDoiTen = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 2006);
                            if (theDoiTen == null) {
                                Service.getInstance().sendThongBao(player, "Không tìm thấy thẻ đổi tên");
                            } else {
                                InventoryServiceNew.gI().subQuantityItemsBag(player, theDoiTen, 1);
                                player.name = text[0];
                                GirlkunDB.executeUpdate("update player set name = ? where id = ?", player.name, player.id);
                                Service.getInstance().player(player);
                                Service.getInstance().Send_Caitrang(player);
                                Service.getInstance().sendFlagBag(player);
                                Zone zone = player.zone;
                                ChangeMapService.gI().changeMap(player, zone, player.location.x, player.location.y);
                                Service.getInstance().sendThongBao(player, "Chúc mừng bạn đã có cái tên mới đẹp đẽ hơn tên ban đầu");
                            }
                        }
                    }
                }
                break;
                case CHOOSE_LEVEL_BDKB:
                    int level = Integer.parseInt(text[0]);
                    if (level >= 1 && level <= 110) {
                        Npc npc = NpcManager.getByIdAndMap(ConstNpc.QUY_LAO_KAME, player.zone.map.mapId);
                        if (npc != null) {
                            npc.createOtherMenu(player, ConstNpc.MENU_ACCEPT_GO_TO_BDKB,
                                    "Con có chắc chắn muốn tới bản đồ kho báu cấp độ " + level + "?",
                                    new String[]{"Đồng ý", "Từ chối"}, level);
                        }
                    } else {
                        Service.getInstance().sendThongBao(player, "Không thể thực hiện");
                    }

//                    BanDoKhoBauService.gI().openBanDoKhoBau(player, (byte) );
                    break;
                case SEND_ITEM:
                    if (player.isAdmin()) {
                        int idItemBuff = Integer.parseInt(text[1]);
                        int quantityItemBuff = Integer.parseInt(text[2]);
                        Player pBuffItem = Client.gI().getPlayer(text[0]);
                        if (pBuffItem != null) {
                            String txtBuff = "Buff to player: " + pBuffItem.name + "\b";
                            if (idItemBuff == -1) {
                                pBuffItem.inventory.gold = Math.min(pBuffItem.inventory.gold + (long) quantityItemBuff, (Inventory.LIMIT_GOLD + pBuffItem.limitgold));
                                txtBuff += quantityItemBuff + " vàng\b";
                                Service.getInstance().sendMoney(player);
                            } else if (idItemBuff == -2) {
                                pBuffItem.inventory.gem = Math.min(pBuffItem.inventory.gem + quantityItemBuff, 2000000000);
                                txtBuff += quantityItemBuff + " ngọc\b";
                                Service.getInstance().sendMoney(player);
                            } else if (idItemBuff == -3) {
                                pBuffItem.inventory.ruby = Math.min(pBuffItem.inventory.ruby + quantityItemBuff, 2000000000);
                                txtBuff += quantityItemBuff + " ngọc khóa\b";
                                Service.getInstance().sendMoney(player);
                            } else {
                                Item itemBuffTemplate = ItemService.gI().createNewItem((short) idItemBuff);
                                itemBuffTemplate.quantity = quantityItemBuff;
                                InventoryServiceNew.gI().addItemBag(pBuffItem, itemBuffTemplate);
                                InventoryServiceNew.gI().sendItemBags(pBuffItem);
                                txtBuff += "x" + quantityItemBuff + " " + itemBuffTemplate.template.name + "\b";
                            }
                            NpcService.gI().createTutorial(player, 24, txtBuff);
                            if (player.id != pBuffItem.id) {
                                NpcService.gI().createTutorial(pBuffItem, 24, txtBuff);
                            }
                        } else {
                            Service.getInstance().sendThongBao(player, "Player không online");
                        }
                        break;
                    }
                    break;
                case SEND_ITEM_OP:
                    if (player.isAdmin()) {
                        int idItemBuff = Integer.parseInt(text[1]);
                        int idOptionBuff = Integer.parseInt(text[2]);
                        int slOptionBuff = Integer.parseInt(text[3]);
                        int slItemBuff = Integer.parseInt(text[4]);
                        Player pBuffItem = Client.gI().getPlayer(text[0]);
                        if (pBuffItem != null) {
                            String txtBuff = "Buff to player: " + pBuffItem.name + "\b";
                            if (idItemBuff == -1) {
                                pBuffItem.inventory.gold = Math.min(pBuffItem.inventory.gold + (long) slItemBuff, (Inventory.LIMIT_GOLD + pBuffItem.limitgold));
                                txtBuff += slItemBuff + " vàng\b";
                                Service.getInstance().sendMoney(player);
                            } else if (idItemBuff == -2) {
                                pBuffItem.inventory.gem = Math.min(pBuffItem.inventory.gem + slItemBuff, 2000000000);
                                txtBuff += slItemBuff + " ngọc\b";
                                Service.getInstance().sendMoney(player);
                            } else if (idItemBuff == -3) {
                                pBuffItem.inventory.ruby = Math.min(pBuffItem.inventory.ruby + slItemBuff, 2000000000);
                                txtBuff += slItemBuff + " ngọc khóa\b";
                                Service.getInstance().sendMoney(player);
                            } else {
                                //Item itemBuffTemplate = ItemBuff.getItem(idItemBuff);
                                Item itemBuffTemplate = ItemService.gI().createNewItem((short) idItemBuff);
                                itemBuffTemplate.itemOptions.add(new Item.ItemOption(idOptionBuff, slOptionBuff));
                                itemBuffTemplate.quantity = slItemBuff;
                                txtBuff += "x" + slItemBuff + " " + itemBuffTemplate.template.name + "\b";
                                InventoryServiceNew.gI().addItemBag(pBuffItem, itemBuffTemplate);
                                InventoryServiceNew.gI().sendItemBags(pBuffItem);
                            }
                            NpcService.gI().createTutorial(player, 24, txtBuff);
                            if (player.id != pBuffItem.id) {
                                NpcService.gI().createTutorial(player, 24, txtBuff);
                            }
                        } else {
                            Service.getInstance().sendThongBao(player, "Player không online");
                        }
                        break;
                    }
                    break;
                case SEND_ITEM_SKH:
                    if (player.isAdmin()) {
                        int idItemBuff = Integer.parseInt(text[1]);
                        int idOptionSKH = Integer.parseInt(text[2]);
                        int idOptionBuff = Integer.parseInt(text[3]);
                        int slOptionBuff = Integer.parseInt(text[4]);
                        int slItemBuff = Integer.parseInt(text[5]);
                        Player pBuffItem = Client.gI().getPlayer(text[0]);
                        if (pBuffItem != null) {
                            String txtBuff = "Buff to player: " + pBuffItem.name + "\b";
                            if (idItemBuff == -1) {
                                pBuffItem.inventory.gold = Math.min(pBuffItem.inventory.gold + (long) slItemBuff, (Inventory.LIMIT_GOLD + pBuffItem.limitgold));
                                txtBuff += slItemBuff + " vàng\b";
                                Service.getInstance().sendMoney(player);
                            } else if (idItemBuff == -2) {
                                pBuffItem.inventory.gem = Math.min(pBuffItem.inventory.gem + slItemBuff, 2000000000);
                                txtBuff += slItemBuff + " ngọc\b";
                                Service.getInstance().sendMoney(player);
                            } else if (idItemBuff == -3) {
                                pBuffItem.inventory.ruby = Math.min(pBuffItem.inventory.ruby + slItemBuff, 2000000000);
                                txtBuff += slItemBuff + " ngọc khóa\b";
                                Service.getInstance().sendMoney(player);
                            } else {
                                Item itemBuffTemplate = ItemService.gI().createNewItem((short) idItemBuff);
                                itemBuffTemplate.itemOptions.add(new Item.ItemOption(idOptionSKH, 0));
                                if (idOptionSKH == 127) {
                                    itemBuffTemplate.itemOptions.add(new Item.ItemOption(139, 0));
                                } else if (idOptionSKH == 128) {
                                    itemBuffTemplate.itemOptions.add(new Item.ItemOption(140, 0));
                                } else if (idOptionSKH == 129) {
                                    itemBuffTemplate.itemOptions.add(new Item.ItemOption(141, 0));
                                } else if (idOptionSKH == 130) {
                                    itemBuffTemplate.itemOptions.add(new Item.ItemOption(142, 0));
                                } else if (idOptionSKH == 131) {
                                    itemBuffTemplate.itemOptions.add(new Item.ItemOption(143, 0));
                                } else if (idOptionSKH == 132) {
                                    itemBuffTemplate.itemOptions.add(new Item.ItemOption(144, 0));
                                } else if (idOptionSKH == 133) {
                                    itemBuffTemplate.itemOptions.add(new Item.ItemOption(136, 0));
                                } else if (idOptionSKH == 134) {
                                    itemBuffTemplate.itemOptions.add(new Item.ItemOption(137, 0));
                                } else if (idOptionSKH == 135) {
                                    itemBuffTemplate.itemOptions.add(new Item.ItemOption(138, 0));
                                }
                                itemBuffTemplate.itemOptions.add(new Item.ItemOption(30, 0));
                                itemBuffTemplate.itemOptions.add(new Item.ItemOption(idOptionBuff, slOptionBuff));
                                itemBuffTemplate.quantity = slItemBuff;
                                txtBuff += "x" + slItemBuff + " " + itemBuffTemplate.template.name + "\b";
                                InventoryServiceNew.gI().addItemBag(pBuffItem, itemBuffTemplate);
                                InventoryServiceNew.gI().sendItemBags(pBuffItem);
                            }
                            NpcService.gI().createTutorial(player, 24, txtBuff);
                            if (player.id != pBuffItem.id) {
                                NpcService.gI().createTutorial(player, 24, txtBuff);
                            }
                        } else {
                            Service.getInstance().sendThongBao(player, "Player không online");
                        }
                        break;
                        
                    }
                    break;
            }
        } catch (Exception e) {
        }
    }
    
    public void createForm(Player pl, int typeInput, String title, SubInput... subInputs) {
        pl.iDMark.setTypeInput(typeInput);
        Message msg;
        try {
            msg = new Message(-125);
            msg.writer().writeUTF(title);
            msg.writer().writeByte(subInputs.length);
            for (SubInput si : subInputs) {
                msg.writer().writeUTF(si.name);
                msg.writer().writeByte(si.typeInput);
            }
            pl.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }
    
    public void createForm(ISession session, int typeInput, String title, SubInput... subInputs) {
        Message msg;
        try {
            msg = new Message(-125);
            msg.writer().writeUTF(title);
            msg.writer().writeByte(subInputs.length);
            for (SubInput si : subInputs) {
                msg.writer().writeUTF(si.name);
                msg.writer().writeByte(si.typeInput);
            }
            session.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }
    
    public void createFormChangePassword(Player pl) {
        createForm(pl, CHANGE_PASSWORD, "Đổi mật khẩu", new SubInput("Mật khẩu cũ", PASSWORD),
                new SubInput("Mật khẩu mới", PASSWORD),
                new SubInput("Nhập lại mật khẩu mới", PASSWORD));
    }
    
    public void createFormGiveItem(Player pl) {
        createForm(pl, GIVE_IT, "Tặng vật phẩm", new SubInput("Tên", ANY), new SubInput("Id Item", ANY), new SubInput("Số lượng", ANY));
    }
    
    public void createFormFindPlayer(Player pl) {
        createForm(pl, FIND_PLAYER, "Tìm kiếm người chơi", new SubInput("Tên người chơi", ANY));
    }
    
    public void createFormNapThe(Player pl, byte loaiThe) {
        pl.iDMark.setLoaiThe(loaiThe);
        createForm(pl, NAP_THE, "Nạp thẻ", new SubInput("Mã thẻ", ANY), new SubInput("Seri", ANY));
    }
    
    public void createFormChangeName(Player pl, Player plChanged) {
        PLAYER_ID_OBJECT.put((int) pl.id, plChanged);
        createForm(pl, CHANGE_NAME, "Đổi tên " + plChanged.name, new SubInput("Tên mới", ANY));
    }
    
    public void createFormChangeNameByItem(Player pl) {
        createForm(pl, CHANGE_NAME_BY_ITEM, "Đổi tên " + pl.name, new SubInput("Tên mới", ANY));
    }
    
    public void TAI(Player pl) {
        createForm(pl, TAI, "Chọn số thỏi vàng đặt Xỉu", new SubInput("Số thỏi vàng", ANY));//????
    }
    
    public void XIU(Player pl) {
        createForm(pl, XIU, "Chọn số thỏi vàng đặt Tài", new SubInput("Số thỏi vàng", ANY));
    }
    
    public void createFormChooseLevelBDKB(Player pl) {
        createForm(pl, CHOOSE_LEVEL_BDKB, "Chọn cấp độ", new SubInput("Cấp độ (1-110)", NUMERIC));
    }
    
    public void createFormChooseLevelGas(Player pl) {
        createForm(pl, CHOOSE_LEVEL_GAS, "Chọn cấp độ", new SubInput("Cấp độ (1-110)", NUMERIC));
    }
    
    public void createFormGiftCode(Player pl) {
        createForm(pl, GIFT_CODE, "Gift code Ngọc rồng HAW", new SubInput("Gift-code", ANY));
    }
    
    public void createFormNhanTV(Player pl) {
        createForm(pl, SEND_THOI_VANG, "Nhập số lượng thỏi vàng muốn đổi", new SubInput("Nhập giá trị là số....", NUMERIC));
    }
    
    public void createFormFindPlayer1(Player pl) {
        createForm(pl, FIND_PLAYER_PHU, "Tìm kiếm người chơi", new SubInput("Tên người chơi", ANY));
    }
//
//    public void createFormFindPlayer2(Player pl) {
//        createForm(pl, NHAP_GIO, "Nhập số giờ", new SubInput("Nhập tên nhân vật cần ban...", ANY), new SubInput("Nhập số...", NUMERIC));
//    }

    public void createFormSendGoldBar(Player pl) {
        createForm(pl, SEND_ITEM_GOLDBAR, "SEND THỎI NẠP",
                new SubInput("Số lượng", ANY));
    }
    
    public void createFormSenditem(Player pl) {
        createForm(pl, SEND_ITEM, "SEND ITEM",
                new SubInput("Tên người chơi", ANY),
                new SubInput("ID item", NUMERIC),
                new SubInput("Số lượng", NUMERIC));
    }
    
    public void createFormSenditem1(Player pl) {
        createForm(pl, SEND_ITEM_OP, "SEND Vật Phẩm Option",
                new SubInput("Tên người chơi", ANY),
                new SubInput("ID Trang Bị", NUMERIC),
                new SubInput("ID Option", NUMERIC),
                new SubInput("Param", NUMERIC),
                new SubInput("Số lượng", NUMERIC));
    }
    
    public void createMenuDoiDaNguSac(Player pl) {
        createForm(pl, DOIDANS,
                "Doi da ngu sac \n 1 Da ngu sac = 1000 VND\n Ban dang co " + GodGK.GetMoney((pl.getSession().userId)) + " VND",
                new SubInput("Số lượng", NUMERIC));
    }
    
    public void createMenuDoiThoiVang(Player pl) {
        createForm(pl, DOITHOIVANG,
                "Doi Thoi vang \n 50 thoi vang = 10000 VND\n Ban dang co " + GodGK.GetMoney((pl.getSession().userId)) + " VND",
                new SubInput("Số lượng", NUMERIC));
    }
    
    public void createMenuDoiHongNgoc(Player pl) {
        createForm(pl, DOIHONGNGOC,
                "Doi Thoi vang \n 50 hong ngoc = 10000 VND\n Ban dang co " + GodGK.GetMoney((pl.getSession().userId)) + " VND",
                new SubInput("Số lượng", NUMERIC));    
    }
    
    public void createFormSenditem2(Player pl) {
        createForm(pl, SEND_ITEM_SKH, "Buff SKH Option V2",
                new SubInput("Tên người chơi", ANY),
                new SubInput("ID Trang Bị", NUMERIC),
                new SubInput("ID Option SKH 127 > 135", NUMERIC),
                new SubInput("ID Option Bonus", NUMERIC),
                new SubInput("Param", NUMERIC),
                new SubInput("Số lượng", NUMERIC));
    }
    
    public static class SubInput {
        
        private String name;
        private byte typeInput;
        
        public SubInput(String name, byte typeInput) {
            this.name = name;
            this.typeInput = typeInput;
        }
    }
    
    public void addItemGiftCodeToPlayer(Player p, final String giftcode) {
        try {
            final GirlkunResultSet red = GirlkunDB.executeQuery("SELECT * FROM `giftcode` WHERE `code` LIKE '" + Util.strSQL(giftcode) + "' LIMIT 1;");
            if (red.first()) {
                String text = "Mã quà tặng" + ": " + giftcode + "\b- " + "Phần quà của bạn là:" + "\b";
                final byte type = red.getByte("type");
                int limit = red.getInt("limit");
                final boolean isDelete = red.getBoolean("Delete");
                final boolean isCheckbag = red.getBoolean("bagCount");
                final JSONArray listUser = (JSONArray) JSONValue.parseWithException(red.getString("listUser"));
                final JSONArray listItem = (JSONArray) JSONValue.parseWithException(red.getString("listItem"));
                if (limit == 0) {
                    NpcService.gI().createTutorial(p, 24, "Số lượng mã quà tặng này đã hết.");
                } else {
                    if (type == 1) {
                        for (int i = 0; i < listUser.size(); ++i) {
                            final int playerId = Integer.parseInt(listUser.get(i).toString());
                            if (playerId == p.id) {
                                NpcService.gI().createTutorial(p, 24, "Mỗi tài khoản chỉ được phép sử dụng mã quà tặng này 1 lần duy nhất.");
                                return;
                            }
                        }
                    }
                    if (isCheckbag && listItem.size() > InventoryServiceNew.gI().getCountEmptyBag(p)) {
                        NpcService.gI().createTutorial(p, 24, "Hành trang cần phải có ít nhất " + listItem.size() + " ô trống để nhận vật phẩm");
                    } else {
                        for (int i = 0; i < listItem.size(); ++i) {
                            final JSONObject item = (JSONObject) listItem.get(i);
                            final int idItem = Integer.parseInt(item.get("id").toString());
                            final int quantity = Integer.parseInt(item.get("quantity").toString());
                            if (idItem == -1) {
                                p.inventory.gold = Math.min(p.inventory.gold + (long) quantity, Inventory.LIMIT_GOLD);
                                text += quantity + " vàng\b";
                            } else if (idItem == -2) {
                                p.inventory.gem = Math.min(p.inventory.gem + quantity, 2000000000);
                                text += quantity + " ngọc\b";
                            } else if (idItem == -3) {
                                p.inventory.ruby = Math.min(p.inventory.ruby + quantity, 2000000000);
                                text += quantity + " ngọc khóa\b";
                            } else {
                                Item itemGiftTemplate = ItemService.gI().createNewItem((short) idItem);
                                
                                itemGiftTemplate.quantity = quantity;
                                itemGiftTemplate.itemOptions.add(new Item.ItemOption(30, 1));
                                text += "x" + quantity + " " + itemGiftTemplate.template.name + "\b";
                                InventoryServiceNew.gI().addItemBag(p, itemGiftTemplate);
                                InventoryServiceNew.gI().sendItemBags(p);
                            }
                            
                            if (i < listItem.size() - 1) {
                                text += "";
                            }
                        }
                        if (limit != -1) {
                            --limit;
                        }
                        listUser.add(p.id);
                        GirlkunDB.executeUpdate("UPDATE `giftcode` SET `limit` = " + limit + ", `listUser` = '" + listUser.toJSONString() + "' WHERE `code` LIKE '" + Util.strSQL(giftcode) + "';");
                        NpcService.gI().createTutorial(p, 24, text);
                    }
                }
            } else {
                NpcService.gI().createTutorial(p, 24, "Mã quà tặng không tồn tại hoặc đã được sử dụng");
            }
        } catch (Exception e) {
            NpcService.gI().createTutorial(p, 24, "Có lỗi sảy ra  hãy báo ngay cho QTV để khắc phục.");
            e.printStackTrace();
        }
    }
}
