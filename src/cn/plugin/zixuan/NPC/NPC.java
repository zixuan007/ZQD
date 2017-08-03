package cn.plugin.zixuan.NPC;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityHuman;
import cn.nukkit.entity.data.EntityMetadata;
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
import cn.nukkit.network.protocol.*;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.Utils;
import cn.plugin.zixuan.ZQD;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.util.*;

public class NPC extends EntityHuman {


    public NPC(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);

    }


    public static void init(){
        if(ZQD.getINSTANCE().getConfig().get("nameTag") != null){
            Config config=ZQD.getINSTANCE().getConfig();
            ArrayList posList= (ArrayList) config.get("pos");
            for(Map.Entry<Integer,Level> entry: ZQD.getINSTANCE().getServer().getLevels().entrySet()){
                entry.getValue().loadChunk(((Integer) posList.get(0))>>4,((Integer)posList.get(2)>>4));
                for(Entity entity:entry.getValue().getEntities()){
                    if(entity instanceof NPC){
                        entity.despawnFromAll();
                        entity.setNameTag((String)config.get("nameTag"));
                        entity.setNameTagAlwaysVisible();
                        entity.yaw=(double)config.get("yaw");
                        entity.pitch=(double)config.get("pith");
                        ZQD.getINSTANCE().setNpc((NPC)entity);
                        return;
                    }
                }

            }
            Position pos=new Position((Integer)posList.get(0),(Integer)posList.get(1),(Integer)posList.get(2),ZQD.getINSTANCE().getServer().getLevelByName((String)posList.get(3)));
            FullChunk chunk=pos.getLevel().getChunk(pos.getFloorX()>>4,pos.getFloorZ()>>4);
            CompoundTag nbt=new CompoundTag();
            nbt.putList(new ListTag<DoubleTag>("Pos").add(new DoubleTag("", pos.getFloorX())).add(new DoubleTag("", pos.getFloorY())).add(new DoubleTag("", pos.getFloorZ())))
                    .putList(new ListTag<DoubleTag>("Motion").add(new DoubleTag("", 0)).add(new DoubleTag("", 0)).add(new DoubleTag("", 0)))
                    .putList(new ListTag<FloatTag>("Rotation").add(new FloatTag("", pos instanceof Location ? (float) ((Location) pos).yaw : 0))
                            .add(new FloatTag("", pos instanceof Location ? (float) ((Location) pos).pitch : 0)));
            Skin skin=new Skin(ZQD.getINSTANCE().getSkinFile());
            NPC npc=new NPC(chunk,nbt);
            npc.setNameTag("§a>>§c签§e到§a使§b者§a<<");
            npc.setNameTagAlwaysVisible();
            ZQD.getINSTANCE().setNpc(npc);
        }
    }

    public void initEntity(){
        setSkin(new Skin(ZQD.getINSTANCE().getSkinFile()));
        super.initEntity();
    }



    public void save(){
        Config config=ZQD.getINSTANCE().getConfig();
        config.set("nameTag",getNameTag());
        config.set("pos", new ArrayList<Object>(){{
            add(getFloorX());
            add(getFloorY());
            add(getFloorZ());
            add(getLevel().getName());
        }
        });
        config.set("yaw",getYaw());
        config.set("pith",getPitch());
        config.save();
    }


    public void look(Player player) {
        double x=getFloorX()-player.x, y=getFloorY()-player.y, z=getFloorZ()-player.z;
        double yaw=Math.asin(x/Math.sqrt(x*x+z*z))/3.14*180, pitch=Math.round(Math.asin(y/Math.sqrt(x*x+z*z+y*y))/3.14*180);
        if(z>0) {
            yaw=-yaw+180;
        }
        MovePlayerPacket pk=new MovePlayerPacket();
        pk.eid=getId();
        pk.x=Float.parseFloat(String.valueOf(this.x));
        pk.y=Float.parseFloat(String.valueOf(this.y+1.62));
        pk.z=Float.parseFloat(String.valueOf(this.z));
        pk.headYaw=pk.yaw=Float.parseFloat(String.valueOf(yaw));
        pk.pitch=Float.parseFloat(String.valueOf(pitch));
        pk.mode=0;
        player.dataPacket(pk);
    }

    public void removeNPC(){
        despawnFromAll();
        Config config=ZQD.getINSTANCE().getConfig();
        config.remove("nameTag");
        config.remove("pos");
        config.remove("yaw");
        config.remove("pith");
        config.save();
    }


}
