package BinPacking.Data.LogicUI;

import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import BinPacking.Logic.PackingStrategy.PackingStrategy;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Flexscan2243 on 24.01.2017.
 * The Bin is a tree-like structure. Every bin contains a list of its Bin children.
 */
public class Bin extends Cuboid {
    public enum BinState {EMPTY, FULL}
    private BinState binState = BinState.EMPTY;
    public enum BinType {ROOT,A,B,C,D}
    private BinType binType;
    private static int rootBinCounter = 0; //< Each box gets this cid. The cid indicates the bin assigned to box.
    private Bin parent;
    private List<Bin> children;

    /**
     * Public constructor used to construct a new Root Bin
     * @param length
     * @param width
     * @param height
     */
    public Bin(double length, double width, double height){
        this(length,width,height, Color.GREY);
    }

    /**
     * Public constructor used to construct a new Root Bin. Additional parameter: color of graphic model edges.
     * @param length
     * @param width
     * @param height
     * @param color
     */
    public Bin(double length, double width, double height, Color color){
        this(0,0,0,length,width,height,BinType.ROOT);
        rootBinCounter++;

        createGraphicModel(length, width, height, color); //< Appearance part of code
    }

    /**
     * Main constructor used to construct a new Root Bin or Bin children.
     * @param x Bin x
     * @param y Bin y
     * @param z Bin z
     * @param length Bin length
     * @param width Bin width
     * @param height Bin height
     * @param binType Space type needed to perform box insertion with alternate configurations.
     */
    private Bin(double x, double y, double z, double length, double width, double height, BinType binType) {
        super(x, y, z, length, width, height);
        this.binType = binType;
        setCid(rootBinCounter);
        children = new LinkedList<>();
    }

    /**
     * addChild
     * @param bin
     */
    private void addChild(Bin bin) {
        bin.parent = this;
        bin.setCid(getCid());
        children.add(bin);
    }

    /**
     * Every box insertion creates 12 new bins (3 subspaces in XYZ direction in 4 alternate versions). Each triplet has its BinType (A,B,C or D) exclusive to rest.
     * If one BinType is chosen the rest must be removed. The BinTypes represent new spaces created after box insertion.
     * @param box
     */
    public void createChildren(Box box) {
        if (getLength() - box.getLength() > 0) {
            addChild(new Bin(getX() + box.getLength(), getY(), getZ(), getLength() - box.getLength(), getWidth(), getHeight(), BinType.A));
            addChild(new Bin(getX() + box.getLength(), getY(), getZ(), getLength()- box.getLength(), box.getWidth(), getHeight(), BinType.B));
            addChild(new Bin(getX() + box.getLength(), getY(), getZ(), getLength() - box.getLength(), getWidth(), box.getHeight(), BinType.C));
            addChild(new Bin(getX() + box.getLength(), getY(), getZ(), getLength()- box.getLength(), box.getWidth(), box.getHeight(), BinType.D));
        }
        if (getWidth() - box.getWidth() > 0) {
            addChild(new Bin(getX(), getY() + box.getWidth(), getZ(), box.getLength(), getWidth() - box.getWidth(), getHeight(), BinType.A));
            addChild(new Bin(getX(), getY()+ box.getWidth(), getZ(), getLength(), getWidth() - box.getWidth(), getHeight(), BinType.B));
            addChild(new Bin(getX(), getY() + box.getWidth(), getZ(), box.getLength(), getWidth() - box.getWidth(), box.getHeight(), BinType.C));
            addChild(new Bin(getX(), getY() + box.getWidth(), getZ(), getLength(), getWidth() - box.getWidth(), box.getHeight(), BinType.D));
        }
        if (getHeight() - box.getHeight() > 0) {
            addChild(new Bin(getX(), getY(), getZ() + box.getHeight(), box.getLength(), box.getWidth(), getHeight() - box.getHeight(), BinType.A));
            addChild(new Bin(getX(), getY(), getZ() + box.getHeight(), box.getLength(), box.getWidth(), getHeight() - box.getHeight(), BinType.B));
            addChild(new Bin(getX(), getY(), getZ() + box.getHeight(), getLength(), getWidth(), getHeight() - box.getHeight(), BinType.C));
            addChild(new Bin(getX(), getY(), getZ() + box.getHeight(), getLength(), getWidth(), getHeight() - box.getHeight(), BinType.D));
        }
    }

