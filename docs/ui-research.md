# HyUI Research — UI Styling & Customization

> Summary of how to build and style UIs in Hytale using the HyUI library (v0.5.5).
> Sources: [HyUI GitBook](https://hyui.gitbook.io/docs), local `Common.ui`, `.ui` element files, `WelcomeWindowEvent.java`.

---

## Table of Contents

1. [Two Approaches Overview](#1-two-approaches-overview)
2. [Approach A: Java (HYUIML + Builder API)](#2-approach-a-java-hyuiml--builder-api)
3. [Approach B: Native .ui Files](#3-approach-b-native-ui-files)
4. [Layout System](#4-layout-system)
5. [Styling Properties Reference](#5-styling-properties-reference)
6. [Available Builders / Elements](#6-available-builders--elements)
7. [Event Handling](#7-event-handling)
8. [Runtime Updates](#8-runtime-updates)
9. [Template Processor](#9-template-processor)
10. [Tab Navigation](#10-tab-navigation)
11. [Item Grids](#11-item-grids)
12. [Common.ui Style Definitions](#12-commonui-style-definitions)
13. [Limitations & Gotchas](#13-limitations--gotchas)

---

## 1. Two Approaches Overview

| Aspect | Java (HYUIML / Builder API) | Native `.ui` Files |
|--------|----------------------------|--------------------|
| Syntax | HTML-like string or fluent Java builders | Hytale's proprietary declarative syntax |
| Event listeners | `.addEventListener()` on element IDs | Not supported directly; use `.editElement()` |
| Dynamic content | Full runtime manipulation via `UIContext` | Limited to `.editElement()` raw commands |
| File location | Java source or `Common/UI/Custom/Pages/*.html` | `Common/UI/Custom/Pages/**/*.ui` |
| Best for | Interactive pages, dynamic UIs | Static layouts, reusing Hytale's built-in styles |

Both approaches use `PageBuilder` as the entry point. You can also combine them — load a `.ui` file as a base, then inject dynamic elements via `.editElement()`.

---

## 2. Approach A: Java (HYUIML + Builder API)

### 2.1 HYUIML (HTML-like Syntax)

Build UIs with HTML strings that compile to Hytale UI elements:

```java
String html = """
    <div class="page-overlay">
        <div class="container" data-hyui-title="My Page"
             style="anchor-width: 800; anchor-height: 500;">
            <div class="container-contents" style="layout-mode: top;">
                <p style="font-size: 20; color: #ffffff;">Hello World</p>
                <button id="my-btn" style="anchor-horizontal: 1; anchor-top: 4;">
                    Click Me
                </button>
            </div>
        </div>
    </div>
    """;

PageBuilder.detachedPage()
    .fromHtml(html)
    .withLifetime(CustomPageLifetime.CanDismiss)
    .addEventListener("my-btn", CustomUIEventBindingType.Activating,
        (data, ctx) -> {
            playerRef.sendMessage(Message.raw("Button clicked!"));
        })
    .open(playerRef, store);
```

**Key structural CSS classes:**
- `page-overlay` — fullscreen dimmed background
- `container` — standard Hytale window frame
- `decorated-container` — framed container with decorations
- `container-contents` — main content area inside a container
- `back-button` — standard back button
- `dynamic-image` — marker for runtime-loaded images
- `tabs` — tab navigation container
- `tab-content` — tab content block

**Special `data-*` attributes:**
- `data-hyui-title="text"` — sets container title
- `data-hyui-slots-per-row="N"` — item grid columns
- `data-hyui-are-items-draggable="true"` — enable drag-and-drop
- `data-hyui-item-id="ItemName"` — item type in slots
- `data-hyui-quantity="N"` — stack size in slots
- `data-hyui-tab-id="id"` — link content to a tab
- `data-hyui-tab-nav="navId"` — link content to a specific tab navigator
- `data-tabs="id:Label:contentId,..."` — define tabs on a `<nav>` element
- `data-selected="tabId"` — initial selected tab

### 2.2 Loading HYUIML from File

Place `.html` files in `Common/UI/Custom/Pages/`:

```java
PageBuilder.detachedPage()
    .loadHtml("Pages/MyPage.html")
    .open(playerRef, store);
```

With template processor:

```java
TemplateProcessor template = new TemplateProcessor()
    .setVariable("title", "Welcome")
    .setVariable("items", itemList);

PageBuilder.pageForPlayer(playerRef)
    .loadHtml("Pages/MyPage.html", template)
    .open(store);
```

### 2.3 Fluent Builder API

Construct UIs purely in Java:

```java
PageBuilder.detachedPage()
    .withLifetime(CustomPageLifetime.CanDismiss)
    .addElement(PageOverlayBuilder.pageOverlay()
        .withId("overlay")
        .addChild(ContainerBuilder.container()
            .withTitleText("My UI")
            .addContentChild(
                LabelBuilder.label().withText("Hello World")
            )
            .addContentChild(
                ButtonBuilder.button().withId("btn").withText("Click")
            )
        )
    )
    .addElement(ButtonBuilder.backButton())
    .open(playerRef, store);
```

**Key builder methods:**
- `.withId("id")` — assign an ID for event binding / lookup
- `.withText("text")` — set label/button text
- `.withLayoutMode("Top")` — set layout mode
- `.addChild(builder)` — add a nested element
- `.addContentChild(builder)` — add to container's content area
- `.addElement(builder)` — add to page root

### 2.4 Loading .ui Files as Base

```java
PageBuilder.pageForPlayer(playerRef)
    .fromFile("Pages/MyPage.ui")
    .editElement(commands -> {
        commands.set("#HyUITitle.Text", "Dynamic Title");
        commands.set("#SomeButton.Disabled", true);
    })
    .open(store);
```

**Important:** Elements from `.ui` files cannot use `.addEventListener()`. Use `.editElement()` to send raw UI commands instead.

### 2.5 Page Lifetime Control

```java
.withLifetime(CustomPageLifetime.CanDismiss)              // ESC closes
.withLifetime(CustomPageLifetime.CanDismissOrCloseThroughInteraction) // ESC or button closes
.withLifetime(CustomPageLifetime.CantClose)               // Cannot be dismissed
```

### 2.6 Detached Pages

Prepare a page before a player reference is available:

```java
PageBuilder builder = PageBuilder.detachedPage()
    .fromHtml(html)
    .withLifetime(CustomPageLifetime.CanDismiss);

// Later, when player is ready:
builder.open(playerRef, store);
```

---

## 3. Approach B: Native .ui Files

Hytale's proprietary UI format. Placed in `Common/UI/Custom/Pages/`.

### 3.1 Syntax Overview

```
// Import shared definitions
$C = "../../Common.ui";

// Element declarations
ElementType #ElementId {
    Property: value;
    Property: (Key: value, Key2: value);

    // Nesting
    ChildType #ChildId {
        Property: value;
    }
}
```

### 3.2 Variable & Import System

```
// Import a file and assign to variable
$C = "../../Common.ui";
$Sounds = "Sounds.ui";

// Reference styles from imported file
Style: $C.@DefaultButtonStyle;
Style: $C.@DefaultSliderStyle;

// Use templates (parameterized elements)
$C.@Container #MyContainer {
    @Text = "My Title";        // Template parameter override
    Anchor: (Width: 600, Height: 400);
}
```

### 3.3 Template Definitions (in Common.ui)

Templates use `@Name = ElementType { ... }` syntax with `@Param` for parameters:

```
@TextButton = TextButton {
    @Anchor = Anchor();        // Default anchor, overridable
    @Sounds = ();              // Default sounds, overridable
    Style: (
        ...@DefaultTextButtonStyle,
        Sounds: (...$Sounds.@ButtonsLight, ...@Sounds)
    );
    Anchor: (...@Anchor, Height: @DefaultButtonHeight);
    Padding: (Horizontal: @DefaultButtonPadding);
    Text: @Text;               // Must be provided by caller
};
```

**Spread operator:** `...@StyleName` merges properties from another style definition.

### 3.4 Element Examples

**Container with title:**
```
$C = "../../Common.ui";
$C.@Container #HyUIContainer {
    Anchor: (Width: 600, Height: 260);
    #Title {
        Group {
            $C.@Title #HyUIContainerTitle { @Text = "Window Title"; }
        }
    }
    #Content { LayoutMode: Top; }
}
```

**Slider:**
```
$C = "../../Common.ui";
Slider #MySlider {
    Value: 50;
    Min: 0;
    Max: 100;
    Step: 1;
    Style: $C.@DefaultSliderStyle;
}
```

**Progress bar:**
```
Group #OuterBar {
    Background: "../../Common/ProgressBar.png";
    ProgressBar #InnerBar {
        BarTexturePath: "../../Common/ProgressBarFill.png";
        EffectTexturePath: "../../Common/ProgressBarEffect.png";
        Value: 0.0;
    }
}
```

**Item grid:**
```
$C = "../../Common.ui";
ItemGrid #MyGrid {
    SlotsPerRow: 4;
    Style: (
        SlotSize: 64,
        SlotIconSize: 64,
        SlotSpacing: 0,
        SlotBackground: "../../Common/BlockSelectorSlotBackground.png"
    );
}
```

**Sprite animation:**
```
Sprite #MySpinner {
    TexturePath: "Common/Spinner.png";
    Frame: (Width: 32, Height: 32, PerRow: 8, Count: 72);
    FramesPerSecond: 30;
    Anchor: (Width: 32, Height: 32);
}
```

**Checkbox with label:**
```
$C = "../../Common.ui";
$C.@CheckBoxWithLabel #MyCheck {
    @Text = "Enable feature";
    @Checked = false;
}
```

---

## 4. Layout System

### 4.1 Layout Modes

Set via `style="layout-mode: Top;"` in HYUIML, `LayoutMode: Top;` in .ui files, or `.withLayoutMode("Top")` in builders.

| Mode | Behavior |
|------|----------|
| `Top` | Stack children top-to-bottom (vertical list) |
| `Bottom` | Stack children bottom-to-top |
| `Left` | Stack children left-to-right (horizontal) |
| `Right` | Stack children right-to-left |
| `Center` | Center children while maintaining stack direction |
| `Middle` / `MiddleCenter` / `CenterMiddle` | Center vertically and horizontally |
| `Full` | Stretch children to fill container |
| `TopScrolling` | Vertical list with scroll |
| `BottomScrolling` | Reverse vertical with scroll |
| `LeftCenterWrap` | Wrap children into rows, centered (grid-like) |

### 4.2 Flex Weight

Distributes available space proportionally among siblings:

```html
<div style="layout-mode: left; padding: 4;">
    <p style="flex-weight: 2;">Title</p>
    <p style="flex-weight: 1;">Level</p>
</div>
```

In .ui files: `FlexWeight: 1;`

Note: This is a simple proportional allocator, not a full flexbox implementation.

### 4.3 Anchor System (Sizing & Positioning)

The anchor system controls element dimensions and positioning.

**HYUIML inline styles:**
```html
style="anchor-width: 600; anchor-height: 400;
       anchor-min-width: 200; anchor-max-width: 800;
       anchor-horizontal: 1; anchor-top: 4; anchor-left: 10;
       anchor-bottom: 0; anchor-right: 0;"
```

**Native .ui syntax:**
```
Anchor: (Width: 600, Height: 400);
Anchor: (Top: 10, Left: 20, Right: 0, Bottom: 0);
Anchor: (Full: 0);                    // Fill parent
Anchor: (Horizontal: 8);              // Horizontal stretch with margin
```

---

## 5. Styling Properties Reference

### 5.1 HYUIML Inline Style Properties

| Property | Example | Description |
|----------|---------|-------------|
| `layout-mode` | `top`, `left`, `center` | Child layout direction |
| `flex-weight` | `1`, `2` | Proportional size in flex layout |
| `anchor-width` | `600` | Fixed width |
| `anchor-height` | `400` | Fixed height |
| `anchor-min-width` | `200` | Minimum width |
| `anchor-max-width` | `800` | Maximum width |
| `anchor-horizontal` | `1` | Horizontal stretch with margin |
| `anchor-top` | `4` | Top offset/margin |
| `anchor-bottom` | `0` | Bottom offset |
| `anchor-left` | `10` | Left offset |
| `anchor-right` | `10` | Right offset |
| `padding` | `8` | Padding around content |
| `font-size` | `20` | Text font size |
| `color` | `#ffffff`, `#96a9be` | Text color |

### 5.2 Native .ui Style Properties

| Property | Example | Description |
|----------|---------|-------------|
| `Anchor` | `(Width: 600, Height: 400)` | Size/position tuple |
| `LayoutMode` | `Top`, `Left`, `Center` | Child arrangement |
| `FlexWeight` | `1` | Proportional sizing |
| `Padding` | `(Full: 8)`, `(Horizontal: 10, Top: 4)` | Padding |
| `Background` | `"path.png"` or `(TexturePath: "x.png", Border: 20)` | Background image/color |
| `Style` | `@DefaultButtonStyle` | Apply a named style |
| `Text` | `"Hello"` | Label/button text |
| `Value` | `0`, `""`, `true` | Input element value |
| `Visible` | `true` / `false` | Element visibility |
| `Disabled` | `true` / `false` | Disable interaction |
| `Min`, `Max`, `Step` | `0`, `100`, `1` | Slider range |
| `SlotsPerRow` | `4` | Item grid columns |

### 5.3 Color Format

```
#ffffff            // Hex RGB
#96a9be            // Hex RGB
#000000(0.45)      // Hex RGB with alpha (0.0 = transparent, 1.0 = opaque)
#00000000          // Fully transparent (8-digit hex)
```

### 5.4 Background Formats

```
// Simple texture
Background: "Common/ContainerFullPatch.png";

// 9-patch with border
Background: (TexturePath: "Common/ContainerPatch.png", Border: 23);

// 9-patch with separate horizontal/vertical borders
Background: (TexturePath: "Common/ContainerHeader.png", HorizontalBorder: 50, VerticalBorder: 0);

// Solid color
Background: (Color: #0a0f17);

// Color with alpha
Background: #000000(0.45);
```

### 5.5 LabelStyle Properties

```
LabelStyle(
    FontSize: 17,
    TextColor: #bfcdd5,
    RenderBold: true,
    RenderUppercase: true,
    HorizontalAlignment: Center,      // Start, Center, End
    VerticalAlignment: Center,         // Top, Center, Bottom
    FontName: "Secondary",             // "Default" or "Secondary"
    LetterSpacing: 2,
    Wrap: true                         // Text wrapping
)
```

### 5.6 ButtonStyle Properties

Buttons have four states: `Default`, `Hovered`, `Pressed`, `Disabled`. Each state has a `Background` (texture) and optionally a `LabelStyle`.

```
ButtonStyle(
    Default: (Background: PatchStyle(TexturePath: "path.png", Border: 12)),
    Hovered: (Background: PatchStyle(TexturePath: "path_hover.png", Border: 12)),
    Pressed: (Background: PatchStyle(TexturePath: "path_press.png", Border: 12)),
    Disabled: (Background: PatchStyle(TexturePath: "path_disabled.png", Border: 12)),
    Sounds: @ButtonSounds
)
```

`TextButtonStyle` adds `LabelStyle` per state:

```
TextButtonStyle(
    Default: (Background: ..., LabelStyle: @DefaultButtonLabelStyle),
    Hovered: (Background: ..., LabelStyle: @DefaultButtonLabelStyle),
    ...
)
```

### 5.7 PatchStyle (9-Patch Textures)

```
PatchStyle(TexturePath: "path.png", Border: 12)
PatchStyle(TexturePath: "path.png", VerticalBorder: 12, HorizontalBorder: 80)
```

---

## 6. Available Builders / Elements

### Container Builders

| Builder | HTML Class/Tag | Description |
|---------|---------------|-------------|
| `PageOverlayBuilder` | `<div class="page-overlay">` | Fullscreen dimmed overlay |
| `ContainerBuilder` | `<div class="container">` | Standard window frame |
| `ContainerBuilder` (decorated) | `<div class="decorated-container">` | Framed container with decorations |
| `GroupBuilder` | `<div>` | Generic container / layout group |
| `TabNavigationBuilder` | `<nav class="tabs">` | Tab navigation bar |
| `TabContentBuilder` | `<div data-hyui-tab-id="...">` | Tab content panel |

### Input Builders

| Builder | HTML Tag | Description |
|---------|---------|-------------|
| `ButtonBuilder` | `<button>` | Clickable button |
| `TextFieldBuilder` | `<input type="text">` | Text input |
| `NumberFieldBuilder` | `<input type="number">` | Number input |
| `SliderBuilder` | `<input type="range">` | Range slider |
| `CheckBoxBuilder` | `<input type="checkbox">` | Toggle checkbox |
| `DropdownBoxBuilder` | `<select>` | Dropdown selection |
| `ColorPickerBuilder` | — | Color picker (builder-only) |

### Display Builders

| Builder | HTML Tag | Description |
|---------|---------|-------------|
| `LabelBuilder` | `<p>`, `<span>`, `<label>` | Text display |
| `ImageBuilder` | `<img>` | Static image |
| `DynamicImageBuilder` | `<img class="dynamic-image">` | Runtime-loaded image |
| `ProgressBarBuilder` | — | Progress bar (builder-only) |
| `TimerLabelBuilder` | — | Timer display (builder-only) |
| `SpriteBuilder` | — | Animated sprite (builder-only) |
| `ItemIconBuilder` | — | Item icon display (builder-only) |
| `ItemSlotBuilder` | — | Single inventory slot (builder-only) |
| `ItemGridBuilder` | — | Grid of item slots (builder-only) |

---

## 7. Event Handling

### 7.1 Event Types

| Type | Triggers On |
|------|------------|
| `CustomUIEventBindingType.Activating` | Button click |
| `CustomUIEventBindingType.ValueChanged` | Input value change (slider, dropdown, text) |
| `CustomUIEventBindingType.FocusLost` | Input loses focus (preferred for text fields) |
| `CustomUIEventBindingType.SlotClicking` | Item grid slot click |
| `CustomUIEventBindingType.Dropped` | Item dropped on slot (drag-and-drop) |

### 7.2 Registering Event Listeners

```java
PageBuilder.detachedPage()
    .fromHtml(html)
    .addEventListener("btn-id", CustomUIEventBindingType.Activating,
        (data, ctx) -> {
            // Handle click
        })
    .addEventListener("slider-id", CustomUIEventBindingType.ValueChanged,
        (data, ctx) -> {
            Optional<Double> value = ctx.getValue("slider-id", Double.class);
        })
    .open(playerRef, store);
```

### 7.3 UIContext Methods

| Method | Description |
|--------|-------------|
| `ctx.getPage()` | Returns `Optional<HyUIPage>` |
| `ctx.getValue("id")` | Get element value as string |
| `ctx.getValue("id", Type.class)` | Get typed element value |
| `ctx.getById("id", BuilderClass.class)` | Get builder for runtime modification |
| `ctx.updatePage(true)` | Full page rebuild |
| `ctx.updatePage(false)` | Rebuild with runtime template re-evaluation |

### 7.4 Closing a Page

```java
// From event handler:
ctx.getPage().ifPresent(page -> page.close());

// From outside:
player.getPageManager().setPage(player.getReference(), store, Page.None);
```

---

## 8. Runtime Updates

### 8.1 Modifying Elements

```java
.addEventListener("btn", CustomUIEventBindingType.Activating, (data, ctx) -> {
    // Update a label
    ctx.getById("label", LabelBuilder.class).ifPresent(lb -> {
        lb.withText("Updated text");
    });

    // Change layout
    ctx.getById("grid", GroupBuilder.class).ifPresent(group -> {
        group.withLayoutMode("LeftCenterWrap");
    });

    // Rebuild page
    ctx.updatePage(true);
});
```

### 8.2 Adding Children at Runtime

```java
ctx.getById("list", GroupBuilder.class).ifPresent(list -> {
    list.addChild(
        LabelBuilder.label().withText("New item")
    );
    ctx.updatePage(true);
});
```

Note: HyUI does not currently support removing elements at runtime. Place dynamic lists in scrollable containers (`TopScrolling`) to handle growing content.

### 8.3 Editing .ui File Elements

```java
PageBuilder.pageForPlayer(playerRef)
    .fromFile("Pages/MyPage.ui")
    .editElement(commands -> {
        commands.set("#ElementId.Text", "New Value");
        commands.set("#ElementId.Disabled", true);
    })
    .open(store);
```

From event handlers (for button text, disabled state, etc.):

```java
button.editElementAfter((commandBuilder, selector) -> {
    commandBuilder.set(selector + ".Text", "New Text");
    commandBuilder.set(selector + ".Disabled", true);
});
```

---

## 9. Template Processor

The template processor enables data-driven UIs with variable substitution, loops, and conditionals.

### 9.1 Variables

```java
TemplateProcessor template = new TemplateProcessor()
    .setVariable("title", "Bounty Board")
    .setVariable("count", bounties.size())
    .setVariable("meta", metaObject);
```

In HTML: `{{$title}}`, `{{$count}}`, `{{$meta.region}}`

**Default values:** `{{$region|Unknown}}`

**Filters:** `{{$title|upper}}`, `{{$reward|number}}`

### 9.2 Loops

```html
{{#each bounties}}
    <div style="layout-mode: left; padding: 4;">
        <p style="flex-weight: 2;">{{$title}}</p>
        <p style="flex-weight: 1;">Lv. {{$level}}</p>
    </div>
{{/each}}
```

### 9.3 Conditionals

```html
{{#if rarity == Rare || level >= 7}}
    <p style="color: #ffcc00;">Elite Bounty</p>
{{else}}
    <p>Standard Bounty</p>
{{/if}}
```

**Operators:** `==`, `!=`, `>`, `<`, `>=`, `<=`, `&&`, `||`, `!`, `contains`

### 9.4 Reusable Components

```java
template.registerComponent("bountyCard", """
    <div style="layout-mode: left; padding: 4;">
        <p style="flex-weight: 2;">{{$title}}</p>
        <p style="flex-weight: 1;">Lv. {{$level}}</p>
    </div>
    """);
```

Use in template: `{{@bountyCard:title={{$title}},level={{$level}}}}`

### 9.5 Runtime Template Updates

Enable re-evaluation of templates from live UI element values:

```java
PageBuilder.pageForPlayer(playerRef)
    .loadHtml("Pages/Board.html", template)
    .enableRuntimeTemplateUpdates(true)
    .addEventListener("region", CustomUIEventBindingType.ValueChanged,
        (value, ctx) -> ctx.updatePage(false))  // false = re-evaluate templates
    .open(store);
```

Variables like `{{$region}}` will resolve from the element with `id="region"` at runtime.

**Constraint:** With `updatePage(false)`, all elements must exist on first load. Elements can be hidden but not added/removed. Use `updatePage(true)` for structural changes.

---

## 10. Tab Navigation

### 10.1 HYUIML Approach

```html
<nav class="tabs"
     data-tabs="tab1:First Tab:content1,tab2:Second Tab:content2"
     data-selected="tab1">
</nav>
<div data-hyui-tab-id="tab1">Content for tab 1</div>
<div data-hyui-tab-id="tab2">Content for tab 2</div>
```

Format: `data-tabs="tabId:Label:contentElementId,..."`

### 10.2 Builder Approach

```java
TabNavigationBuilder.tabNavigation()
    .withId("nav")
    .addTab("tab1", "First Tab", "content1")
    .addTab("tab2", "Second Tab", "content2")
    .withSelectedTab("tab1")

TabContentBuilder.tabContent()
    .withId("content1")
    .withTabId("tab1")
    .addChild(LabelBuilder.label().withText("Tab 1 content"))

TabContentBuilder.tabContent()
    .withId("content2")
    .withTabId("tab2")
    .addChild(LabelBuilder.label().withText("Tab 2 content"))
```

### 10.3 Multiple Tab Navigators

Link content to a specific navigator using `data-hyui-tab-nav` / `.withTabNavigationId()`:

```html
<nav id="main-tabs" class="tabs" data-tabs="a:Tab A:contentA,b:Tab B:contentB"></nav>
<nav id="side-tabs" class="tabs" data-tabs="x:Tab X:contentX,y:Tab Y:contentY"></nav>

<div data-hyui-tab-id="a" data-hyui-tab-nav="main-tabs">...</div>
<div data-hyui-tab-id="x" data-hyui-tab-nav="side-tabs">...</div>
```

### 10.4 Editing Tabs at Runtime

```java
ctx.getById("nav", TabNavigationBuilder.class).ifPresent(nav -> {
    TabNavigationBuilder.Tab tab = nav.getTab("tab1");
    TabNavigationBuilder.Tab updated = new TabNavigationBuilder.Tab(
        tab.id(), "New Label", tab.contentId(), tab.buttonBuilder());
    nav.updateTab("tab1", updated);
    ctx.updatePage(true);
});
```

### 10.5 Tab Styles

Apply predefined styles to selected/unselected states:

```java
HyUIStyle selected = HyUIStyle.withStyleReference("Common.ui", "DefaultTextButtonStyle");
HyUIStyle unselected = HyUIStyle.withStyleReference("Common.ui", "SecondaryTextButtonStyle");

tabNav.withSelectedTabStyle(selected)
      .withUnselectedTabStyle(unselected);
```

Built-in tab styles from Common.ui:
- `@TopTabsStyle` — icon-based tabs above container
- `@HeaderTabsStyle` — compact header tabs with separator

---

## 11. Item Grids

### 11.1 HYUIML

```html
<div data-hyui-slots-per-row="4" data-hyui-are-items-draggable="true">
    <div data-hyui-item-id="Stone" data-hyui-quantity="64"></div>
    <div data-hyui-item-id="Wood" data-hyui-quantity="32"></div>
</div>
```

### 11.2 Builder API

```java
ItemGridBuilder.itemGrid()
    .withId("grid")
    .withSlotsPerRow(4)
    .withDraggable(true)
    .addSlot(new ItemGridSlot("Stone", 64))
    .addSlot(new ItemGridSlot("Wood", 32))
```

### 11.3 Slot Management

```java
grid.addSlot(slot);               // Append slot
grid.updateSlot(slot, index);     // Replace slot at index
grid.removeSlot(index);           // Remove slot
grid.getSlot(index);              // Get slot
grid.getSlots();                  // All slots (unmodifiable)
```

### 11.4 Grid Events

```java
.addEventListener("grid", CustomUIEventBindingType.SlotClicking, (data, ctx) -> {
    SlotClickingEventData slotData = (SlotClickingEventData) data;
    // Handle slot click
})
.addEventListener("grid", CustomUIEventBindingType.Dropped, (data, ctx) -> {
    DroppedEventData dropData = (DroppedEventData) data;
    int source = dropData.getSourceSlotId();
    int target = dropData.getSlotIndex();
    String itemId = dropData.getItemStackId();
    int quantity = dropData.getItemStackQuantity();
})
```

---

## 12. Common.ui Style Definitions

The file at `Common/UI/Custom/Common.ui` (845 lines) defines all reusable styles and templates. Key definitions:

### Button Styles

| Style Name | Description |
|------------|-------------|
| `@DefaultButtonStyle` | Primary button (no label) |
| `@DefaultTextButtonStyle` | Primary button with label |
| `@SecondaryButtonStyle` / `@SecondaryTextButtonStyle` | Secondary button |
| `@TertiaryButtonStyle` / `@TertiaryTextButtonStyle` | Tertiary button |
| `@CancelButtonStyle` / `@CancelTextButtonStyle` | Destructive/cancel button |
| `@SmallDefaultTextButtonStyle` | Small primary button |
| `@SmallSecondaryTextButtonStyle` | Small secondary button |

### Button Templates

| Template | Description |
|----------|-------------|
| `@TextButton` | Full primary text button |
| `@SecondaryTextButton` | Full secondary text button |
| `@TertiaryTextButton` | Full tertiary text button |
| `@CancelTextButton` | Full cancel text button |
| `@SmallSecondaryTextButton` | Small secondary button |
| `@SmallTertiaryTextButton` | Small tertiary button |
| `@Button` | Primary square button |
| `@SecondaryButton` | Secondary square button |
| `@TertiaryButton` | Tertiary square button |
| `@CancelButton` | Cancel square button |
| `@CloseButton` | Container close "X" button |
| `@BackButton` | Back button group |

### Container Templates

| Template | Description |
|----------|-------------|
| `@Container` | Standard container (header + content) |
| `@DecoratedContainer` | Fancy container with top/bottom decorations |
| `@PageOverlay` | Fullscreen dimmed overlay |

### Input Styles

| Style | Description |
|-------|-------------|
| `@DefaultInputFieldStyle` | Default text input style |
| `@DefaultInputFieldPlaceholderStyle` | Placeholder text style (color: #6e7da1) |
| `@DefaultDropdownBoxStyle` | Dropdown with panel, arrow, entry styles |
| `@DefaultCheckBoxStyle` | Checkbox with checked/unchecked states + sounds |
| `@DefaultSliderStyle` | Slider with handle and track |
| `@DefaultColorPickerStyle` | Color picker with opacity selector |

### Other Styles

| Style | Description |
|-------|-------------|
| `@DefaultScrollbarStyle` | Standard scrollbar |
| `@TranslucentScrollbarStyle` | Only visible on hover |
| `@DefaultLabelStyle` | Default label (16px, #96a9be) |
| `@TitleStyle` | Title label (15px, bold, uppercase, Secondary font) |
| `@SubtitleStyle` | Subtitle label (15px, uppercase, #96a9be) |
| `@PopupTitleStyle` | Popup title (38px, bold, uppercase) |
| `@DefaultTextTooltipStyle` | Tooltip (max 400px, 16px, wrapping) |
| `@TopTabsStyle` | Icon-based tab navigation |
| `@HeaderTabsStyle` | Compact header tab navigation |

### Size Constants

| Constant | Value |
|----------|-------|
| `@PrimaryButtonHeight` | 44 |
| `@SmallButtonHeight` | 32 |
| `@BigButtonHeight` | 48 |
| `@ButtonPadding` | 24 |
| `@DefaultButtonMinWidth` | 172 |
| `@ButtonBorder` | 12 |
| `@TitleHeight` | 38 |
| `@DropdownBoxHeight` | 32 |

---

## 13. Limitations & Gotchas

1. **No `.addEventListener()` on .ui file elements** — Elements loaded from `.ui` files cannot have event listeners. Use `.editElement()` for raw command-based modifications.

2. **No element removal at runtime** — HyUI currently only supports adding children, not removing them. Use scrollable containers for growing lists.

3. **Slider style loss on update** — Known Hytale issue: slider elements may lose custom styles when `updatePage()` is called.

4. **`updatePage(false)` constraints** — All elements must exist on first load. Cannot add/remove elements, only hide them. Use `updatePage(true)` for structural changes.

5. **`updatePage(true)` = full rebuild** — This is not a surgical patch; it rebuilds the entire page client-side.

6. **Text field event noise** — Use `FocusLost` instead of `ValueChanged` for text inputs to avoid firing on every keystroke.

7. **Codec key capitalization** — When using persistent components with `BuilderCodec`, key names must start with a capital letter.

8. **Runtime template updates are experimental** — Requires thorough testing before production use.

9. **Interactive pages must call `sendUpdate()`** — For `InteractiveCustomUIPage`, always call `sendUpdate()` after handling events, or the client shows "Loading..." forever.

10. **Asset pack requirement** — Set `"IncludesAssetPack": true` in `manifest.json` if your plugin includes UI files, textures, or other assets.
