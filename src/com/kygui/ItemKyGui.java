/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kygui;

import com.girlkun.models.item.Item.ItemOption;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Administrator
 */
public class ItemKyGui {
     public int id;
    public short itemId;
    public int player_sell;
    public byte tab;
    public int goldSell;
    public int gemSell;
    public int quantity;
    public byte isUpTop;
    public List<ItemOption> options = new ArrayList<>();
    public boolean isBuy;
    
    public ItemKyGui(){
    }
    
    public ItemKyGui(int i,short id,int plId,byte t,int gold,int gem,int q,byte isUp,List<ItemOption> op,boolean b){
        this.id = i;
        itemId = id;
        player_sell=plId;
        tab = t;
        goldSell = gold;
        gemSell = gem;
        quantity =q;
        isUpTop = isUp;
        options = op;
        isBuy = b;
    }
    
        @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Item ID: ").append(id).append("\n");
        sb.append("Associated Item ID: ").append(itemId).append("\n");
        sb.append("Player Sell ID: ").append(player_sell).append("\n");
        sb.append("Tab: ").append(tab).append("\n");
        sb.append("Gold Sell Price: ").append(goldSell).append("\n");
        sb.append("Gem Sell Price: ").append(gemSell).append("\n");
        sb.append("Quantity: ").append(quantity).append("\n");
        sb.append("Is Up Top: ").append(isUpTop).append("\n");
        sb.append("Options: ").append(options).append("\n");
        sb.append("Is Buy: ").append(isBuy).append("\n");
        return sb.toString();
    }
}
