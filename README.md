# Introduction

The purpose of this library is to provide a welcome window with information about the server.

Use it to explain which mods you added to your server and how to use them.

![alt text](https://media.forgecdn.net/attachments/1493/984/welcome-window-2-png.png)

## Usage 

### Server admin

1.  Download the `.jar` file
2.  Put it into your `mods` folder
3.  Run the server
4.  Login
5.  Edit `./mods/WelcomeWindow/config.json`

PS: Use empty strings (`""`) in the paragraphs to have more space between the texts.

### Player

- Run `/welcome` if you want to see the window again

## Dependencies

[HyUI](https://www.curseforge.com/hytale/mods/hyui) >= 0.5.2

## Complete example

`config.json`

```
{
  "BackButtonText": "Back",
  "NextButtonText": "Next",
  "DoneButtonText": "Finish",
  "PageCounterText": "Page",
  "MenuWidth": 260,
  "ContainerWidth": 900,
  "ContainerHeight": 420,
  "FontSize": 16,
  "AlwaysShow": false,
  "Debug": false,
  "ShowPageCounter": true,
  "AllowExitOnAnyPage": true,
  "Pages": [
    {
      "Title": "Welcome to Hytale",
      "ButtonTitle": "Commands",
      "Paragraphs": [
        "Here are some available commands:",
        "",
        "/help - shows all available commands",
        "/welcome - shows this window",
        "/modlist - shows all installed mods",
        "/lvl gui - panel to assign level points",
        "/simpleclaims - claim a map area just for you",
        "/hidearmor - hide your equipment on your skin",
        "/sit - allows changing the character's pose",
        "",
        "Press ENTER or / to open the chat and run these commands"
      ]
    },
    {
      "Title": "Level",
      "ButtonTitle": "Level",
      "Paragraphs": [
        "By killing creatures, you gain experience.",
        "",
        "When leveling up, type /lvl gui to assign your level points.",
        "",
        "You can improve your health, mana, stamina, damage, defense,",
        "mining, woodcutting, oxygen, and ammo capacity."
      ]
    },
    {
      "Title": "Map",
      "ButtonTitle": "Map",
      "Paragraphs": [
        "Places you visit will be saved on your map.",
        "",
        "Press M to view it."
      ]
    },
    {
      "Title": "Inventory",
      "ButtonTitle": "Inventory",
      "Paragraphs": [
        "Press TAB to:",
        "- Equip armor pieces and off-hand items",
        "- Craft tools and crafting tables",
        "- Organize your items",
        "",
        "Press 'shift' + 'left mouse button' to quickly transfer.",
        "Press 'shift' + 'right mouse button' to select half the",
        "amount.",
        "Press the 'right mouse button' to select only one unit.",
        "",
        "Items are automatically organized and stacked when placed",
        "in your inventory.",
        "",
        "You can hide your armor on your skin using the /hidearmor command"
      ]
    },
    {
      "Title": "Counter-attack",
      "ButtonTitle": "Parry",
      "Paragraphs": [
        "By blocking at the exact moment you are about to receive an",
        "attack, the enemy will be left open and vulnerable to a counter-attack."
      ]
    },
    {
      "Title": "Durability",
      "ButtonTitle": "Durability",
      "Paragraphs": [
        "Weapon, tool, and equipment durability has been disabled."
      ]
    },
    {
      "Title": "Claiming",
      "ButtonTitle": "Claiming",
      "Paragraphs": [
        "Type /simpleclaims to:",
        "- View areas on the map already occupied by other players",
        "- View areas on the map protected from destruction",
        "- Claim an area for yourself on the map",
        "",
        "You can also type '/simpleclaims claim' to claim the area",
        "you are standing in or '/simpleclaims unclaim' to release it."
      ]
    },
    {
      "Title": "Death",
      "ButtonTitle": "Death",
      "Paragraphs": [
        "Upon death, some of your equipment, tools, and weapons may be",
        "lost.",
        "",
        "A coffin containing your items will remain at the place where",
        "you died until someone retrieves them."
      ]
    },
    {
      "Title": "Fishing",
      "ButtonTitle": "Fishing",
      "Paragraphs": [
        "Craft a fishing rod through your inventory.",
        "",
        "Place the bait in the water by right-clicking and wait",
        "a few moments.",
        "",
        "When pulling the bait out of the water, you may have caught a fish."
      ]
    },
    {
      "Title": "Mount",
      "ButtonTitle": "Mount",
      "Paragraphs": [
        "Approach a horse and press F to mount it",
        "",
        "Type '/mount dismount' to walk on foot again."
      ]
    }
  ]
}
```