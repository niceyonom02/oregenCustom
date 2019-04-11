package event;

import main.GoodByeLava;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PurchaseUpgradeEvent extends Event {


    public PurchaseUpgradeEvent(Player player, String genName){
        this.player = player;
        this.genName = genName;
    }

    private Player player;
    private String genName;
    private static final HandlerList handlers = new HandlerList();


    public Player getPlayer(){
        return player;
    }

    public String getGenName(){
        return genName;
    }


    @Override
    public String getEventName() {
        return super.getEventName();
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList(){
        return handlers;
    }

}
