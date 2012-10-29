//
// microtome

package microtome {

public interface Tome extends LibraryItem
{
    function get pageType () :Class;
    function get size () :int;
    function get pages () :Vector.<Page>;

    function pageNamed (name :String) :Page;
    function requirePageNamed (name :String) :Page;
}
}
