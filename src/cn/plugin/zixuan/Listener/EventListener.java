package cn.plugin.zixuan.Listener;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.player.PlayerInteractEntityEvent;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.event.player.PlayerJoinEvent;
import cn.nukkit.event.player.PlayerMoveEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.Position;
import cn.nukkit.level.sound.ExperienceOrbSound;
import cn.nukkit.level.sound.ExplodeSound;
import cn.nukkit.math.Vector3;
import cn.plugin.zixuan.NPC.NPC;
import cn.plugin.zixuan.ZQD;
import money.CurrencyType;
import money.MoneyAPI;
import java.util.List;

public class EventListener implements Listener {
    public ZQD plugin=ZQD.getINSTANCE();

    @EventHandler(priority = EventPriority.HIGHEST)
    public void PlayerJoin(PlayerJoinEvent event){
        Player player=event.getPlayer();
        if(plugin.getNpc() != null){
            plugin.getNpc().spawnToAll();
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void EntityDamage(EntityDamageEvent event){
        if(event instanceof EntityDamageByEntityEvent){
            EntityDamageByEntityEvent entityDamageByEntityEvent= (EntityDamageByEntityEvent) event;
            if(entityDamageByEntityEvent.getEntity() instanceof NPC){
                event.setCancelled();
                if(entityDamageByEntityEvent.getDamager() instanceof Player){
                    Player player=(Player) entityDamageByEntityEvent.getDamager();
                    if(player.getGamemode() == 1){
                        player.sendMessage("§d[§2签§b到§5系§6统§d]§c您当前为创造模式无法签到,请切换到生存模式");
                        return;
                    }
                    if(!plugin.iscreateDateFile())
                        plugin.createDateFile();
                    if(!plugin.isQD(player.getName())){
                        plugin.QD(player.getName());
                        List itemList=(List) plugin.getAwardConfig().get("award-item");
                        Item[] items=new Item[itemList.size()];
                        for(int i=0;i<itemList.size();i++){
                            String[] itemData=((String)itemList.get(i)).split(":");
                            items[i]=Item.get(Integer.valueOf(itemData[0]),Integer.valueOf(itemData[1]),Integer.valueOf(itemData[2]));
                        }
                        for(int i=0;i<items.length;i++){
                            player.getInventory().addItem(items[i]);
                        }
                        Integer money=(Integer)plugin.getAwardConfig().get("award-money");
                        MoneyAPI.getInstance().addMoney(player,money, CurrencyType.FIRST);
                        player.sendMessage("§d[§2签§b到§5系§6统§d]§e签到成功,获得"+money+"Coin");
                    }else{
                        player.sendMessage("§d[§2签§b到§5系§6统§d]§c你已经签过到了,请勿再次签到");
                        player.getLevel().addSound(new ExplodeSound(new Vector3(player.getFloorX(),player.getFloorY(),player.getFloorZ())));
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void PlayerMove(PlayerMoveEvent event){
        Player player=event.getPlayer();
        String levelName=player.getLevel().getName();
        if(plugin.existxNPC()){
            Position npcPos=new Position(plugin.getNpc().getFloorX(),plugin.getNpc().getFloorY(),plugin.getNpc().getFloorZ(),plugin.getNpc().getLevel());
            if(npcPos.getLevel().getName().equals(levelName) && player.getFloorX()<=npcPos.getFloorX()+10 && player.getFloorY()<=npcPos.getFloorY()+5 && player.getFloorZ()<=npcPos.getFloorZ()+10){
                plugin.getNpc().look(player);
            }
        }
    }


}
