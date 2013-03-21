import org.powerbot.core.event.listeners.PaintListener;
import org.powerbot.core.script.ActiveScript;
import org.powerbot.game.api.Manifest;
import org.powerbot.game.api.methods.Calculations;
import org.powerbot.game.api.methods.Walking;
import org.powerbot.game.api.methods.interactive.Players;
import org.powerbot.game.api.methods.node.Menu;
import org.powerbot.game.api.methods.node.SceneEntities;
import org.powerbot.game.api.methods.tab.Inventory;
import org.powerbot.game.api.methods.widget.Bank;
import org.powerbot.game.api.methods.widget.Camera;
import org.powerbot.game.api.wrappers.Tile;
import org.powerbot.game.api.wrappers.map.Path;
import org.powerbot.game.api.wrappers.node.SceneObject;
import org.powerbot.game.api.methods.Widgets;
import org.powerbot.game.api.methods.input.Keyboard;
import org.powerbot.game.api.wrappers.map.TilePath;
import org.powerbot.game.api.methods.tab.Equipment;
import org.powerbot.game.api.methods.Tabs;
import org.powerbot.game.api.methods.tab.Skills;
import org.powerbot.game.api.util.net.GeItem;


import java.awt.*;



@Manifest(authors = {"ggImANoob"}, name = "Repairs Armor", description = "nothing!", version = 1.0)
public class armorRepair extends ActiveScript implements PaintListener{



    //need rune ids for house tele
    //need armor ids
    //need armor stand ids
    //need to figure out how to use a glory
    //need portal ids


    //start
    //pick what armor to use have that set use armor id equal to a variable
    //check to make sure that there is enough money in inv
    //see if inv is full with that id
    //make sure that the settings are to go into house in construction mode
    //if there are tabs in inv then use them if there are runes then use those

    //Teleport ids-----------------------------------
    int houseTeleID =8013;
    int lawRuneID = 563;
    int airRuneID =556;
    int earthRuneID=557;
    int airStaffID = -1;
    String teleType;


    //house ids------------------------
    int armorStandID = 13715;
    int varrockHousePortalID = 13615;
    int gloryID = 13523;


    //camera--------------------------------
    int[] cameraAngleStart={22274,11120,359,60};


    //Armor ids------------------------
    int DHBody=4892;
    int DHLegs=-1;
    int useArmor;
    int armorFixed;
    String armorName;



    //Proffit Vars-------------------------------
    int proffit=0;
    int brokenGE;
    int fixedGE;
    int fixCost;

    //Banking--------------------
    Tile bankTile= new Tile (3093,3496,0);
    int bankNPCS = 2759;
    int[] bankCouner = {42378,42377,42217};


    //onstart fix-------------------------------------
    boolean onstartDone=false;




    public void onStart() {
        System.out.println("in onStart first line");
        settingTeleTab();
        System.out.println("after settingTeleTab in onStart");
        sleep(300, 700);
        useArmor=checkInvArmor();
        sleep(300, 700);
        teleType=checkInvTele();
        sleep(300, 700);
        buildingModeSet();
        sleep(300, 700);
        onstartDone=true;
        //works like a charm to here
    }

    @Override
    public int loop() {
        if (onstartDone==true){
            System.out.println("in loop first line");
            //start working on fixing here
            withdrawlBank(useArmor,armorFixed);
            sleep(300, 700);
            teleport(teleType);
            sleep(5000,6000);
            rotateCameraToArmorStand();
            sleep(300, 700);
            useArmorOnStand(useArmor);
            repairArmorDio(useArmor);   //finish this so it can finish going throught the diolog
            sleep(300,1000);
            rotateCameraToGlory(gloryID);
            sleep(300, 1000);
            useGlory(gloryID);
        }


        return 50;
    }

    //buying form ge--------------------------------------
    //click on ge person
    // click on an empty slot buy.  Use widgets to do this
    //type in the name then click on the right choice using widgets and read text
    //see how much money they have
    // get the price from the ge then see how many you can get
    //if the person wants to in the GUI they can get more money from the bank
    //then put the money back after
    //you can set the sell price from the person or you can calculate it using how much you purchesed for and sell for.
    //have the person tell you how much they got them for.
    //or look in ge history.


    //selling ge------------------------------------------

    //walking to ge--------------------------------------

