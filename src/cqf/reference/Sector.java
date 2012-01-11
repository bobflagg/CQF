package cqf.reference;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;

/**
 *
 * @author Bob Flagg <bob@calcworks.net>
 */
public class Sector {
    private static Map<String, Sector> registry = new HashMap<String, Sector>();
    private static TreeModel referenceTreeModel;

    private static void register(Sector sector) {
        if (registry.containsValue(sector)) return;
        //System.out.println(question.getField());
        registry.put(sector.getName(), sector);
    }

    public static Sector lookup(String name) throws IllegalArgumentException {
        Sector sector = registry.get(name);
        if (sector == null) throw new IllegalArgumentException("Can't find sector for name "+name);
        return sector;
    }

    public static Collection<Sector> sectors() {
        return registry.values();
    }

    public static TreeModel getReferenceTreeModel() {
        if (referenceTreeModel == null) buildReferenceTreeModel();
        return referenceTreeModel;
    }

    public static void buildReferenceTreeModel() {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Reference Entities");
        referenceTreeModel = new DefaultTreeModel(root);
        for (Sector sector:sectors()) {
            DefaultMutableTreeNode sectorNode = new DefaultMutableTreeNode(sector);
            root.add(sectorNode);
            for (Entity entity: sector.getEntities()) {
                DefaultMutableTreeNode entityNode = new DefaultMutableTreeNode(entity);
                sectorNode.add(entityNode);
            }
        }
    }

    private String name;
    private List<Entity> entities;

    public Sector(String name) {
        this.name = name;
        entities = new ArrayList<Entity>();
        register(this);
    }

    public String getName() {
        return name;
    }

    private List<Entity> getEntities() {
        return entities;
    }

    public void add(Entity entity) {
        entities.add(entity);
    }

    public void remove(Entity entity) {
        entities.remove(entity);
    }

    public Entity getEntity(int i) {
        return entities.get(i);
    }

    @Override
    public String toString() {
        //StringBuffer display = new StringBuffer(name+"\n");
        //for (Entity entity:entities) display.append("\t"+entity.toString()+"\n");
        //return display.append("\n\n").toString();
        return name;

    }
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Sector other = (Sector) obj;
        if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + (this.name != null ? this.name.hashCode() : 0);
        return hash;
    }


}
