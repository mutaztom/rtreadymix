package com.rationalteam.rtreadymix;

import com.rationalteam.rterp.erpcore.CRtDataObject;
import com.rationalteam.rterp.erpcore.CService;
import com.rationalteam.rterp.erpcore.Utility;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RtSerializer<E extends CRtDataObject> {
    E service;

    public RtSerializer(E service) {
        this.service = service;
    }

    public Map<String, Object> getAsRecord() {
        Map<String, Object> map = new HashMap<>();
        List<Field> flds = service.getBrowsableFields();
        for (Field f :
                flds) {
            try {
                map.put(f.getName(), f.get(service));

            } catch (IllegalAccessException e) {
                Utility.ShowError(e);
            }
        }
        return map;
    }
}
