package com.applozic.mobicomkit.listners;

import com.applozic.mobicomkit.annotations.ApplozicInternal;
import com.applozic.mobicomkit.feed.MqttMessageResponse;

@ApplozicInternal
public interface AlMqttListener {
    void onMqttMessageReceived(MqttMessageResponse mqttMessage);
}
