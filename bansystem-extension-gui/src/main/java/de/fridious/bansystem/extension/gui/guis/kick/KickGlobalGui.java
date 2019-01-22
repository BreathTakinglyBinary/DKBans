package de.fridious.bansystem.extension.gui.guis.kick;

/*
 * (C) Copyright 2019 The DKBans Project (Davide Wietlisbach)
 *
 * @author Philipp Elvin Friedhoff
 * @since 05.01.19 00:17
 * @Website https://github.com/DevKrieger/DKBans
 *
 * The DKBans Project is under the Apache License, version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

import ch.dkrieger.bansystem.lib.BanSystem;
import ch.dkrieger.bansystem.lib.config.mode.ReasonMode;
import ch.dkrieger.bansystem.lib.player.NetworkPlayer;
import de.fridious.bansystem.extension.gui.DKBansGuiExtension;
import de.fridious.bansystem.extension.gui.api.inventory.gui.PrivateGui;
import de.fridious.bansystem.extension.gui.api.inventory.item.ItemBuilder;
import de.fridious.bansystem.extension.gui.api.inventory.item.ItemStorage;
import de.fridious.bansystem.extension.gui.guis.GuiManager;
import de.fridious.bansystem.extension.gui.guis.Guis;
import de.fridious.bansystem.extension.gui.utils.GuiExtensionUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;

public class KickGlobalGui extends PrivateGui<Player> {

    public KickGlobalGui(Player owner) {
        super(54, owner);
        setPageEntries(GuiExtensionUtils.getInteractOnlinePlayers(owner));
    }

    @Override
    public void setPageItem(int slot, Player player) {
        NetworkPlayer networkPlayer = BanSystem.getInstance().getPlayerManager().getPlayer(player.getUniqueId());
        setItem(slot, new ItemBuilder(ItemStorage.get("globalkick_skull", networkPlayer)).setGameProfile(networkPlayer.getName()));
    }

    @Override
    public void beforeUpdatePage() {
        setPageEntries(GuiExtensionUtils.getInteractOnlinePlayers(getOwner()));
    }

    @Override
    protected void onOpen(InventoryOpenEvent event) {

    }

    @Override
    protected void onClick(InventoryClickEvent event) {
        final Player player = (Player) event.getWhoClicked();
        Player target = getEntryBySlot().get(event.getSlot());
        if(target != null) {
            NetworkPlayer targetNetworkPlayer = BanSystem.getInstance().getPlayerManager().getPlayer(target.getUniqueId());
            if(player.hasPermission("dkbans.kick") && (!targetNetworkPlayer.hasBypass()
                    || player.hasPermission("dkbans.bypass.ignore"))) {
                GuiManager guiManager = DKBansGuiExtension.getInstance().getGuiManager();
                ReasonMode kickMode = BanSystem.getInstance().getConfig().kickMode;
                if(kickMode == ReasonMode.TEMPLATE && guiManager.isGuiEnabled(KickTemplateGui.class)) {
                    Bukkit.getScheduler().runTask(DKBansGuiExtension.getInstance(), ()->
                            DKBansGuiExtension.getInstance().getGuiManager().getCachedGuis(player)
                                    .create(Guis.KICK_TEMPLATE, new KickTemplateGui(player, target.getUniqueId())).open());
                } else if(kickMode == ReasonMode.SELF && guiManager.isGuiEnabled(KickSelfGui.class)) {
                    Bukkit.getScheduler().runTask(DKBansGuiExtension.getInstance(), ()->
                            DKBansGuiExtension.getInstance().getGuiManager().getCachedGuis(player)
                                    .create(Guis.KICK_SELF, new KickSelfGui(player, target.getUniqueId())).open());
                }
            }
        }
    }

    @Override
    protected void onClose(InventoryCloseEvent event) {

    }
}