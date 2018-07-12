package com.rd.bean.player;

import com.rd.net.message.Message;

import java.util.ArrayList;
import java.util.List;

/**
 * 角色外形数据
 *
 * @author Created by U-Demon on 2016年12月14日 下午2:59:30
 * @version 1.0.0
 */
public class AppearPlayer {

    //法宝外形
    private short magicStage = 0;

    //宠物外形
    private byte petShow = 0;

    private List<AppearCharacter> chas = new ArrayList<>();

//	private Map<Byte, Byte> huanhua = Collections.EMPTY_MAP;

    public void init(Player player, int occ) {
        this.magicStage = player.getMagicStage();
        this.petShow = 0;
//		if (player.getCharacterList() != null) {
//			//所有角色
//			if (occ == -1) {
//				for (Character cha : player.getCharacterList()) {
//					AppearCharacter ac = new AppearCharacter(cha);
//					chas.add(ac);
//				}
//			}
//			//某个角色
//			else {				
//				for (Character cha : player.getCharacterList()) {
//					if (cha.getOccupation() == occ)
//						chas.add(new AppearCharacter(cha));
//				}
//			}
//		}
//		this.huanhua = ImmutableMap.copyOf(player.getHuanhuaAppearance());
    }

    public void getMessage(Message msg) {
        msg.setByte(magicStage);
        msg.setByte(petShow);
        msg.setByte(this.chas.size());
        for (AppearCharacter ac : this.chas) {
            ac.getMsg(msg);
        }
    }

    public void getFirstMsg(Message msg) {
        AppearCharacter ac = this.chas.get(0);
        ac.getMsg(msg);
    }

    public AppearCharacter getAppearCha(int idx) {
        return this.chas.get(idx);
    }

//	public Map<Byte, Byte> getHuanhua() {
//		return huanhua;
//	}
//
//	public void setHuanhua(Map<Byte, Byte> huanhua) {
//		this.huanhua = huanhua;
//	}
//
//	public String getHuanhunJsonData(){
//		return JSON.toJSONString(huanhua);
//	}
//
//	public void setHuanhuaJsonData(String jsonData){
//		if (!StringUtil.isEmpty(jsonData)) {
//			this.huanhua = JSON.parseObject(jsonData, new TypeReference<Map<Byte, Byte>>(){});
//		}
//	}

}
