package com.tencentcs.iotvideodemo.accountmgrtc.httpservice;

import java.util.HashMap;

public class TencentcsModel extends AbstractModel {
    @Override
    protected void toMap(HashMap<String, String> map, String prefix) {
        setParamSimple(map, prefix + "Test", "hello");
    }
}
