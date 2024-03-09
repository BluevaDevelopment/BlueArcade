package net.blueva.arcade.listeners;

import net.blueva.arcade.Main;
import net.blueva.arcade.managers.ArenaManager;
import net.blueva.arcade.managers.CacheManager;
import net.blueva.arcade.managers.PlayerManager;
import net.blueva.arcade.managers.minigames.KnockBackManager;
import net.blueva.arcade.managers.minigames.OneInTheChamberManager;
import net.blueva.arcade.managers.minigames.SnowballFightManager;
import net.blueva.arcade.utils.TitlesUtil;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.Objects;

import static net.blueva.arcade.managers.minigames.TNTTagManager.changePlayerTagged;

public class EntityDamageByEntityListener implements Listener {

    private final Main main;

    public EntityDamageByEntityListener(Main main) {
        this.main = main;
    }

    @EventHandler
    public void EDBEL(EntityDamageByEntityEvent event) {
        if(event.getEntity() instanceof Player player) {
            Entity damager = event.getDamager();

            if(PlayerManager.PlayerStatus.containsKey(player)) {
                if(PlayerManager.PlayerStatus.get(player).equals("Playing")) {
                    int playerArenaID = PlayerManager.PlayerArena.get(player);
                    if (ArenaManager.ArenaActualGame.get(PlayerManager.PlayerArena.get(player)).equalsIgnoreCase("SnowballFight")) {
                        if(damager instanceof Snowball) {
                            SnowballFightManager.finishPlayer(PlayerManager.PlayerArena.get(player), player, true);
                            TitlesUtil.sendTitle(player,
                                    CacheManager.Language.TITLES_YOU_DIED_TITLE
                                            .replace("{place}", String.valueOf(ArenaManager.getPlayerPositionOnPodium(playerArenaID, player))),
                                    CacheManager.Language.TITLES_YOU_DIED_SUBTITLE
                                            .replace("{place}", String.valueOf(ArenaManager.getPlayerPositionOnPodium(playerArenaID, player)))
                                    , 0, 80, 20);
                        } else if (damager instanceof Player) {
                            event.setCancelled(true);
                        }
                    } else if (ArenaManager.ArenaActualGame.get(PlayerManager.PlayerArena.get(player)).equalsIgnoreCase("OneInTheChamber")) {
                        if(damager instanceof Projectile projectile) {
                            if (projectile.getShooter() instanceof Player shooter) {
                                OneInTheChamberManager.deathPlayer(player, main);

                                int playerKills = OneInTheChamberManager.PlayerKills.get(shooter)+1;
                                OneInTheChamberManager.PlayerKills.replace(shooter, playerKills);
                                OneInTheChamberManager.addArrow(shooter);

                                int playerDeaths = OneInTheChamberManager.PlayerDeaths.get(player)+1;
                                OneInTheChamberManager.PlayerDeaths.replace(player, playerDeaths);

                                projectile.remove();
                            }
                        } else if(damager instanceof Player) {
                            if(player.getHealth() - event.getFinalDamage() <= 0) {
                                player.setHealth(20);
                                player.getInventory().clear();
                                OneInTheChamberManager.deathPlayer(player, main);

                                int playerDeaths = OneInTheChamberManager.PlayerDeaths.get(player)+1;
                                OneInTheChamberManager.PlayerDeaths.replace(player, playerDeaths);

                                if(event.getDamager() instanceof Player killer) {

                                    int playerKills = OneInTheChamberManager.PlayerKills.get(killer)+1;
                                    OneInTheChamberManager.PlayerKills.replace(killer, playerKills);
                                    OneInTheChamberManager.addArrow(killer);
                                }
                            }
                        }
                    } else if (ArenaManager.ArenaActualGame.get(PlayerManager.PlayerArena.get(player)).equalsIgnoreCase("KnockBack")) {
                        if(event.getDamager() instanceof Player lastDamager) {
                            KnockBackManager.lastDamager.replace(player, lastDamager);
                        }
                    } else if (ArenaManager.ArenaActualGame.get(PlayerManager.PlayerArena.get(player)).equalsIgnoreCase("TNTTag")) {
                        if(damager instanceof Player) {
                            if(Objects.requireNonNull(((Player) damager).getPlayer()).getInventory().contains(Material.TNT)) {
                                changePlayerTagged((Player) damager, player);
                            }
                        }
                    }
                }
            }
        }
    }
}
