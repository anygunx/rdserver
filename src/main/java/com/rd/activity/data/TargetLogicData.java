package com.rd.activity.data;

import com.rd.util.XmlUtils;
import org.w3c.dom.Element;

/**
 * 节日达标活动
 *
 * @author U-Demon Created on 2017年3月16日 下午2:34:13
 * @version 1.0.0
 */
public class TargetLogicData extends BaseActivityLogicData {

    private short id;
    private byte type;
//	private Map<Integer, Integer> rank2mubiao;
//	private ImmutableListMultimap<Integer, DropData> mubiaoMap;
//	private short paihangreward;
//
//	public TargetLogicData() {
//	}

    //	private int mubiao1;
//
//	private List<DropData> reward1;
//
//	private int mubiao2;
//
//	private List<DropData> reward2;
//
//	private int mubiao3;
//
//	private List<DropData> reward3;
//
//	private int mubiao4;
//
//	private List<DropData> reward4;
//
//	private int mubiao5;
//
//	private List<DropData> reward5;
//


    @Override
    public String getKey() {
        return round + "";
    }

    @Override
    public void loadData(Element root) {
        id = Short.valueOf(XmlUtils.getAttribute(root, "round"));
        type = Byte.valueOf(XmlUtils.getAttribute(root, "type"));
//		byte size = Byte.valueOf(XmlUtils.getAttribute(root, "mubiaoCount"));
//		Map<Integer, Integer> rank2mubiao = new HashMap<>();
//		Multimap<Integer, DropData> mubiaoMap = ArrayListMultimap.create();
//		for (int i = 1; i <= size; i++){
//			int mubiao = Integer.valueOf(XmlUtils.getAttribute(root, "mubiao" + i));
//			List<DropData> reward = StringUtil.getRewardDropList(XmlUtils.getAttribute(root, "reward" + i));
//			mubiaoMap.putAll(mubiao, reward);
//			rank2mubiao.put(i, mubiao);
//		}
//		paihangreward = Short.valueOf(XmlUtils.getAttribute(root, "paihangreward"));
//
//		this.rank2mubiao = ImmutableMap.copyOf(rank2mubiao);
//		this.mubiaoMap = ImmutableListMultimap.copyOf(mubiaoMap);
    }

    public short getId() {
        return id;
    }

    public byte getType() {
        return type;
    }

//	public int getMuBiao(int rank){
//		if (!rank2mubiao.containsKey(rank)){
//			return -1;
//		}
//		return rank2mubiao.get(rank);
//	}
//
//	public List<DropData> getReward(int mubiao){
//		return mubiaoMap.get(mubiao);
//	}
//
//	public short getPaihangreward() {
//		return paihangreward;
//	}
//
//	public int getMuBiaoCount(){
//		return mubiaoMap.size();
//	}

}
