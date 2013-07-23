package com.microtome;

import com.microtome.core.LibraryItem;
import com.microtome.core.LibraryItemBase;
import com.microtome.core.TypeInfo;
import com.microtome.prop.ObjectProp;
import com.microtome.prop.Prop;
import com.microtome.util.Util;
import java.util.ArrayList;
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
        return new ArrayList<Prop>();
    }

    protected TypeInfo _typeInfo; // lazy init
}
