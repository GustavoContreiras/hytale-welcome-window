# Introduction

The purpose of this library is to provide a welcome window with information about the server.

Use it to explain which mods you added to your server and how to use them.

![alt text](https://media.forgecdn.net/attachments/1493/984/welcome-window-2-png.png)

## Usage

1.  Download the `.jar` file
2.  Put it into your `mods` folder
3.  Run the server
4.  Login
5.  Edit `./mods/WelcomeWindow/config.json`

PS: Use empty strings (`""`) in the paragraphs to have more space between the texts.

## Dependencies

[HyUI](https://www.curseforge.com/hytale/mods/hyui) >= 0.5.2

## Complete example

`config.json`

```
{
  "backButtonText": "Back",
  "nextButtonText": "Next",
  "doneButtonText": "Finish",
  "menuWidth": 260,
  "containerWidth": 900,
  "containerHeight": 420,
  "fontSize": 16,
  "alwaysShow": false,
  "pages": [
    {
      "title": "Welcome to Hytale",
      "buttonTitle": "Commands",
      "paragraphs": [
        "Here are some available commands:",
        "",
        "/help - displays all available commands",
        "/welcome - displays this window",
        "/modlist - displays all installed mods",
        "/lvl gui - panel to assign level points",
        "/simpleclaims - claim an area of the map just for yourself",
        "/hidearmor - hide your equipment on your skin",
        "/sit - allows changing the character's sitting positions",
        "",
        "Press ENTER or / to open the chat and run these commands"
      ]
    },
    {
      "title": "Level",
      "buttonTitle": "Level",
      "paragraphs": [
        "By killing creatures, you gain experience.",
        "",
        "When you level up, type /lvl gui to assign your level points.",
        "",
        "You can improve your health, mana, stamina, damage, defense,",
        "mining, woodcutting, oxygen, and ammo capacity."
      ]
    },
    {
      "title": "Map",
      "buttonTitle": "Map",
      "paragraphs": [
        "Places you visit will be permanently saved on your map.",
        "",
        "Press M to view it."
      ]
    },
    {
      "title": "Inventory",
      "buttonTitle": "Inventory",
      "paragraphs": [
        "Press TAB to:",
        "- Equip armor pieces and off-hand items",
        "- Craft tools and crafting tables",
        "- Organize your items",
        "",
        "Press 'shift' + 'left mouse button' to transfer items quickly.",
        "Press 'shift' + 'right mouse button' to select half of the",
        "stack.",
        "Press the 'right mouse button' to select only one unit.",
        "",
        "Items are automatically organized and stacked when placed",
        "in your inventory.",
        "",
        "You can hide your armor on your skin using the /hidearmor command"
      ]
    },
    {
      "title": "Counter-attack",
      "buttonTitle": "Parry",
      "paragraphs": [
        "When you successfully block at the exact moment you are",
        "receiving an attack, the enemy will be left open and vulnerable",
        "to a counter-attack."
      ]
    },
    {
      "title": "Durability",
      "buttonTitle": "Durability",
      "paragraphs": [
        "Weapon, tool, and equipment durability has been disabled."
      ]
    },
    {
      "title": "Claiming",
      "buttonTitle": "Claiming",
      "paragraphs": [
        "Type /simpleclaims to:",
        "- View areas on the map already claimed by other players",
        "- View areas on the map protected from destruction",
        "- Claim an area on the map for yourself",
        "",
        "You can also type '/simpleclaims claim' to claim the area",
        "you are currently in or '/simpleclaims unclaim' to release it."
      ]
    },
    {
      "title": "Death",
      "buttonTitle": "Death",
      "paragraphs": [
        "Upon death, some of your equipment, tools, and weapons may be",
        "lost.",
        "",
        "A coffin containing your items will remain at the location",
        "where you died until someone collects them."
      ]
    },
    {
      "title": "Fishing",
      "buttonTitle": "Fishing",
      "paragraphs": [
        "Craft a fishing rod through your inventory.",
        "",
        "Place the bait in the water by right-clicking and wait",
        "a few moments.",
        "",
        "When pulling the bait out of the water, you may have",
        "caught a fish."
      ]
    },
    {
      "title": "Mount",
      "buttonTitle": "Mount",
      "paragraphs": [
        "Approach a horse and press F to mount it",
        "",
        "Type '/mount dismount' to walk on foot again."
      ]
    }
  ]
}
```