package com.timconkling.microtome;

import com.timconkling.microtome.core.LibraryItem;
import com.timconkling.microtome.core.LibraryItemBase;
import com.timconkling.microtome.core.TypeInfo;
import com.timconkling.microtome.prop.ObjectProp;
import com.timconkling.microtome.prop.Prop;
import com.timconkling.microtome.util.Util;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MutablePage extends LibraryItemBase implements Page {
    public MutablePage (String name) {
        super(name);
    }

    @Override public TypeInfo typeInfo () {
        if (_typeInfo == null) {
            _typeInfo = new TypeInfo(getClass(), null);
        }
        return _typeInfo;
    }

    @Override public List<LibraryItem> children () {
        List<LibraryItem> out = new ArrayList<LibraryItem>();
        for (Prop prop : props()) {
            if (prop.value() instanceof LibraryItem) {
                out.add((LibraryItem)prop.value());
            }
        }
        return out;
    }

    @Override public Object childNamed (String name) {
        Prop prop = Util.getProp(this, name);
        return prop == null || !(prop instanceof ObjectProp) ? null : ((ObjectProp)prop).value();
    }

    public List<Prop> props () {
        return Collections.emptyList();
    }

    protected TypeInfo _typeInfo; // lazy init
}
