//
// microtome - Copyright 2012 Three Rings Design

#import "RefPage.h"
#import "PrimitivePage.h"

static NSString* const XML_STRING =
    @"<root><refTest type='RefPage'>"
    @"  <nested>tomeTest.test1</nested>"
    @"</refTest></root>";

static MTObjectPropSpec* _nestedSpec = nil;

@implementation RefPage {
@protected
    MTObjectProp* _nested;
}

+ (NSString*)XML { return XML_STRING; }

- (MTMutablePageRef*)nestedRef { return _nested.value; }
- (PrimitivePage*)nested { return self.nestedRef.page; }

- (id)init {
    if ((self = [super init])) {
        _nested = [[MTObjectProp alloc] initWithPropSpec:_nestedSpec];
    }
    return self;
}

- (NSArray*)props {
    return MT_PROPS(_nested);
}

+ (void)initialize {
    if (self == [RefPage class]) {
        _nestedSpec = [[MTObjectPropSpec alloc] initWithName:@"nested" nullable:NO valueType:MTBuildType(@[ [MTMutablePageRef class], [PrimitivePage class] ])];
    }
}

@end