    //Settings-------------------------------------------
    void buildingModeSet(){
        Tabs.OPTIONS.open(true);
        Widgets.get(261).getChild(25).interact("Open House Options");
        sleep(300, 700);
        Widgets.get(398).getChild(28).interact("When teleporting, arrive in house");
        sleep(300, 700);
        Widgets.get(398).getChild(15).interact("Building mode on");
        sleep(300, 700);
        if(Widgets.get(137).getChild(58).getChild(0).getText()=="Building mode is now on."){
        }else{
            Widgets.get(398).getChild(21).interact("Close");
        }
    }

    //Banking--------------------------------------------------------------------
    void withdrawlBank(int useArmor,int armorFixed){
        SceneObject bankCounterObject = SceneEntities.getNearest(bankCouner);
        String item="rune";
        System.out.println("in withdrawBank");

        walkNext(bankTile,10);
        Camera.turnTo(bankCounterObject);
        while(Bank.isOpen()==false){
            bankCounterObject.click(true);
            sleep(1000,1600);
        }
        if(Inventory.contains(armorFixed)){
            Bank.deposit(armorFixed,28);
        }
        System.out.println(useArmor);
        if(GeItem.lookup(useArmor).getName()==null)
            Bank.search(armorName);
        else{
            item=GeItem.lookup(useArmor).getName();
            Bank.search(item);

        }
        if (Bank.getItemCount(useArmor) >0){
            Bank.withdraw(useArmor,28);
            sleep(500,600);
        }
        Bank.close();
    }



    //Proffit--------------------------------------------------------------------
    int repairCostLevel(int normalRepairCost){
        int smithingSkill=1;
        smithingSkill= Skills.getLevel(Skills.SMITHING);
        return (1-(smithingSkill)/200)*normalRepairCost;
    }

    int gePriceBroken(int useArmor){
        return GeItem.lookup(useArmor).getPrice();
    }

    int gePriceFixed (int fixedArmor){
        return GeItem.lookup(fixedArmor).getPrice();
    }

    int totalProffit(){
        return 0;
    }


    //Teleporting--------------------------------------------------------------

    void settingTeleTab(){
        System.out.println("in settingTeleTab"); // A message shown when the script starts
        Tabs.ABILITY_BOOK.open();
        sleep(1000, 1200);
        //magic tab widget
        while(Widgets.get(275).getChild(46).visible()!=true){
            Widgets.get(275).getChild(41).click(true);//interact("Magic");
            sleep(300, 800);
        }
        //tele spell tab widget
        while ( Widgets.get(275).getChild(18).getChild(36).getTextColor() <1000){
            Widgets.get(275).getChild(38).click(true);//interact("Teleport-spells");
            sleep(300, 800);
        }

        System.out.println("end of settingTeleTab");
    }


    void teleport(String teleType){
        Tile location;
        Tabs.INVENTORY.open();
        if(teleType=="tabTele"){
            Inventory.selectItem(houseTeleID);
        }else if(teleType=="magicTele"){
            location=Players.getLocal().getLocation();
            while (Players.getLocal().getLocation()==location){
                Tabs.ABILITY_BOOK.open(true);
                //House teleport
                Widgets.get(275).getChild(18).getChild(36).click(true);
                while(!Players.getLocal().isIdle()){
                    sleep(500);
                }
            }


        }

    }



    //check Inv----------------------------------------------------------------
    int checkInvArmor (){
        if(Inventory.contains(DHBody)){
            armorName="Dhorax Body";
            return DHBody;
        }
        if(Inventory.contains(DHLegs)) {
            armorName="Dhorax Legs";
            return DHLegs;
        }
        //finish this off

        return -1;
    }

    String checkInvTele (){
        Tabs.INVENTORY.open();
        if(Inventory.contains(houseTeleID)){
            return "tabTele";
        }
        if(Equipment.containsOneOf(airStaffID)){
            if(Inventory.contains(lawRuneID)){
                if(Inventory.contains(earthRuneID)){
                    return "magicTele";
                }
            }
        } if(Inventory.contains(airRuneID)){
            if(Inventory.contains(lawRuneID)){
                if(Inventory.contains(earthRuneID)){
                    return "magicTele";
                }
            }
        } else{
            return "error";
        }
        return "error";
    }



