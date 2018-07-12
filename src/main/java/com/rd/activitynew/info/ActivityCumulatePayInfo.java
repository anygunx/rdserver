package com.rd.activitynew.info;

import com.rd.activitynew.ActivityNewService;
import com.rd.activitynew.EActivityNewType;
import com.rd.activitynew.data.ActivityNewData;
import com.rd.activitynew.data.ActivityNewOpenData;
import com.rd.bean.player.PlayerActivity;
import com.rd.game.GameRole;
import com.rd.net.message.Message;

import java.util.ArrayList;
import java.util.List;

public class ActivityCumulatePayInfo implements IActivityInfo {

    @Override
    public void getMessage(Message message, byte groupId, GameRole role) {

        ActivityNewOpenData openData = ActivityNewService.getActivityOpenMap().get(groupId);
        ActivityNewData activityData = ActivityNewService.getActivityGroupMap().get(groupId).getActivityMap().get(EActivityNewType.ACTIVITY_CUMULATE_PAY.getId());

        String key = ActivityNewService.getKey(groupId);

        int sum = role.getPayManager().getDiamondInPay(openData.getStartTime(), activityData.getEndTime());
        message.setByte(groupId);
        message.setInt((int) ((activityData.getEndTime() - System.currentTimeMillis()) / 1000));
        message.setInt(sum);

        PlayerActivity activity = role.getActivityManager().getActivityData();
        List<Byte> list = new ArrayList<>();
        for (String data : activity.getPayCumulateFixedData()) {
            String[] str = data.split("_");
            if (str[0].equals(key)) {
                list.add(Byte.parseByte(str[1]));
            }
        }
        message.setByte(list.size());
        for (int value : list) {
            message.setByte(value);
        }
    }
}
