$modelsDir = "src\main\resources\assets\sticktoslick\models\item"
if (!(Test-Path $modelsDir)) {
    New-Item -ItemType Directory -Force -Path $modelsDir | Out-Null
}

$weapons = @{
    "weapon_wooden_stick" = "item/stick"
    "weapon_dagger" = "item/iron_sword"
    "weapon_shortsword" = "item/iron_sword"
    "weapon_spear" = "item/trident"
    "weapon_club" = "block/oak_log"
    "weapon_dirk" = "item/iron_sword"
    "weapon_arming_sword" = "item/iron_sword"
    "weapon_trident" = "item/trident"
    "weapon_mace" = "item/iron_axe"
    "weapon_saber" = "item/iron_sword"
    "weapon_longsword" = "item/iron_sword"
    "weapon_lucerne_hammer" = "item/iron_axe"
    "weapon_morning_star" = "item/iron_pickaxe"
    "weapon_katana" = "item/iron_sword"
    "weapon_bastard_sword" = "item/iron_sword"
    "weapon_halberd" = "item/iron_axe"
    "weapon_warhammer" = "item/iron_pickaxe"
    "weapon_nodachi" = "item/iron_sword"
    "weapon_claymore" = "item/iron_sword"
    "weapon_partisan" = "item/trident"
    "weapon_great_maul" = "block/iron_block"
    "weapon_zweihander" = "item/iron_sword"
    "weapon_winged_lance" = "item/trident"
    "weapon_executioners_axe" = "item/iron_axe"
    "weapon_dragon_slayer" = "item/netherite_sword"
    "weapon_gungnir" = "item/trident"
    "weapon_void_crusher" = "block/obsidian"
    "weapon_genesis" = "item/netherite_sword"
    "weapon_longinus" = "item/trident"
    "weapon_atlas" = "block/bedrock"
}

foreach ($weapon in $weapons.GetEnumerator()) {
    $name = $weapon.Key
    $texture = $weapon.Value
    
    $filePath = Join-Path $modelsDir "$name.json"
    
    if ($texture.StartsWith("block/")) {
        $json = @'
{
  "parent": "minecraft:%%TEXTURE%%"
}
'@
        $json = $json.Replace('%%TEXTURE%%', $texture)
    } else {
        $json = @'
{
  "parent": "minecraft:item/handheld",
  "textures": {
    "layer0": "minecraft:%%TEXTURE%%"
  }
}
'@
        $json = $json.Replace('%%TEXTURE%%', $texture)
    }
    
    Set-Content -Path $filePath -Value $json -Force -Encoding UTF8
}
