package com.tantanbei.updateauction;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

@JsonObject
public class StartAuction {
    @JsonField
    long overTime;//multi second

    @JsonField
    int cautionPrice;

    @JsonField
    int limitation;
}
