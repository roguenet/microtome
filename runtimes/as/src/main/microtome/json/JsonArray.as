package microtome.json {

import microtome.core.ReadableObject;
import microtome.core.WritableObject;

public class JsonArray extends JsonElement
    implements ReadableObject, WritableObject
{
    public function JsonArray (name :String, obj :Array) {
        super(name, obj);
    }

    public function get children () :Vector.<ReadableObject> {
        var children :Vector.<ReadableObject> = new <ReadableObject>[];
        array.forEach(function (child :Object, ..._) :void {
            children.push(child is Array ?
                new JsonArray(null, child as Array) : new JsonObject(null, child));
        });
        return children;
    }

    public function hasValue (name :String) :Boolean {
        return false;
    }

    public function getBool (name :String) :Boolean {
        throw new Error("getBool not supported");
    }

    public function getInt (name :String) :int {
        throw new Error("getInt not supported");
    }

    public function getNumber (name :String) :Number {
        throw new Error("getNumber not supported");
    }

    public function getString (name :String) :String {
        throw new Error("getString not supported");
    }

    public function addChild (name :String, isList :Boolean = false) :WritableObject {
        return isList ? new JsonArray(null, []) : new JsonObject(null, {});
    }

    public function writeBool (name :String, val :Boolean) :void {
        throw new Error("writeBool not supported");
    }

    public function writeInt (name :String, val :int) :void {
        throw new Error("writeInt not supported");
    }

    public function writeNumber (name :String, val :Number) :void {
        throw new Error("writeNumber not supported");
    }

    public function writeString (name :String, val :String) :void {
        throw new Error("writeString not supported");
    }

    protected function get array () :Array {
        return _value as Array;
    }
}
}