    /**
     * If the box fits to the one of 3 subspaces forming a BinType, eg. A rest of them will be removed (B,C,D)
     */
    public  void removeAltChildren() {
        if (parent != null)
            parent.children.removeIf((Bin bin) -> bin.binType != binType);
    }

    /**
     * Assign the Bin to the Box. The bin will be no longer available to assignation.
     * @param box
     */
    public void reserveBin(Box box){
        box.setCoordinates(getX(), getY(), getZ());
        box.setCid(getCid());
        setBinState(BinState.FULL);
    }

    /**
     * Search method dependent of used PackingStrategy.
     * @param packingStrategy
     * @param box
     * @return
     */
    public Bin search(PackingStrategy packingStrategy, Box box) {
        return packingStrategy.search(this, box);
    }

    public List<Bin> getBinChildren() {
        return children;
    }

    public void setBinState(BinState binState) {
        this.binState = binState;
    }

    public BinType getBinType() {
        return binType;
    }

    public BinState getBinState() {
        return binState;
    }

    public static void setRootBinCounter(int rootBinCounter) {
        Bin.rootBinCounter = rootBinCounter;
    }

    public static void resetRootBinCounter(){
        Bin.rootBinCounter = 0;
    }


    //Appearance
    private final static int  thickness = 10;
    private final static double[][] EDGES_SIZES = {{0,0,1}, {0,0,1}, {0,0,1}, {0,0,1}, {1,0,0}, {1,0,0}, {1,0,0}, {1,0,0}, {0,1,0}, {0,1,0}, {0,1,0}, {0,1,0}};
    private final static double[][] EDGES_POSITIONS = {{0,0,0}, {1,0,0}, {0,1,0}, {1,1,0}, {0,0,0}, {0,1,0}, {0,0,1}, {0,1,1}, {0,0,0}, {1,0,0}, {0,0,1}, {1,0,1}};
    private final static double[][] EDGES_SHIFTS = {{1,1,0},{0,1,0},{1,0,0},{0,0,0},{0,1,1},{0,0,1},{0,1,0},{0,0,0},{1,0,1},{0,0,1},{1,0,0},{0,0,0}};
    private final PhongMaterial material = new PhongMaterial();
    private javafx.scene.shape.Box[] edges = new javafx.scene.shape.Box[12];
    private final static double SHIFT_RATIO = 0.5;

    @Override
    public void scale(double scale){
        for(int i = 0 ; i < EDGES_SHIFTS.length; i++) {
            edges[i].setWidth(edges[i].getWidth() * scale);
            edges[i].setHeight(edges[i].getHeight() * scale);
            edges[i].setDepth(edges[i].getDepth() * scale);
            edges[i].setTranslateX(edges[i].getTranslateX() * scale);
            edges[i].setTranslateY(edges[i].getTranslateY() * scale);
            edges[i].setTranslateZ(edges[i].getTranslateZ() * scale);
        }
    }

    @Override
    public void createGraphicModel(double length, double width, double height, Color color){
        material.setSpecularColor(Color.DARKGREY);
        material.setDiffuseColor(color);
        for(int i = 0 ; i < EDGES_SHIFTS.length; i++) {
            edges[i] = new javafx.scene.shape.Box(EDGES_SIZES[i][0] * length  + thickness, EDGES_SIZES[i][1] * (width ) + thickness, EDGES_SIZES[i][2] * (height ) + thickness);
            edges[i].setTranslateX(EDGES_POSITIONS[i][0] * length  + SHIFT_RATIO * edges[i].getWidth() - EDGES_SHIFTS[i][0] * thickness);
            edges[i].setTranslateY(EDGES_POSITIONS[i][1] * width  + SHIFT_RATIO * edges[i].getHeight() - EDGES_SHIFTS[i][1] * thickness);
            edges[i].setTranslateZ(EDGES_POSITIONS[i][2] * height + SHIFT_RATIO * edges[i].getDepth() - EDGES_SHIFTS[i][2] * thickness);
            edges[i].setMaterial(material);
            getChildren().add(edges[i]);
        }
    }
}