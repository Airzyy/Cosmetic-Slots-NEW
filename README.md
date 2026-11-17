# Cosmetic Slots

A standalone cosmetic armor mod for **Minecraft 1.21.1 / NeoForge 21.1.265+** (Java 21). No Curios or Accessories dependency.

Adds four cosmetic armor slots in a panel to the left of the inventory screen. Whatever you put there is what everyone sees you wearing; the real armor in your normal slots still does all the protecting. Cosmetic items give no armor, toughness, enchantments or other stats.

## Behaviour
- Slots accept only armor for the matching slot (helmet, chestplate, leggings, boots).
- Shift-click on a cosmetic slot sends the item to your inventory.
- Cosmetic armor is shown on you and on other players (synced to everyone tracking you).
- Items are saved with the player. On death they drop like worn armor, unless `keepInventory` is on.
- The panel is hidden while the recipe book is open (they'd overlap) and in the creative inventory screen.
- Worlds from 1.x are migrated automatically on first login.

## Building
```
./gradlew build
```
The jar ends up in `build/libs/`. `./gradlew runClient` starts a dev client. GitHub Actions builds on every push.

## How it works
| Piece | What it does |
|---|---|
| `CosmeticAttachments` / `CosmeticArmor` | NeoForge data attachment holding the four stacks. Saved + synced by NeoForge itself, no custom packets. |
| `CosmeticSlot` | The menu slot, reads/writes the attachment. |
| `InventoryMenuMixin` | Appends the slots (after the vanilla ones, so no vanilla index changes) and fixes shift-click. |
| `HumanoidArmorLayerMixin` | Client only: renders the cosmetic stack instead of the real one when present. |
| `CosmeticSlotsClient` | Client only: draws the slot panel. |
