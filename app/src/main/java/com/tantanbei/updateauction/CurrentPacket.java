package com.tantanbei.updateauction;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

@JsonObject
public class CurrentPacket {

    @JsonField
    public int currentTransactionPrice;

    @JsonField
    public int forecastTransactionPrice;

    @JsonField
    public int cautionPrice;

    @JsonField
    public int peopleNumber;

    @JsonField
    public int limitation;

    @JsonField
    public long serverTime;

    public CurrentPacket() {
    }
}