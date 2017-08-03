package cn.plugin.zixuan.Command;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityHuman;
import cn.nukkit.entity.data.Skin;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.level.Location;
import cn.nukkit.level.Position;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.DoubleTag;
import cn.nukkit.nbt.tag.FloatTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.plugin.zixuan.NPC.NPC;
import cn.plugin.zixuan.ZQD;


public class zqd extends Command {
    public ZQD plugin=ZQD.getINSTANCE();

    public zqd() {
        super("zqd","签到插件总指令");
        getCommandParameters().clear();
        getCommandParameters().put("1",new CommandParameter[]{
                new CommandParameter("create",true,new String[]{"create"})
        });
        getCommandParameters().put("2",new CommandParameter[]{
                new CommandParameter("del",true,new String[]{"del"})
        });
        getCommandParameters().put("3",new CommandParameter[]{
                new CommandParameter("help", true, new String[]{"help"})
        });
        setAliases(new String[]{
                "zqd create  §a>>§b创建签到NPC",
                "zqd del §a>>§b移除签到NPC"
        });
    }

    @Override
    public boolean execute(CommandSender sender, String s, String[] args) {
        Player player=(Player)sender;
        if(getName().equals("zqd")){
            if(args.length<1){
                sender.sendMessage("§b/zqd help §a>>§c获取帮助");
                return false;
            }
            switch (args[0]){
                case "create":
                    if(plugin.existxNPC()){
                        sender.sendMessage("§d[§2签§b到§5系§6统§d]§c你已经创建了签到NPC了");
                        return false;
                    }
                    for(Entity entity:player.getLevel().getEntities()){
                        if(entity instanceof NPC){
                            entity.despawnFromAll();
                            entity.close();
                        }
                    }
                    Position position=new Position(player.getFloorX(),player.getFloorY(),player.getFloorZ(),player.getLevel());
                    Float yaw=(float) player.getYaw();
                    Float pitch=(float)player.getPitch();
                    FullChunk fullChunk=player.getLevel().getChunk((int)player.getX()>>4,(int)player.getZ()>>4);
                    CompoundTag nbt=new CompoundTag();
                    Position pos=new Position(player.getFloorX(),player.getFloorY(),player.getFloorZ(),player.getLevel());
                    nbt.putList(new ListTag<DoubleTag>("Pos").add(new DoubleTag("", pos.getFloorX())).add(new DoubleTag("", pos.getFloorY())).add(new DoubleTag("", pos.getFloorZ())))
                            .putList(new ListTag<DoubleTag>("Motion").add(new DoubleTag("", 0)).add(new DoubleTag("", 0)).add(new DoubleTag("", 0)))
                            .putList(new ListTag<FloatTag>("Rotation").add(new FloatTag("", pos instanceof Location ? (float) ((Location) pos).yaw : 0))
                                    .add(new FloatTag("", pos instanceof Location ? (float) ((Location) pos).pitch : 0)));
                    Skin skin=new Skin(plugin.getSkinFile());
                    NPC npc=new NPC(fullChunk,nbt);
                    npc.setNameTag("§a>>§c签§e到§a使§b者§a<<");
                    npc.setNameTagAlwaysVisible();
                    npc.spawnToAll();
                    npc.save();
                    plugin.setNpc(npc);
                    sender.sendMessage("§d[§2签§b到§5系§6统§d]§e创建成功");
                    return true;
                case "del":
                    if(!plugin.existxNPC()){
                        sender.sendMessage("§d[§2签§b到§5系§6统§d]§c你还没创建过签到NPC,请先创建");
                        return false;
                    }
                    plugin.getNpc().removeNPC();
                    plugin.setNpc(null);
                    sender.sendMessage("§d[§2签§b到§5系§6统§d]§e移除成功");
                    return true;
                case "help":
                    sender.sendMessage("§b/zqd create §a>>§b创建签到NPC");
                    sender.sendMessage("§b/zqd del §a>>§b移除签到NPC");
                    return true;
            }
        }
        return false;
    }
}
