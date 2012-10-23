//
// microtome - Copyright 2012 Three Rings Design

#import "microtomeTests.h"

#import "microtome.h"
#import "MTXmlLibrary.h"
#import "GDataXMLElement+MTExtensions.h"

#import "PrimitivePage.h"
#import "NestedPage.h"
#import "RefPage.h"

static NSString* const TOME_XML =
    @"<root><tomeTest type='Tome:PrimitivePage'>"
    @"  <test1 type='PrimitivePage' foo='true' bar='2' baz='3.1415'/>"
    @"  <test2 type='PrimitivePage' foo='false' bar='666' baz='0.1'/>"
    @"</tomeTest></root>";

static NSString* const TEMPLATE_XML =
    @"<root>"
    @"  <test0 type='PrimitivePage' foo='true' bar='2' baz='3.1415'/>"
    @"  <test2 type='PrimitivePage' template='test1' baz='666'/>"
    @"  <test1 type='PrimitivePage' template='test0'/>"
    @"</root>";

static const float EPSILON = 0.0001f;

static GDataXMLDocument* GetXML (NSString* xmlString) {
    NSError* err = nil;
    return [[GDataXMLDocument alloc] initWithXMLString:xmlString options:0 error:&err];
}

@implementation microtomeTests

- (void)setUp {
    [super setUp];
    _library = [[MTXmlLibrary alloc] init];
    [_library registerPageClasses:@[
        [PrimitivePage class],
        [NestedPage class],
        [RefPage class],
    ]];
}

- (void)tearDown {
    [super tearDown];
}

- (void)testPrimitives {
    [_library loadXmlDocs:@[GetXML(PrimitivePage.XML)]];
    PrimitivePage* page = _library[@"primitiveTest"];
    STAssertNotNil(page, @"");
    STAssertEquals(page.foo, YES, @"");
    STAssertEquals(page.bar, 2, @"");
    STAssertEqualsWithAccuracy(page.baz, 3.1415f, EPSILON, @"");
    [_library removeAllItems];
}

- (void)testTome {
    [_library loadXmlDocs:@[GetXML(TOME_XML)]];
    id<MTTome> tome = _library[@"tomeTest"];
    STAssertEquals(tome.pageCount, 2, @"");
    [_library removeAllItems];
}

- (void)testNested {
    [_library loadXmlDocs:@[GetXML(NestedPage.XML)]];
    NestedPage* page = _library[@"nestedTest"];
    STAssertEquals(page.nested.foo, YES, @"");
    STAssertEquals(page.nested.bar, 2, @"");
    STAssertEqualsWithAccuracy(page.nested.baz, 3.1415f, EPSILON, @"");
    [_library removeAllItems];
}

- (void)testRefs {
    [_library loadXmlDocs:@[GetXML(TOME_XML), GetXML(RefPage.XML)]];
    RefPage* refPage = _library[@"refTest"];
    STAssertNotNil(refPage.nested, @"");
    STAssertEquals(refPage.nested.foo, YES, @"");
    STAssertEquals(refPage.nested.bar, 2, @"");
    STAssertEqualsWithAccuracy(refPage.nested.baz, 3.1415f, EPSILON, @"");
    [_library removeAllItems];
}

- (void)testTemplates {
    [_library loadXmlDocs:@[ GetXML(TEMPLATE_XML) ]];
    
    PrimitivePage* page = _library[@"test1"];
    STAssertEquals(page.foo, YES, @"");
    STAssertEquals(page.bar, 2, @"");
    STAssertEqualsWithAccuracy(page.baz, 3.1415f, EPSILON, @"");

    page = _library[@"test2"];
    STAssertEqualsWithAccuracy(page.baz, 666.0f, EPSILON, @"");
    
    [_library removeAllItems];
}

@end
