# Sophisticated Fabric Library

Sophisticated Fabric Library is the shared foundation used by the Fabric versions of the Sophisticated mods.

You normally will not notice this mod on its own. It does not add new blocks, items, mobs, dimensions, or other standalone content to Minecraft. Instead, it helps mods such as **Sophisticated Backpacks (Unofficial Fabric Port)**, **Sophisticated Storage (Unofficial Fabric Port)**, and other Sophisticated projects provide their features on Fabric.

This is my version of implementing features from [NeoForge](https://neoforged.net/) to make Unofficial Fabric Ports of the Sophisticated Mods possible. It is also heavily inspired by [Porting-Lib's](https://github.com/Fabricators-of-Create/Porting-Lib/tree/1.20.1) implementation of NeoForge's features.

## What does it do?

The library supplies shared systems that the Unofficial Fabric Ports of the Sophisticated mods rely on, including:

*   **Inventory and item handling:** allows Sophisticated storage features to move, insert, extract, and manage items reliably.
*   **Fluid and energy handling:** gives supported Sophisticated features a common way to work with fluids and power.
*   **Client-server communication:** keeps inventories, storage actions, and other mod features synchronized in multiplayer.
*   **Game events and loot:** lets the Sophisticated mods react to events in the world and add or modify loot where needed.
*   **Rendering and model support:** helps Sophisticated items, screens, particles, and other visual features appear correctly in-game.

## How does it affect the game?

The library itself does not change your gameplay or add content. Its role is to make the other mods work correctly on Fabric.

For example, when you use Sophisticated Backpacks (Unofficial Fabric Port) or Sophisticated Storage (Unofficial Fabric Port), this library helps those mods handle their inventories, transfer items, display their interfaces, and provide a way to synchronize between players and servers. Without the library, those mods will not work.

This means the benefits you experience come from the Unofficial Fabric Ports of the Sophisticated mods you install, while this library provides the behind-the-scenes support that makes those benefits possible on Fabric.