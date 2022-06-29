package com.ssomar.executableblocks.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;

import com.ssomar.executableblocks.ExecutableBlocks;
import com.ssomar.executableblocks.checkversion.CheckVersion;
import com.ssomar.executableblocks.configs.GeneralConfig;
import com.ssomar.score.utils.StringConverter;

public class VersionEvt implements Listener {

	@EventHandler
	public void onPlayerJoinEvent(PlayerJoinEvent e) {

		Player p = e.getPlayer();

		if(p.isOp() && GeneralConfig.getInstance().isCheckVersionMsg()) {
			try {
				String cVer = CheckVersion.getVersion();
				if(cVer == null) return;
				String aVer = ExecutableBlocks.getPluginSt().getDescription().getVersion();

				if(!cVer.equals(aVer)) {
					p.sendMessage(StringConverter.coloredString("&8&l&oExecutableBlocks &8(&cOnly for op&8)  &7Your version of ExecutableBlocks is out of date. &8(&7Actual: &6"+aVer+"&8) &8(&7Lastest: &a"+cVer+ "&8) &7Update your plugin for the lastest features / bug fixes / better performance !"));
				}
			}catch(Exception err) {
				err.printStackTrace();
			}
		}
	}


}
