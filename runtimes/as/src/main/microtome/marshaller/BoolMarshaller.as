//
// microtome

package microtome.marshaller {

import microtome.core.Annotation;
import microtome.core.DataReader;
import microtome.core.MicrotomeMgr;
import microtome.core.TypeInfo;
import microtome.core.WritableObject;
import microtome.prop.Prop;

public class BoolMarshaller extends PrimitiveMarshaller
{
    public function BoolMarshaller () {
        super(Boolean);
    }

    override public function validateProp (prop :Prop) :void {
        // no validation necessary
    }

    override public function readValue (mgr :MicrotomeMgr, reader :DataReader, name :String, type :TypeInfo) :* {
        return reader.requireBool(name);
    }

    override public function readDefault (mgr :MicrotomeMgr, type :TypeInfo, anno :Annotation) :* {
        return anno.boolValue(false);
    }

    override public function writeValue (mgr :MicrotomeMgr, writer :WritableObject, val :*, name :String, type :TypeInfo) :void {
        writer.writeBool(name, val);
    }
}
}
