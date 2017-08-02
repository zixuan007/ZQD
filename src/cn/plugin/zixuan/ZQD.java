package cn.plugin.zixuan;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import cn.plugin.zixuan.Command.zqd;
import cn.plugin.zixuan.Listener.EventListener;
import cn.plugin.zixuan.NPC.NPC;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;



public class ZQD extends PluginBase {
    private static ZQD INSTANCE;
    private Config config;
    private File skinFile;
    private File Data;
    private NPC npc;
    private Config awardConfig;

    @Override
    public void onEnable() {

        if(getServer().getPluginManager().getPlugin("Money") == null){
            getServer().getLogger().info("§d[§2签§b到§5系§6统§d]§7:§c您还没有装Money插件,请前往§1https://pl.zxda.net/plugins/573.html§c,下载并安装Money插件");
            getServer().shutdown();
        }
        getServer().getLogger().info("§a>>§c签到§bNPC§e插§5件§9加§7载§a<<");
        if(INSTANCE == null){
            INSTANCE=this;
        }
        saveResource("1.png");
        skinFile=new File(getDataFolder()+"//1.png");
        saveResource("Config.yml");
        awardConfig=new Config(getDataFolder()+"//Config.yml",Config.YAML);
        config=new Config(getDataFolder()+"//NPC.yml",Config.YAML);
        Data=new File(getDataFolder()+"//QD");
        if(!Data.exists())
            Data.mkdir();
        getServer().getCommandMap().register("zqd",new zqd());
        getServer().getPluginManager().registerEvents(new EventListener(),this);
        Entity.registerEntity("NPC",NPC.class);
        if(config.get("nameTag") != null){
            NPC.init();
        }
    }

    public static ZQD getINSTANCE(){
        return INSTANCE;
    }

    public Config getConfig(){
        return config;
    }

    public File getSkinFile(){
        return skinFile;
    }

    public NPC getNpc() {
        return npc;
    }

    public void setNpc(NPC npc){
        this.npc=npc;
    }

    public boolean existxNPC(){
        if(config.getAll().get("nameTag") != null){
            return true;
        }
        return false;
    }

    public void createDateFile(){
        if(iscreateDateFile()){
            File dateFile=new File(getNowDateFile().getAbsolutePath());
            dateFile.mkdir();
        }
    }

    public boolean iscreateDateFile(){
        Calendar c = Calendar.getInstance();
        int year=c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int date = c.get(Calendar.DATE);
        File dateFile=new File(Data+"//"+year+"."+month+"."+date);
        if(dateFile.exists())
            return true;
        return false;
    }

    public boolean isQD(String playerName){
        for(File file:getNowDateFile().listFiles()){
            String[] name=file.getName().split("\\.");
            if(name[0] != null){
                if(name[0].equals(playerName.toLowerCase())){
                    return true;
                }
            }
        }
        return false;
    }

    public void QD(String playerName){
        File QDFile=new File(getNowDateFile().getAbsolutePath()+"//"+playerName.toLowerCase()+".yml");
        try {
            QDFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public File getNowDateFile(){
        Calendar c = Calendar.getInstance();
        int year=c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int date = c.get(Calendar.DATE);
        File dateFile=new File(Data+"//"+year+"."+month+"."+date);
        if(!iscreateDateFile())
            dateFile.mkdir();
        return  dateFile;
    }

    public Config getAwardConfig() {
        return awardConfig;
    }
}
