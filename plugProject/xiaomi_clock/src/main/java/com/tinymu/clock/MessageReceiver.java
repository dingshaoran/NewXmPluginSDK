
package com.tinymu.clock;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;

import com.tinymu.clock.main.MainActivity;
import com.xiaomi.plugin.core.XmPluginPackage;
import com.xiaomi.smarthome.device.api.BaseWidgetView;
import com.xiaomi.smarthome.device.api.DeviceStat;
import com.xiaomi.smarthome.device.api.IXmPluginMessageReceiver;
import com.xiaomi.smarthome.device.api.MessageCallback;
import com.xiaomi.smarthome.device.api.XmPluginHostApi;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * 所有插件入口函数，必须实现
 */
public class MessageReceiver implements IXmPluginMessageReceiver {

    @Override
    public boolean handleMessage(Context context, XmPluginPackage xmPluginPackage, int type,
            Intent intent,
            DeviceStat deviceStat) {
//        DeviceStub stub = DeviceStub.getInstance(deviceStat);
//        if(stub == null)return false;
        switch (type) {
            case LAUNCHER: {// 启动入口
                XmPluginHostApi.instance().startActivity(context, xmPluginPackage, intent,
                        deviceStat.did, MainActivity.class);
                return true;
            }
            case PUSH_MESSAGE: {
                // 订阅消息push通知
                if (intent == null)
                    return false;
                String msgType = intent.getStringExtra("type");
                if (TextUtils.isEmpty(msgType))
                    return false;
                if ("DevicePush".equals(msgType)) {// 订阅的消息
                    //String event = intent.getStringExtra("event");
                    //[{"key":"event.work_status","time":1514280249,"value":["use"]}]
                    String data = intent.getStringExtra("data");
                    try {
                        JSONArray array = new JSONArray(data);
                        for(int i = 0; i < array.length(); i ++){
                            JSONObject object = array.getJSONObject(i);
                            String event =object.getString("key");
//                            if(DeviceProperty.Events.work_status.equals(event)){
//                                stub.synchronizeProperties(DeviceStub.NewSet(DeviceProperty.Names.work_state), null);
//                            }
                        }
                    }catch (Exception ex){

                    }

                } else if ("ScenePush".equals(msgType)) {// 场景消息
                    String event = intent.getStringExtra("event");
                    String extra = intent.getStringExtra("extra");
                    boolean isNotified = intent.getBooleanExtra("isNotified", false);
//                    Log.d(DeviceStub.MODEL, "ScenePush :" + event + "  " + extra);
                    //TODO 处理场景通知
                }
            }
            default:
                break;
        }
        return false;
    }

    //智能家庭app主动插件，获取插件数据,建议定义的type>=100
    @Override
    public boolean handleMessage(Context context, XmPluginPackage xmPluginPackage, int type,
            Intent intent, DeviceStat deviceStat, MessageCallback callback) {
        //TODO 主app调用插件获取数据
        return false;
    }
    
    //提供卡片模式，在设备列表显示
    @Override
    public BaseWidgetView createWidgetView(Context arg0, LayoutInflater arg1, XmPluginPackage arg2,
            int arg3, Intent arg4, DeviceStat arg5) {
        return null;
    }

}