    //Glory amy---------------------------------------------------------------
    void rotateCameraToGlory(int gloryID){
        boolean turned=false;
        //need a while loop
        while (turned!=true){
            if (SceneEntities.getNearest(gloryID)!=null){
                SceneObject gloryObject = SceneEntities.getNearest(gloryID);
                Camera.turnTo(gloryObject);
                turned=true;
            }else{
                buildingModeSet();
            }
        }
    }

    void useGlory (int gloryID){
        Tile gloryTile;
        String two;
        SceneObject gloryObject = SceneEntities.getNearest(gloryID);
        two=Widgets.get(1188).getChild(3).getText();


        while(two==null){
            String text;

            if (gloryObject.isOnScreen()==true){
                gloryObject.click(false);
                sleep(500,600);
                gloryObject.interact("Rub");
                sleep(300,500);
                text=Widgets.get(1188).getChild(5).getText();
                sleep(300,500);
                if(text!=null){
                    Widgets.get(1188).getChild(2).interact("Continue");
                }


            }   else{
                gloryTile = gloryObject.getLocation();
                walkNext(gloryTile,15);
                sleep(3000,4000);
            }

        }




    }





    //Armor Stand------------------------------------------------------------------------
    void rotateCameraToArmorStand(){
        SceneObject armorStandObject = SceneEntities.getNearest(armorStandID);
        Camera.turnTo(armorStandObject);
    }

    void useArmorOnStand(int useArmor){
        String one,two;
        one=Widgets.get(1186).getChild(1).getText();
        two=Widgets.get(1188).getChild(5).getText();
        while(one==null && two==null){
            SceneObject armorStandObject = SceneEntities.getNearest(armorStandID);
            Inventory.selectItem(useArmor);
            sleep(200);
            armorStandObject.click(true);
            sleep(3000);
            one=Widgets.get(1186).getChild(1).getText();
            two=Widgets.get(1188).getChild(5).getText();

        }


    }

    void repairArmorDio(int useArmor){

        String text,text2;

        text=Widgets.get(1186).getChild(1).getText();
        text2=Widgets.get(1188).getChild(5).getText();
        if(text!=null){
            Widgets.get(1186).getChild(7).interact("Continue");
            sleep(500,800);
            Widgets.get(1188).getChild(2).interact("Continue");


        }else if(text2 != null){
            Widgets.get(1188).getChild(11).interact("Continue");  //11 for ok 13 for cancel!!!!!!!!!!!!!
            sleep(500,800);
        }
    }




    //Walking------------------------------------------------------
    public static void walkPath(Tile[] tiles, int i){

        TilePath t = Walking.newTilePath(tiles);

        while(Calculations.distance(Walking.getDestination(), t.getEnd()) >= i)
            walkNext(t.getNext(), i);

    }

    public static void walkReverse(Tile[] tiles, int i){

        TilePath t = Walking.newTilePath(tiles).reverse();

        while(Calculations.distance(Walking.getDestination(), t.getEnd()) >= i)
            walkNext(t.getNext(), i);

    }

    public static void walkNext(Tile t, int i) {


        if (!Players.getLocal().isMoving() || Calculations.distanceTo(Walking.getDestination()) <= i)
            if(t != null && t.distanceTo() < 100) {

                Walking.walk(t.randomize(1, 1));
                sleep(800, 1000);

            }

    }






    //START: Code generated using Enfilade's Easel
    private final Color color1 = new Color(0, 0, 0);
    private final Color color2 = new Color(255, 255, 255);

    private final BasicStroke stroke1 = new BasicStroke(1);

    private final Font font1 = new Font("Arial", 0, 16);
    private final Font font2 = new Font("Arial", 0, 10);

    public void onRepaint(Graphics g1) {
        Graphics2D g = (Graphics2D)g1;
        g.setColor(color1);
        g.fillRect(9, 397, 488, 110);
        g.setStroke(stroke1);
        g.drawRect(9, 397, 488, 110);
        g.setFont(font1);
        g.setColor(color2);
        g.drawString("gg's Armor Repairererer!", 18, 415);
        g.setFont(font2);
        g.drawString("Money Made: ", 22, 449);
        g.drawString("Armors Repaired:", 22, 475);
        g.drawString("Money Per Hour:", 234, 449);
    }
    //END: Code generated using Enfilade's Easel
}