package com.girlkun.models.item;

import com.girlkun.models.Template;
import com.girlkun.models.Template.ItemTemplate;
import com.girlkun.services.ItemService;
import com.girlkun.utils.Util;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Item {

    public ItemTemplate template;

    public String info;

    public String content;

    public int quantity;

    public int quantityGD = 0;

    public List<ItemOption> itemOptions;

    public long createTime;

    public boolean isNotNullItem() {
        return this.template != null;
    }

    public boolean isAura() {
        return this.template.id == 2049;
    }

    public boolean isNullItem() {
        return this.template == null;
    }

    public Item() {
        this.itemOptions = new ArrayList<>();
        this.createTime = System.currentTimeMillis();
    }

    public Item(short itemId) {
        this.template = ItemService.gI().getTemplate(itemId);
        this.itemOptions = new ArrayList<>();
        this.createTime = System.currentTimeMillis();
    }

    public String getInfo() {
        String strInfo = "";
        for (ItemOption itemOption : itemOptions) {
//         

            strInfo += itemOption.getOptionString();
            
           

           
        }
//       
        return strInfo;
    }

    public String getContent() {
        return "Yêu cầu sức mạnh " + this.template.strRequire + " trở lên";
    }

    public void dispose() {
        this.template = null;
        this.info = null;
        this.content = null;
        if (this.itemOptions != null) {
            for (ItemOption io : this.itemOptions) {
                io.dispose();
            }
            this.itemOptions.clear();
        }
        this.itemOptions = null;
    }

    public static class ItemOption {

        @Override
        public String toString() {
            return "ItemOption{" + "param=" + param + ", optionTemplate=" + optionTemplate.toString() + '}';
        }

        private static Map<String, String> OPTION_STRING = new HashMap<String, String>();

        public int param;

        public Template.ItemOptionTemplate optionTemplate;

        public ItemOption() {
        }

        public ItemOption(ItemOption io) {
            this.param = io.param;
            this.optionTemplate = io.optionTemplate;
        }

        public ItemOption(int tempId, int param) {
            this.optionTemplate = ItemService.gI().getItemOptionTemplate(tempId);
            this.param = param;
        }

        public ItemOption(Template.ItemOptionTemplate temp, int param) {
            this.optionTemplate = temp;
            this.param = param;
        }

        public String getOptionString() {

            return Util.replace(this.optionTemplate.name, "#", String.valueOf(this.param));
        }
        public void dispose() {
            this.optionTemplate = null;
        }
    }

    public boolean isSKH() {
        for (ItemOption itemOption : itemOptions) {
            if (itemOption.optionTemplate.id >= 127 && itemOption.optionTemplate.id <= 135 || itemOption.optionTemplate.id == 210) {
                return true;
            }
        }
        return false;
    }
    
     public boolean isSKHNangCap() {
        for (ItemOption itemOption : itemOptions) {
            if (itemOption.optionTemplate.id >= 140 && itemOption.optionTemplate.id <= 144 || itemOption.optionTemplate.id == 136 || itemOption.optionTemplate.id == 138) {
              
                return true;
            }
        }
     
        return false;
    }
     
      public boolean isTacdung() {
        for (ItemOption itemOption : itemOptions) {
            if (itemOption.optionTemplate.id == 212) {
                return true;
            }
        }
        return false;
    }
     

    public int isSKHH() {
        for (ItemOption itemOption : itemOptions) {
            if (itemOption.optionTemplate.id >= 127 && itemOption.optionTemplate.id <= 135) {
                switch (itemOption.optionTemplate.id) {
                    case 133:
                        return 136;
                    case 134:
                        return 137;
                    case 135:
                        return 138;

                }
            }
        }
        return -1;
    }

    public boolean isDTS() {
        if (this.template.id >= 1048 && this.template.id <= 1062) {
            return true;
        }
        return false;
    }

    public boolean isPorata2() {
        if (this.template.id == 921) {
            return true;
        }
        return false;
    }

    public boolean isPorata3() {
        if (this.template.id == 2064) {
            return true;
        }
        return false;
    }

    public boolean isManhHon() {
        if (this.template.id == 934) {
            return true;
        }
        return false;
    }

    public boolean isPorata() {
        if (this.template.id == 454) {
            return true;
        }
        return false;
    }

    public boolean isManhVo() {
        if (this.template.id == 933) {
            return true;
        }
        return false;
    }

    public boolean isDTL() {
        if (this.template.id >= 555 && this.template.id <= 567) {
            return true;
        }
        return false;
    }

    public boolean isDHD() {
        if (this.template.id >= 650 && this.template.id <= 662) {
            return true;
        }
        return false;
    }
    public boolean isDoTS() {
        if (this.template.id >= 1048 && this.template.id <= 1060) {
            return true;
        }
        return false;
    }

    public boolean isManhTS() {
        if (this.template.id >= 1066 && this.template.id <= 1070) {
            return true;
        }
        return false;
    }

    public String typeName() {
        switch (this.template.type) {
            case 0:
                return "Áo";
            case 1:
                return "Quần";
            case 2:
                return "Găng";
            case 3:
                return "Giày";
            case 4:
                return "Rada";
            default:
                return "";
        }
    }

    public byte typeIdManh() {
        if (!isManhTS()) {
            return -1;
        }
        switch (this.template.id) {
            case 1066:
                return 0;
            case 1067:
                return 1;
            case 1070:
                return 2;
            case 1068:
                return 3;
            case 1069:
                return 4;
            default:
                return -1;
        }
    }

    public String typeNameManh() {
        switch (this.template.id) {
            case 1066:
                return "Áo";
            case 1067:
                return "Quần";
            case 1070:
                return "Găng";
            case 1068:
                return "Giày";
            case 1069:
                return "Nhẫn";
            default:
                return "";
        }
    }
}
